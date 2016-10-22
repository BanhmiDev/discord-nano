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

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import org.apache.commons.lang3.StringUtils;
import org.gimu.discordnano.DiscordNano;
import org.gimu.discordnano.commands.NanoExecutor;
import org.gimu.discordnano.util.NanoMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.StringJoiner;

public class GameLookupCommand extends NanoExecutor {

    public String[] triggers = {"gamelookup"};
    public String description = "Looks up a video game";
    public String usage = "";

    @Override
    public void respond(NanoMessage message, String[] args) throws IllegalArgumentException {
        if (args.length == 0) {
           throw new IllegalArgumentException();
        }

        try {
            String query = StringUtils.join(args, "+");

            HttpResponse<JsonNode> response = Unirest.get("https://videogamesrating.p.mashape.com/get.php?count=5&game=" + query)
                    .header("X-Mashape-Key", DiscordNano.config.getString("x_mashape_key"))
                    .header("Accept", "application/json")
                    .asJson();

            JSONArray jsa = response.getBody().getArray();
            System.out.println(response.getBody());

            if (jsa.length() == 0) {
                message.getChannel().sendMessage("I couldn't find a game with that title.");
                return;
            }

            JSONObject jso = new JSONObject(jsa.getJSONObject(0).toString()) {
                @Override
                public String getString(String key) throws JSONException
                {
                    String s = super.getString(key);
                    return s == null || s.isEmpty() ? "Not found." : s;
                }
            };

            StringJoiner joiner = new StringJoiner("\n");

            joiner.add("**Title**: " + jso.getString("title"));
            joiner.add("**Publisher**: " + jso.getString("publisher"));
            joiner.add("**Score**: " + jso.getString("score"));
            joiner.add("\n" + jso.getString("short_description"));

            joiner.add("\n**" + jso.getString("thumb") + "**");

            message.getChannel().sendMessage(joiner.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
