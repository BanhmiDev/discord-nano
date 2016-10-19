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
package org.gimu.discordnano.commands.media;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.gimu.discordnano.DiscordNano;
import org.gimu.discordnano.commands.AbstractCommand;
import org.gimu.discordnano.util.MALInfo;
import org.gimu.discordnano.util.NanoMessage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class AnimeCommand extends AbstractCommand {

	private String[] triggers = {"anime"};
	private String description = "Search and display anime information";
	private String usage = "<keyword>/<view> [index]";

	private long lastExecution = 0L;
	private static final String MAL_USER = DiscordNano.config.getString("mal_user");
	private static final String MAL_PASS = DiscordNano.config.getString("mal_pass");

	private HashMap<Integer, MALInfo> animeMap = new HashMap<>();

	NanoMessage message;

	@Override
	public void respond(NanoMessage message, String[] args) {
		if (args.length == 0) {
			message.reply(getUsageText());
			return;
		}

		this.message = message;

		String index = args.length >= 2 ? args[1] : "";

		switch (args[0].toLowerCase()) {
			case "view":
				if (index.length() == 0) {
					viewRecent();
				} else {
					viewEntry(index);
				}
				break;
			default:
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < args.length-1; i++) {
					sb.append(args[i] + " ");
				}
				sb.append(args[args.length-1]);
				searchMAL(sb.toString());
				break;
		}
	}

	private void viewRecent() {
        if (animeMap.isEmpty()) {
            message.reply("There are no recent queries to show!");
        } else {
            StringBuilder result = new StringBuilder();
            int i = 0;
            for (Map.Entry<Integer, MALInfo> entry : animeMap.entrySet()) {
                result.append("`" + i + "` " + entry.getValue().title);
                if (entry.getValue().english.length() != 0) result.append(" / " + entry.getValue().english);
                result.append("\n");
                i++;
            }
            message.reply(result.toString());
        }
	}

	private void viewEntry(String index) {
		MALInfo entry = animeMap.get(Integer.parseInt(index));
		if (!NumberUtils.isNumber(index) || entry == null) {
			message.reply("Not a valid index or no recent queries saved!");
		} else {
            message.reply(entry.title
                    + (entry.english.length() != 0 ? " / " + entry.english : "")
                    + "**\n**Type:** " + entry.type
                    + " **| Episodes:** " + entry.episodes
                    + " **| Status:** " + entry.status
                    + " **| Score:** " + entry.score
                    + "**\n\n" + entry.synopsis
                    + "\n\n**<http://www.myanimelist.net/anime/" + entry.id + ">**");
		}
	}

	private void searchMAL(String query) {
		// MALCommand/Manga search
		if (MAL_USER == "" || MAL_PASS == "") {
			message.reply("MAL login not configured.");
		} else {
			if (lastExecution == 0) {
				lastExecution = System.currentTimeMillis();
			} else {
				long currentExecution = System.currentTimeMillis();
				long time = (currentExecution - lastExecution) / 1000;
				if (time < 5) {
					message.reply("Please wait 5 seconds to submit another query.");
					return;
				}
				lastExecution = currentExecution;
			}
			message.getChannel().sendTyping();
			try {
				String encodedQuery = URLEncoder.encode(query, "UTF-8");
				String api = "http://myanimelist.net/api/anime/search.xml?q=" + encodedQuery;
				URL url = new URL(api);
				URLConnection uc = url.openConnection();
				String userpass = MAL_USER + ":" + MAL_PASS;
				String basicAuth = "Basic " + new String(new Base64().encode(userpass.getBytes()));
				uc.setRequestProperty("Authorization", basicAuth);

				String id = "", title = "", english = "", episodes = "", score = "", type = "", status = "", synopsis = "";

				// XML parsing
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(uc.getInputStream());
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

				message.reply("__Query result__\n\n" + result.toString() + "\nList temporarily saved. Write `" + DiscordNano.prefix + "anime view <index>` to examine an entry.");
			} catch (Exception e) {
				message.reply("I couldn't find an entry fitting that phrase.");
				System.out.println(e.getMessage());
			}
		}
	}
}
