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
package org.gimu.discordnano;

import com.google.gson.Gson;
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.JDABuilder;
import org.gimu.discordnano.lib.MusicLibrary;
import org.gimu.discordnano.lib.NanoLogger;
import org.gimu.discordnano.listeners.CommandListener;
import org.json.JSONObject;
import javax.security.auth.login.LoginException;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class DiscordNano {

    // ffmpeg:
    // http://johnvansickle.com/ffmpeg/builds/ffmpeg-git-32bit-static.tar.xz
    public static final long START_TIME = System.currentTimeMillis();
    public static final String AUTHOR_ID = "211615203115139072";
    public static final String DEFAULT_STATUS = "";
    public static final JSONObject config = Configurator.getConfig();

    public static String PREFIX;

    public static boolean DEBUG;
    public static boolean PRODUCTION;

    public static boolean RANDOM_MUSIC;
    public static float DEFAULT_VOLUME = 0.25f;

    public static String VOICECHANNEL_ID;
    public static String TESTCHANNEL_ID;

    public static MusicLibrary musicLibrary;

    public static JDA JDA;

    public static void main(String[] args) throws LoginException {
        // Init library
        // Deserialization
        NanoLogger.debug("Initializing music library");

        Gson gson = new Gson();
        FileReader fileReader = null;
        try {
            fileReader = new FileReader("music_library.json");
        } catch (FileNotFoundException e) {
            NanoLogger.error("Couldn't load music library (file not found)");
            e.printStackTrace();
        }
        musicLibrary = gson.fromJson(fileReader, MusicLibrary.class);
        if (musicLibrary == null) { // Empty JSON file
            musicLibrary = new MusicLibrary();
        }

        // Configuration
        PREFIX = config.getString("prefix");

        PRODUCTION = config.getBoolean("production");
        DEBUG = config.getBoolean("debug");

        RANDOM_MUSIC = config.getBoolean("random_music");
        DEFAULT_VOLUME = config.getLong("default_volume");

        VOICECHANNEL_ID = config.getString("voicechannel");
        TESTCHANNEL_ID = config.getString("testchannel");

        // Spawn our bot
        JDA = new JDABuilder()
                .setBotToken(config.getString("token"))
                .addListener(new CommandListener())
                .buildAsync();

    }

}
