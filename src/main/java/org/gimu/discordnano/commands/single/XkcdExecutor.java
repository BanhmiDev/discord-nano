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

import org.gimu.discordnano.commands.NanoExecutor;
import org.gimu.discordnano.util.JSON;
import org.gimu.discordnano.util.NanoMessage;
import org.json.JSONObject;
import java.util.Random;

public class XkcdExecutor extends NanoExecutor {

    private String[] triggers = {"xkcd"};
    private String description = "Display xkcd comic";
    private String usage = "[number|latest]";

    @Override
    public void respond(NanoMessage message, String[] args) {

        try {
            JSONObject latestJSON = JSON.readJsonFromUrl("http://xkcd.com/info.0.json");

            if (latestJSON != null) {
                int min = 500;
                int max = latestJSON.getInt("num");

                int rand;
                if (args.length > 0) {
                    int input;
                    try {
                        input = Integer.valueOf(args[0]);

                        if (input > max || input < 1) {
                            message.reply("xkcd does not have a comic for that number.");
                        }

                        rand = input;
                    } catch (NumberFormatException e) {
                        if (args[0].equalsIgnoreCase("latest")) {
                            rand = max;
                        } else {
                            message.reply("You didn't enter a valid number.");
                            return;
                        }
                    }
                } else {
                    rand = min + new Random().nextInt(max - min);
                }

                JSONObject randJSON = JSON.readJsonFromUrl(String.format("http://xkcd.com/%d/info.0.json", rand));

                if (randJSON != null) {
                    String builder = "**xkcd**: " + randJSON.getString("title") + "\n" +
                            "**No**: " + randJSON.getInt("num") + "\n" +
                            "**<" + randJSON.getString("img").replaceAll("\\\\/", "/") + ">**";

                    message.reply(builder);
                    return;
                }
            }

            message.reply("Unable to get xkcd comic.");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
