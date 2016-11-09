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
import net.dv8tion.jda.player.source.RemoteSource;
import org.apache.commons.lang3.math.NumberUtils;
import org.gimu.discordnano.DiscordNano;

public class PlayCommand {

    public static String respond(MusicStreamer streamer, User author, String input) {
        if (input.length() == 0) {
            if (streamer.isPlaying()) {
                return "Usage: `" + DiscordNano.prefix + "music play <url>|<index>|<searchquery>`";
            } else if (streamer.isPaused()) {
                streamer.play();
                return "Resuming playback.";
            } else {
                if (streamer.getAudioQueue().isEmpty()) {
                    return "Usage: `" + DiscordNano.prefix + "music play <url>|<index>|<searchquery>`";
                } else {
                    streamer.play();
                    return "Starting playback.";
                }
            }
        } else {
            if (NumberUtils.isNumber(input) || !input.contains("http")) {
                // Fetch from library
                String urlFromLibrary = MusicCommand.musicLibrary.get(input);
                if (urlFromLibrary.equals("-1")) {
                    return "Couldn't find music from the library.";
                } else {
                    MusicCommand.musicLibrary.add(streamer, author, new RemoteSource(urlFromLibrary), false);
                }
            } else {
                // Direct playback
                RemoteSource src = new RemoteSource(input);
                if (src.getInfo().getError() != null) {
                    String err = src.getInfo().getError();
                    System.err.println(err);
                    return "I fucked up!";
                } else if (src.getInfo().isLive()) {
                    return "I don't play livestreams.";
                } else {
                    MusicCommand.threadPool.submit(() -> {
                        MusicCommand.musicLibrary.add(streamer, author, src, false);
                    });
                }
            }
        }

        return null;
    }
}
