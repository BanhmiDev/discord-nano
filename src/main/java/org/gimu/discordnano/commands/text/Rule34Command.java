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
package org.gimu.discordnano.commands.text;

import org.gimu.discordnano.commands.NanoExecutor;
import org.gimu.discordnano.util.NanoMessage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import java.util.Random;

public class Rule34Command extends NanoExecutor {

    public String[] triggers = {"rule34"};
    public String description = "Fetches image from rule34.xxx";
    public String usage = "<rock|paper|scissors>";

    public void respond(NanoMessage message, String[] args) throws IllegalArgumentException {
        if (args.length == 0) {
            throw new IllegalArgumentException();
        }

        String tag = "";
        try {
            for (String s : args) {
                if (s.equals("_rule")) continue;
                if (tag.equals("")) {
                    tag += ("&tags=" + s);
                } else {
                    tag += ("+" + s);
                }
            }
        } catch (Exception e) {
            // ...
        }

        try {
            String xml = "http://rule34.xxx/index.php?page=dapi&s=post&q=index" + tag;
            Document document = Jsoup.connect(xml).parser(Parser.xmlParser()).get();
            Elements posts = document.getElementsByTag("post");

            int rand = new Random().nextInt(posts.size());

            Element target = posts.get(rand);

            String url;
            Attributes att = target.attributes();
            Attribute att2 = att.asList().get(2);

            url = att2.getValue();

            message.reply("http:" + url);
        } catch (Exception e) {
            message.reply("I couldn't find anything.");
        }
    }
}
