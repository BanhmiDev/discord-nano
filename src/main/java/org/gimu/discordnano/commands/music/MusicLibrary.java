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

import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.player.source.AudioInfo;
import net.dv8tion.jda.player.source.AudioSource;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.http.message.BasicNameValuePair;
import org.gimu.discordnano.DiscordNano;
import org.gimu.discordnano.util.APIUtil;
import org.gimu.discordnano.util.SongInfo;
import org.json.JSONObject;
import org.omg.CORBA.NameValuePair;

import java.io.InputStream;
import java.util.*;

public class MusicLibrary {

    public static LinkedHashMap<String, JSONObject> musicLibraryMap = new LinkedHashMap<>();

    private static JSONObject musicLibrary;

    public MusicLibrary() {
        // Init library
        try {
            InputStream response = APIUtil.sendGet(DiscordNano.MUSIC_LIBRARY_URL);
            String jsonTxt = convertStreamToString(response);
            musicLibrary = new JSONObject(jsonTxt);
        } catch (Exception e) {
            System.out.println("Fucked up initializing music library");
            System.out.println(e.getMessage());
        }

        // Fill library
        Iterator<?> keys = musicLibrary.keys();
        while (keys.hasNext()) {
            String key = (String)keys.next();
            if (musicLibrary.get(key) instanceof JSONObject) {
                musicLibraryMap.put(key, (JSONObject) musicLibrary.get(key));
            }
        }
    }

    private static String convertStreamToString(java.io.InputStream inputStream) {
        java.util.Scanner s = new java.util.Scanner(inputStream).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public void add(MusicStreamer streamer, User author, AudioSource src, boolean persist) {
        // Adds to given streamer queue
        AudioInfo srcInfo = src.getInfo();
        if (srcInfo.getError() == null) {
            //message.deleteMessage();
            streamer.add(src, new SongInfo(author));

            // Save to library
            if (persist) {
                AudioInfo info = src.getInfo();
                JSONObject json = new JSONObject();
                json.put("url", info.getOrigin());
                musicLibraryMap.put(info.getTitle(), json);
                save();
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
                    return entry.getValue().getString("url");
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
                return musicLibraryMap.get(found).getString("url");
            }
        }
        return "-1";
    }

    public void save() {
        JSONObject json = new JSONObject();
        for (Map.Entry<String, JSONObject> entry : musicLibraryMap.entrySet()) {
            json.put(entry.getKey(), entry.getValue());
        }
        try {
            APIUtil.sendPost(DiscordNano.MUSIC_LIBRARY_SAVE, "");

            // Request parameters and other properties.
            /*List<NameValuePair> params = new ArrayList<NameValuePair>(2);
            params.add(new BasicNameValuePair("json", json.toString()));
            System.out.println(params);*/
        } catch (Exception e) {
            System.out.println("Fucked up saving data.");
        }
    }
}
