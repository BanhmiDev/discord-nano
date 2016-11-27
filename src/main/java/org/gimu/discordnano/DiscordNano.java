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

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import org.gimu.discordnano.lib.MusicLibrary;
import org.gimu.discordnano.lib.NanoGuildLibrary;
import org.gimu.discordnano.lib.NanoLogger;
import org.gimu.discordnano.listeners.CommandListener;
import org.gimu.discordnano.listeners.ConversationListener;
import org.json.JSONObject;

import javax.security.auth.login.LoginException;

public class DiscordNano {

    public static final long START_TIME = System.currentTimeMillis();
    public static final JSONObject config = Configurator.getConfig();

    public static MusicLibrary musicLibrary = new MusicLibrary();
    public static NanoGuildLibrary guildLibrary = new NanoGuildLibrary();

    public static String DEFAULT_STATUS;
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

        // Configuration
        DEFAULT_STATUS = config.getString("default_status");
        PREFIX = config.getString("prefix");
        DEBUG = config.getBoolean("debug");
        DB_USER = config.getString("db_user");
        DB_PASS = config.getString("db_pass");
        RANDOM_MUSIC = config.getBoolean("random_music");
        DEFAULT_VOLUME = Float.parseFloat(config.getString("default_volume"));

        JDA jda = null;
        try {
            jda = new JDABuilder(AccountType.BOT).setToken(config.getString("token")).buildAsync();
        } catch (RateLimitedException e) {
            e.printStackTrace();
        }
        jda.addEventListener(new CommandListener(jda));
        jda.addEventListener(new ConversationListener(jda));
    }
}
