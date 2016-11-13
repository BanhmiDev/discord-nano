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
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.player.source.AudioInfo;
import net.dv8tion.jda.player.source.AudioSource;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

public class MusicLibrary {

    private LinkedHashMap<String, MusicObject> libraryMap = new LinkedHashMap<String, MusicObject>();

    public void add(MusicStreamer streamer, User author, AudioSource src, boolean persist) {
        // Adds to given streamer queue
        AudioInfo srcInfo = src.getInfo();
        if (srcInfo.getError() == null && !srcInfo.isLive()) {
            streamer.add(src, new MusicInfo(author));

            // Save to library
            if (persist) {
                AudioInfo info = src.getInfo();
                MusicObject musicObject = new MusicObject(info.getOrigin());

                boolean isDuplicate = false;

                // Don't allow duplicate URLs
                for (Map.Entry<String, MusicObject> entry : libraryMap.entrySet()) {
                    if (entry.getValue().getURL().equals(musicObject.getURL())) {
                        isDuplicate = true;
                    }
                }
                if (!isDuplicate) libraryMap.put(info.getTitle(), musicObject);
                save();
            }

            if (streamer.getIdle()) {
                streamer.skipToNext();
                streamer.setIdle(false);
            } else if (!streamer.isPlaying()) {
                streamer.play();
            }
        } else {
            NanoLogger.error("Invalid audio source\n" + srcInfo.getError());
        }
    }

    public String get(String query) {
        if (NumberUtils.isNumber(query)) {
            // Search based on index
            int iterator = 0;
            for (Map.Entry<String, MusicObject> entry : libraryMap.entrySet()) {
                if (iterator == Integer.parseInt(query)) {
                    return entry.getValue().getURL();
                }
                iterator++;
            }
        } else {
            // Search based on string
            String found = "";
            for (String key : libraryMap.keySet()) {
                if (key.matches(".*(?i)"+query+".*")) {
                    found = key;
                    break;
                }
            }
            if (!found.equals("")) {
                return libraryMap.get(found).getURL();
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

    public LinkedHashMap<String, MusicObject> getLibraryMap() {
        return libraryMap;
    }
}
