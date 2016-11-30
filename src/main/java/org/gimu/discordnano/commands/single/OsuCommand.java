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
import org.gimu.discordnano.DiscordNano;
import org.gimu.discordnano.commands.AbstractCommand;
import org.gimu.discordnano.commands.MainCommand;
import org.gimu.discordnano.lib.EmbedFieldListBuilder;
import org.gimu.discordnano.lib.MessageUtil;
import org.gimu.discordnano.lib.NanoLogger;
import org.gimu.discordnano.util.HTTPUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;

@MainCommand(
        alias = "osu",
        description = "Fetch osu! profile",
        usage = "osu <user>"
)
public class OsuCommand extends AbstractCommand {

    public OsuCommand(String description, String usage, String alias) {
        super(description, usage, alias);
    }

    public Optional execute(User author, Message message, String[] args) throws IllegalArgumentException {
        if (args.length == 0) {
            throw new IllegalArgumentException();
        }

        EmbedFieldListBuilder builder = new EmbedFieldListBuilder();
        String content = "Displaying osu! profile";
        String api = "https://osu.ppy.sh/api/get_user";
        InputStream stream;
        try {
            stream = HTTPUtil.sendGet(api, "k=" + DiscordNano.config.getString("osu_api_key") + "&u=" + args[0]);

            BufferedReader streamReader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            StringBuilder responseStrBuilder = new StringBuilder();

            String inputStr = streamReader.readLine();
            while (inputStr != null) {
                responseStrBuilder.append(inputStr);
                inputStr = streamReader.readLine();
            }
            JSONObject jsonResponse = new JSONArray(responseStrBuilder.toString()).getJSONObject(0);
            builder.append("Username", jsonResponse.getString("username"));
            builder.append("Play count", jsonResponse.getString("playcount"));
            builder.append("Accuracy: ", jsonResponse.getString("accuracy"));
            builder.append("Level: ", jsonResponse.getString("level"));
            builder.append("Country: ", jsonResponse.getString("country"));
            builder.append("Country_rank: ", jsonResponse.getString("pp_country_rank"));

        } catch (Exception e) {
            NanoLogger.error(e.getMessage());
            return Optional.of("osu! Profile not found.");
        }

        Message response = MessageUtil.frameMessage(author, content, builder.build(), true);
        return Optional.of(response);
    }
}
