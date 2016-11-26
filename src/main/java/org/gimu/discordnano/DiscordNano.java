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
import org.gimu.discordnano.lib.MusicLibrary;
import org.gimu.discordnano.lib.NanoGuildLibrary;
import org.gimu.discordnano.lib.NanoLogger;
import org.gimu.discordnano.listeners.CommandListener;
import org.gimu.discordnano.listeners.ConversationListener;
import org.json.JSONObject;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.util.DiscordException;

import javax.security.auth.login.LoginException;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class DiscordNano {

    // ffmpeg:
    // http://johnvansickle.com/ffmpeg/builds/ffmpeg-git-32bit-static.tar.xz
    public static final long START_TIME = System.currentTimeMillis();
    public static final String DEFAULT_STATUS = "";
    public static final JSONObject config = Configurator.getConfig();

    public static MusicLibrary musicLibrary;
    public static NanoGuildLibrary guildLibrary = new NanoGuildLibrary();

    public static String PREFIX;
    public static boolean DEBUG;
    public static boolean RANDOM_MUSIC;
    public static float DEFAULT_VOLUME;
    public static String DB_USER;
    public static String DB_PASS;
    public static String BOT_OWNER = "211615203115139072";

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
        if (musicLibrary == null) { // Empty JSON file, TODO: move to database
            musicLibrary = new MusicLibrary();
        }

        // Configuration
        PREFIX = config.getString("prefix");
        DEBUG = config.getBoolean("debug");
        DB_USER = config.getString("db_user");
        DB_PASS = config.getString("db_pass");
        RANDOM_MUSIC = config.getBoolean("random_music");
        DEFAULT_VOLUME = Float.parseFloat(config.getString("default_volume"));

        // Spawn our bot
        ClientBuilder clientBuilder = new ClientBuilder(); // Creates the ClientBuilder instance
        clientBuilder.withToken(config.getString("token")); // Adds the login info to the builder
        IDiscordClient client = null;
        try {
            client = clientBuilder.login(); // Creates the client instance and logs the client in
        } catch (DiscordException e) {
            e.printStackTrace();
        }
        EventDispatcher dispatcher = client.getDispatcher();
        dispatcher.registerListener(new CommandListener(client));
        dispatcher.registerListener(new ConversationListener(client));
    }
}
