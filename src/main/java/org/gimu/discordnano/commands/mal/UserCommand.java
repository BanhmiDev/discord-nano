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

import org.gimu.discordnano.DiscordNano;
import org.gimu.discordnano.util.APIUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.net.URLEncoder;

public class UserCommand {

    private static final String MAL_USER = DiscordNano.config.getString("mal_user");
    private static final String MAL_PASS = DiscordNano.config.getString("mal_pass");

    public static String respond(String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException();
        }

        StringBuilder sb = new StringBuilder();

        try {
            String parameters = "u=" + args[1];
            InputStream response = APIUtil.sendGet("http://myanimelist.net/malappinfo.php", parameters);

            // XML parsing
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(response);
            doc.getDocumentElement().normalize();

            NodeList nodes = doc.getElementsByTagName("myinfo");
            Node node = nodes.item(0);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;

                sb.append("Username: " + element.getElementsByTagName("user_name").item(0).getTextContent() + "\n");
                sb.append("Watching: " + element.getElementsByTagName("user_watching").item(0).getTextContent() + "\n");
                sb.append("Completed: " + element.getElementsByTagName("user_completed").item(0).getTextContent() + "\n");
                sb.append("Onhold: " + element.getElementsByTagName("user_onhold").item(0).getTextContent() + "\n");
                sb.append("Dropped: " + element.getElementsByTagName("user_dropped").item(0).getTextContent() + "\n");
                sb.append("Planned: " + element.getElementsByTagName("user_plantowatch").item(0).getTextContent() + "\n");
                sb.append("Wasted: " + element.getElementsByTagName("user_days_spent_watching").item(0).getTextContent() + " Days\n");
            }
        } catch (Exception e) {
            // TODO
        }

        return sb.toString();
    }
}
