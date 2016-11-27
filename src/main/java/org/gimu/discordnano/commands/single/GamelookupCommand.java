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

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import net.dv8tion.jda.core.entities.Message;
import org.apache.commons.lang.StringUtils;
import org.gimu.discordnano.DiscordNano;
import org.gimu.discordnano.commands.AbstractCommand;
import org.gimu.discordnano.commands.MainCommand;
import org.gimu.discordnano.lib.NanoLogger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Optional;
import java.util.StringJoiner;

@MainCommand(
        alias = {"gamelookup"},
        description = "Look up a video-game",
        usage = "gamelookup <query>"
)
public class GamelookupCommand extends AbstractCommand {

    public GamelookupCommand(String description, String usage) {
        super(description, usage);
    }

    public Optional execute(Message message, String[] args) throws IllegalArgumentException {
        if (args.length == 0) {
           throw new IllegalArgumentException();
        }

        try {
            String query = StringUtils.join(args, "+");

            HttpResponse<JsonNode> httpResponse = Unirest.get("https://videogamesrating.p.mashape.com/get.php?count=5&game=" + query)
                    .header("X-Mashape-Key", DiscordNano.config.getString("x_mashape_key"))
                    .header("Accept", "application/json")
                    .asJson();

            JSONArray jsa = httpResponse.getBody().getArray();

            if (jsa.length() == 0) {
                return Optional.of("I couldn't find a game with that title.");
            }

            JSONObject jso = new JSONObject(jsa.getJSONObject(0).toString()) {
                @Override
                public String getString(String key) throws JSONException {
                    String s = super.getString(key);
                    return s == null || s.isEmpty() ? "Unknown" : s;
                }
            };

            StringJoiner response = new StringJoiner("\n");

            response.add("**Title**: " + jso.getString("title"));
            response.add("**Publisher**: " + jso.getString("publisher"));
            response.add("**Rating**: " + jso.getString("score"));
            response.add("\n" + jso.getString("short_description"));
            response.add("\n**" + jso.getString("thumb") + "**");

            return Optional.of(response.toString());
        } catch (Exception e) {
            NanoLogger.error(e.getMessage());
        }
        return Optional.empty();
    }
}
