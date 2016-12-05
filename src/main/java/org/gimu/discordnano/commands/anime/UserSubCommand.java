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

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import org.gimu.discordnano.commands.AbstractSubCommand;
import org.gimu.discordnano.commands.SubCommand;
import org.gimu.discordnano.lib.EmbedFieldListBuilder;
import org.gimu.discordnano.lib.MessageUtil;
import org.gimu.discordnano.lib.NanoLogger;
import org.gimu.discordnano.util.HTTPUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.Optional;

@SubCommand(
        mainCommandAlias = "mal",
        alias = {"user"},
        description = "Fetch MAL user information",
        usage = "mal user <username|id>"
)
public class UserSubCommand extends AbstractSubCommand {

    public UserSubCommand(String description, String usage) {
        super(description, usage);
    }

    public Optional execute(User author, Message message, String[] args) throws IllegalArgumentException {
        if (args.length == 0) {
            throw new IllegalArgumentException();
        }

        EmbedFieldListBuilder builder = new EmbedFieldListBuilder();
        String content = "Displaying MyAnimeList profile";

        try {
            String parameters = "u=" + args[0];
            InputStream response = HTTPUtil.sendGet("https://myanimelist.net/malappinfo.php", parameters);

            // XML parsing
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(response);
            doc.getDocumentElement().normalize();

            NodeList nodes = doc.getElementsByTagName("myinfo");
            Node node = nodes.item(0);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;

                builder.append("Username", element.getElementsByTagName("user_name").item(0).getTextContent());
                builder.append("Watching", element.getElementsByTagName("user_watching").item(0).getTextContent());
                builder.append("Completed", element.getElementsByTagName("user_completed").item(0).getTextContent());
                builder.append("Onhold", element.getElementsByTagName("user_onhold").item(0).getTextContent());
                builder.append("Dropped", element.getElementsByTagName("user_dropped").item(0).getTextContent());
                builder.append("Planned", element.getElementsByTagName("user_plantowatch").item(0).getTextContent());
                builder.append("Wasted", element.getElementsByTagName("user_days_spent_watching").item(0).getTextContent() + " Days");
            }
        } catch (Exception e) {
            content = "Couldn't fetch MyAnimeList profile.";
            NanoLogger.error(e.getMessage());
        }

        Message response = MessageUtil.buildFramedMessage(author, content, builder.build(), true);
        return Optional.of(response);
    }
}
