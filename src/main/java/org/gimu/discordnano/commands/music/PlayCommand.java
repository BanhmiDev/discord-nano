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

import net.dv8tion.jda.player.source.RemoteSource;
import org.apache.commons.lang3.math.NumberUtils;
import org.gimu.discordnano.DiscordNano;
import org.gimu.discordnano.util.CustomMusicPlayer;
import org.gimu.discordnano.lib.NanoMessage;

public class PlayCommand {

    public static void respond(NanoMessage message, String input, CustomMusicPlayer player) {
        if (input.length() == 0) {
            if (player.isPlaying()) {
                message.reply("Usage: `" + DiscordNano.prefix + "music play <url>|<index>|<searchquery>`");
            } else if (player.isPaused()) {
                player.play();
                message.reply("Resuming playback.");
            } else {
                if (player.getAudioQueue().isEmpty()) {
                    message.reply("Usage: `" + DiscordNano.prefix + "music play <url>|<index>|<searchquery>`");
                } else {
                    player.play();
                    message.reply("Starting playback.");
                }
            }
        } else {
            message.getChannel().sendTyping();

            if (NumberUtils.isNumber(input) || !input.contains("http")) {
                // Fetch from library
                String urlFromLibrary = MusicExecutor.getSrcFromLibrary(input);
                if (urlFromLibrary.equals("-1")) {
                    message.reply("Couldn't find music from the library.");
                } else {
                    message.getChannel().sendTyping();
                    MusicExecutor.addSingleSource(new RemoteSource(urlFromLibrary), player, message);
                }
            } else {
                // Direct playback
                RemoteSource src = new RemoteSource(input);
                if (src.getInfo().getError() != null) {
                    String err = src.getInfo().getError();
                    if (err.length() > 1900) {
                        System.err.println(err);
                        message.reply("I fucked up!");
                    } else
                        message.reply("Invalid URL, you fucked up.");
                } else if (src.getInfo().isLive()) {
                    message.reply("I don't play livestreams.");
                } else {
                    MusicExecutor.threadPool.submit(() -> {
                        MusicExecutor.addSingleSource(src, player, message);
                    });
                }
            }
        }
    }
}
