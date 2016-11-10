/*
 *  Copyright 2016 Son Nguyen <mail@gimu.org>
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.gimu.discordnano.commands.mal;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.gimu.discordnano.DiscordNano;
import org.gimu.discordnano.commands.AbstractSubCommand;
import org.gimu.discordnano.commands.SubCommand;
import org.gimu.discordnano.lib.NanoMessage;
import org.gimu.discordnano.util.HTTPUtil;
import org.gimu.discordnano.util.MALInfo;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@SubCommand(
        mainCommandAlias = "mal",
        alias = {"anime"},
        description = "",
        usage = ""
)
public class AnimeSubCommand extends AbstractSubCommand {

	private static long lastExecution = 0L;
	private static final String MAL_USER = DiscordNano.config.getString("mal_user");
	private static final String MAL_PASS = DiscordNano.config.getString("mal_pass");

	private static HashMap<Integer, MALInfo> animeMap = new HashMap<>();

    public Optional execute(NanoMessage message, String[] args) throws IllegalArgumentException {
		if (args.length == 0) {
			throw new IllegalArgumentException();
		}

		// Viewing
		String index = args.length >= 2 ? args[1] : "";
		if (args[0].toLowerCase().equals("view")) {
			if (index.length() == 0) {
				return Optional.of(viewRecent());
			} else {
				return Optional.of(viewEntry(index));
			}
		}

		// Searching
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < args.length-1; i++) {
			sb.append(args[i] + " ");
		}
		sb.append(args[args.length-1]);
		return Optional.of(searchMAL(sb.toString()));
	}

	private String viewRecent() {
        if (animeMap.isEmpty()) {
			return "There are no recent queries to show!";
		}
        StringBuilder result = new StringBuilder();
        int i = 0;
        for (Map.Entry<Integer, MALInfo> entry : animeMap.entrySet()) {
            result.append("`" + i + "` " + entry.getValue().title);
            if (entry.getValue().english.length() != 0) result.append(" / " + entry.getValue().english);
            result.append("\n");
            i++;
        }
        return result.toString();
	}

	private String viewEntry(String index) {
		MALInfo entry = animeMap.get(Integer.parseInt(index));
		if (!NumberUtils.isNumber(index) || entry == null) {
			return "Not a valid index or no recent queries saved!";
		}
        return (entry.title
                + (entry.english.length() != 0 ? " / " + entry.english : "")
                + "**\n**Type:** " + entry.type
                + " **| Episodes:** " + entry.episodes
                + " **| Status:** " + entry.status
                + " **| Score:** " + entry.score
                + "**\n\n" + entry.synopsis
                + "\n\n**<http://www.myanimelist.net/anime/" + entry.id + ">**");
	}

	private String searchMAL(String query) {
		// MALCommand/Manga search
		if (MAL_USER == "" || MAL_PASS == "") {
			return "MAL login not configured.";
		}
        if (lastExecution == 0) {
            lastExecution = System.currentTimeMillis();
        } else {
            long currentExecution = System.currentTimeMillis();
            long time = (currentExecution - lastExecution) / 1000;
            if (time < 5) {
                return "Please wait 5 seconds to submit another query.";
            }
            lastExecution = currentExecution;
        }

        try {
            String parameters = "q=" + URLEncoder.encode(query, "UTF-8");
            InputStream response = HTTPUtil.sendAuthGet("http://myanimelist.net/api/anime/search.xml", parameters, MAL_USER, MAL_PASS);

            String id = "", title = "", english = "", episodes = "", score = "", type = "", status = "", synopsis = "";

            // XML parsing
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(response);
            doc.getDocumentElement().normalize();
            NodeList nodes = doc.getElementsByTagName("entry");

            StringBuilder result = new StringBuilder();
            // Reset recent map
            if (nodes.getLength() > 0) animeMap.clear();

            int limit = (nodes.getLength() <= 21) ? nodes.getLength() : 21;

            for (int i = 0; i < limit; i++) {
                Node node = nodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    id = element.getElementsByTagName("id").item(0).getTextContent();
                    title = element.getElementsByTagName("title").item(0).getTextContent();
                    english = element.getElementsByTagName("english").item(0).getTextContent();
                    episodes = element.getElementsByTagName("episodes").item(0).getTextContent();
                    score = element.getElementsByTagName("score").item(0).getTextContent();
                    type = element.getElementsByTagName("type").item(0).getTextContent();
                    status = element.getElementsByTagName("status").item(0).getTextContent();
                    synopsis = element.getElementsByTagName("synopsis").item(0).getTextContent();

                    result.append("`" + i + "` " + title);
                    if (english.length() != 0) result.append(" / " + english);

                    if (synopsis.length() > 0) {
                        result.append("\n");
                        if (synopsis.length() > 500) {
                            synopsis = synopsis.substring(0, 500) + "...";
                        }
                        synopsis = StringEscapeUtils.unescapeHtml4(synopsis);
                        synopsis = synopsis.replaceAll("<br[^>]*>", " ").replaceAll("\\[/?i\\]", "*").replaceAll("\\[/?b\\]", "**").replaceAll("\\[([^\\]]+)\\]", "");
                    }

                    animeMap.put(i, new MALInfo(id, title, english, episodes, score, type, status, synopsis));
                }
            }

            return "__Query result__\n\n" + result.toString() + "\nList temporarily saved. Write `" + DiscordNano.prefix + "mal anime view <index>` to examine an entry.";
        } catch (Exception e) {
            //System.out.println(e.getMessage());
            return "I couldn't find an entry fitting that phrase.";
        }
	}
}
