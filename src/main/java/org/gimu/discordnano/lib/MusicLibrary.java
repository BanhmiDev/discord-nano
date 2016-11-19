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

package org.gimu.discordnano.lib;

import com.google.gson.Gson;
import net.dv8tion.jda.player.source.AudioSource;
import net.dv8tion.jda.player.source.RemoteSource;
import org.apache.commons.lang3.math.NumberUtils;
import sx.blah.discord.handle.impl.obj.Message;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

public class MusicLibrary {

    // <title, url>
    private LinkedHashMap<String, String> libraryMap = new LinkedHashMap<String, String>();

    public boolean add(String url, Message message) throws RateLimitException, DiscordException, MissingPermissionsException {
        if (!url.contains("http")) {
            message.getChannel().sendMessage("Invalid source!");
            return false;
        }
        String guildId = message.getGuild().getID();

        // Add music source to library
        boolean isDuplicate = false;

        // Don't allow duplicate URLs
        for (Map.Entry<String, String> entry : libraryMap.entrySet()) {
            if (entry.getValue().equals(url)) {
                isDuplicate = true;
            }
        }
        if (isDuplicate) {
            message.getChannel().sendMessage("Duplicate!");
            return false;
        }

        AudioSource audioSource = new RemoteSource(url, guildId);
        libraryMap.put(audioSource.getInfo().getTitle(), url);
        save();
        return true;
    }

    public String get(String query) {
        if (NumberUtils.isNumber(query)) {
            // Search based on index
            int iterator = 0;
            for (Map.Entry<String, String> entry : libraryMap.entrySet()) {
                if (iterator == Integer.parseInt(query)) {
                    return entry.getValue();
                }
                iterator++;
            }
        } else {
            // Search based on string
            for (String key : libraryMap.keySet()) {
                if (key.matches(".*(?i)"+query+".*")) {
                    return libraryMap.get(key);
                }
            }
        }
        return "-1";
    }

    public void save() {
        // Serialization
        Gson gson = new Gson();
        String json = gson.toJson(this);

        try (PrintWriter out = new PrintWriter("music_library.json")) {
            out.println(json);
        } catch (FileNotFoundException e) {
            NanoLogger.error("Couldn't save music library (file not found)");
        }
    }

    public int size() {
        return libraryMap.size();
    }

    public LinkedHashMap<String, String> getLibraryMap() {
        return libraryMap;
    }
}
