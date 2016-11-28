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

package org.gimu.discordnano.commands.single;

import net.dv8tion.jda.core.entities.Message;
import org.gimu.discordnano.commands.AbstractCommand;
import org.gimu.discordnano.commands.MainCommand;
import org.gimu.discordnano.lib.EmbedFieldListBuilder;
import org.gimu.discordnano.lib.MessageUtil;
import org.gimu.discordnano.lib.NanoLogger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.util.Optional;
import java.util.Random;

@MainCommand(
        alias = "rule34",
        description = "Fetch image from rule34.xxx",
        usage = "rule34 <query>"
)
public class Rule34Command extends AbstractCommand {

    public Rule34Command(String description, String usage, String alias) {
        super(description, usage, alias);
    }

    public Optional execute(Message message, String[] args) throws IllegalArgumentException {
        String content = "Displaying image from rule34.xxx";
        String imageUrl = null;

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
            NanoLogger.error(e.getMessage());
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

            imageUrl = "https:" + url;
        } catch (Exception e) {
            content = "I couldn't find anything.";
            NanoLogger.error(e.getMessage());
        }

        Message response = MessageUtil.frameMessage(content, imageUrl, true);
        return Optional.of(response);
    }
}
