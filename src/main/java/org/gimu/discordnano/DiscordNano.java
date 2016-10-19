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

import net.dv8tion.jda.JDA;
import net.dv8tion.jda.JDABuilder;
import org.gimu.discordnano.listeners.CommandListener;
import org.json.JSONObject;
import javax.security.auth.login.LoginException;

public class DiscordNano {

    // ffmpeg:
    // http://johnvansickle.com/ffmpeg/builds/ffmpeg-git-32bit-static.tar.xz
    public static final long START_TIME = System.currentTimeMillis();
    public static final String AUTHOR_ID = "211615203115139072";
    public static final String DEFAULT_STATUS = "";
    public static final boolean TESTMODE = false;
    public static final float DEFAULT_VOLUME = 0.25f;
    public static final boolean RANDOM_MUSIC = true;

    public static final JSONObject config = Configurator.getConfig();
    public static String prefix;
    public static String VOICECHANNEL_ID;
    public static String TESTCHANNEL_ID;
    public static String MUSIC_LIBRARY_SAVE;
    public static String MUSIC_LIBRARY_URL;
    public static JDA jda;

    public static void main(String[] args) throws LoginException {
        prefix = config.getString("prefix");
        VOICECHANNEL_ID = config.getString("voicechannel");
        TESTCHANNEL_ID = config.getString("testchannel");
        MUSIC_LIBRARY_SAVE = config.getString("music_library_save");
        MUSIC_LIBRARY_URL = config.getString("music_library_url");
        jda = new JDABuilder()
                .setBotToken(config.getString("token"))
                .addListener(new CommandListener())
                .buildAsync();

    }

}
