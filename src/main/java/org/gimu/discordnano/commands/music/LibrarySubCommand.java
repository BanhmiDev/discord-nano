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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.player.source.AudioInfo;
import net.dv8tion.jda.player.source.AudioSource;
import org.apache.commons.lang3.math.NumberUtils;
import org.gimu.discordnano.util.SongInfo;
import org.json.JSONObject;

import java.io.*;
import java.util.*;

//TODO: FINISH
public class MusicLibrary {

    public static LinkedHashMap<String, JSONObject> musicLibraryMap = new LinkedHashMap<>();

    private static JSONObject musicLibrary;

    public MusicLibrary() {
        // Init library
        /*try {
            FileReader fileReader = new FileReader("music_library.json");
            musicLibrary = (JSONObject) jsonParser.parse(fileReader);
        } catch (Exception e) {
            System.out.println("Fucked up initializing music library");
            System.out.println(e.getMessage());
        }

        // Fill library
        Iterator<?> keys = musicLibrary.keys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            if (musicLibrary.get(key) instanceof JSONObject) {
                musicLibraryMap.put(key, (JSONObject) musicLibrary.get(key));
            }
        }*/
    }

    private static String convertStreamToString(java.io.InputStream inputStream) {
        java.util.Scanner s = new java.util.Scanner(inputStream).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public void add(MusicStreamer streamer, User author, AudioSource src, boolean persist) {
        // Adds to given streamer queue
        System.out.println("OK");
        AudioInfo srcInfo = src.getInfo();
        if (srcInfo.getError() == null && !srcInfo.isLive()) {
            streamer.add(src, new SongInfo(author));

            System.out.println("OK2");
            // Save to library
            if (persist) {
                System.out.println("persist");
                AudioInfo info = src.getInfo();
                JSONObject json = new JSONObject();
                //json.put("url", info.getOrigin());
                //musicLibraryMap.put(info.getTitle(), json);
                //System.out.println("test" +json.toJSONString());
                //System.out.println("before save: " + musicLibrary.toJSONString());
                //save();
            }

            if (streamer.getIdle()) {
                streamer.skipToNext();
                streamer.setIdle(false);
            } else if (!streamer.isPlaying()) {
                streamer.play();
            }
        } else {
            String err = srcInfo.getError();
            System.err.println(err);
        }
    }

    public String get(String query) {
        if (NumberUtils.isNumber(query)) {
            // Search based on index
            int iterator = 0;
            for (Map.Entry<String, JSONObject> entry : musicLibraryMap.entrySet()) {
                if (iterator == Integer.parseInt(query)) {
                    return (String)entry.getValue().get("url");
                }
                iterator++;
            }
        } else {
            // Search based on string
            String found = "";
            for (String key : musicLibraryMap.keySet()) {
                if (key.matches(".*(?i)"+query+".*")) {
                    found = key;
                    break;
                }
            }
            if (!found.equals("")) {
                return (String)musicLibraryMap.get(found).get("url");
            }
        }
        return "-1";
    }

    public void save() {
        /*JSONObject json = new JSONObject();
        for (Map.Entry<String, JSONObject> entry : musicLibraryMap.entrySet()) {
            json.put(entry.getKey(), entry.getValue());
        }

        try (Writer writer = new FileWriter("music_library.json")) {
            Gson gson = new GsonBuilder().create();
            gson.toJson(, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }
}
