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
import org.gimu.discordnano.lib.NanoLogger;
import org.gimu.discordnano.util.JSONUtil;
import org.json.JSONObject;

import java.util.Optional;
import java.util.Random;

@MainCommand(
        alias = {"xkcd"},
        description = "Display xkcd comic",
        usage = "xkcd [number|latest]"
)
public class XkcdCommand extends AbstractCommand {

    public XkcdCommand(String description, String usage) {
        super(description, usage);
    }

    public Optional execute(Message message, String[] args) {
        String response = "";
        JSONObject latestJSON = null;
        try {
            latestJSON = JSONUtil.readJsonFromUrl("http://xkcd.com/info.0.json");
        } catch (Exception e) {
            NanoLogger.error(e.getMessage());
            return Optional.of("Unable to fetch xkcd comic.");
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
                        response = "xkcd does not have a comic for that number.";
                    }

                    rand = input;
                } catch (NumberFormatException e) {
                    if (args[0].equalsIgnoreCase("latest")) {
                        rand = max;
                    } else {
                        response = "You didn't enter a valid number.";
                    }
                }
            } else {
                rand = min + new Random().nextInt(max - min);
            }

            JSONObject randJSON = JSONUtil.readJsonFromUrl(String.format("http://xkcd.com/%d/info.0.json", rand));

            if (randJSON != null) {
                response = randJSON.getString("title") + "\nNumber: **" + randJSON.getInt("num") + "**\n\n" + randJSON.getString("img").replaceAll("\\\\/", "/");
            }
        }

        return Optional.of(response);
    }
}
