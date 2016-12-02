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
import net.dv8tion.jda.core.entities.User;
import org.gimu.discordnano.commands.AbstractCommand;
import org.gimu.discordnano.commands.MainCommand;
import org.gimu.discordnano.lib.EmbedFieldListBuilder;
import org.gimu.discordnano.lib.MessageUtil;
import org.gimu.discordnano.lib.NanoLogger;
import org.gimu.discordnano.util.JSONUtil;
import org.json.JSONObject;

import java.util.Optional;
import java.util.Random;

@MainCommand(
        alias = "xkcd",
        description = "Display xkcd comic",
        usage = "xkcd [number|latest]"
)
public class XkcdCommand extends AbstractCommand {

    public XkcdCommand(String description, String usage, String alias) {
        super(description, usage, alias);
    }

    public Optional execute(User author, Message message, String[] args) {
        EmbedFieldListBuilder builder = new EmbedFieldListBuilder();
        String content = "Displaying xkcd comic";
        String imageUrl = null;

        JSONObject latestJSON = null;
        try {
            latestJSON = JSONUtil.readJsonFromUrl("https://xkcd.com/info.0.json");
        } catch (Exception e) {
            content = "Unable to fetch xkcd comic.";
            NanoLogger.error(e.getMessage());
        }

        if (latestJSON != null) {
            int min = 500;
            int max = latestJSON.getInt("num");

            int rand = 0;
            if (args.length > 0) {
                int input;
                try {
                    input = Integer.valueOf(args[0]);

                    if (input > max || input < 1) {
                        content = "xkcd does not have a comic for that number.";
                    }

                    rand = input;
                } catch (NumberFormatException e) {
                    if (args[0].equalsIgnoreCase("latest")) {
                        rand = max;
                    } else {
                        content = "You didn't enter a valid number.";
                    }
                }
            } else {
                rand = min + new Random().nextInt(max - min);
            }

            JSONObject randJSON = JSONUtil.readJsonFromUrl(String.format("https://xkcd.com/%d/info.0.json", rand));
            if (randJSON != null) {
                builder.append("Title", randJSON.getString("title"));
                builder.append("Number", Integer.toString(randJSON.getInt("num")));
                imageUrl = randJSON.getString("img").replaceAll("\\\\/", "/");
            }
        }

        Message response = MessageUtil.buildFramedMessage(author, content, builder.build(), imageUrl, true);
        return Optional.of(response);
    }
}
