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
package org.gimu.discordnano.commands.music;

import net.dv8tion.jda.player.source.AudioSource;
import org.gimu.discordnano.util.Hastebin;
import org.gimu.discordnano.util.NanoMessage;
import org.gimu.discordnano.util.SongInfo;
import org.json.JSONObject;

import java.util.Map;

public class ListCommand {

    public static void respond(NanoMessage message, Map<String, JSONObject> musicLibraryMap) {
        if (musicLibraryMap.size() == 0) {
            message.reply("The music library empty ｢(ﾟﾍﾟ)");
        }
        StringBuilder stringBuilder = new StringBuilder("__Music Library Status__ (Entries: " + musicLibraryMap.size() + ")\n\n");
        int iterator = 0;
        if (musicLibraryMap.size() <= 10) {
            for (Map.Entry<String, JSONObject> entry : musicLibraryMap.entrySet()) {
                stringBuilder.append("**" + iterator + "** " + entry.getKey() + " **<" + entry.getValue().getString("url") + ">\n");
                iterator++;
            }
        } else {
            message.getChannel().sendTyping();
            StringBuilder body = new StringBuilder();
            for (Map.Entry<String, JSONObject> entry : musicLibraryMap.entrySet()) {
                body.append("**" + iterator + "** " + entry.getKey() + " **<" + entry.getValue().getString("url") + ">**\n");
                iterator++;
            }
            stringBuilder.append(Hastebin.post(body.deleteCharAt(body.length()-1).toString()));
        }
        message.reply(stringBuilder.toString());
    }
}
