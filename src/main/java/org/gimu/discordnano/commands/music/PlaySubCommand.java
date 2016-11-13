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
import org.gimu.discordnano.commands.AbstractSubCommand;
import org.gimu.discordnano.commands.SubCommand;
import org.gimu.discordnano.lib.MusicStreamer;
import org.gimu.discordnano.lib.NanoLogger;
import org.gimu.discordnano.lib.NanoMessage;

import java.util.Optional;

@SubCommand(
        mainCommandAlias = "music",
        alias = {"play"},
        description = "",
        usage = ""
)
public class PlaySubCommand extends AbstractSubCommand {

    public Optional execute(NanoMessage message, String[] args) throws IllegalArgumentException {
        MusicStreamer streamer = MusicCommand.musicStreamer;
        String response = "";
        if (args.length == 0) {
            if (streamer.isPlaying()) {
                throw new IllegalArgumentException();
                //response = "Usage: `" + DiscordNano.prefix + "music play <url>|<index>|<searchquery>`";
            } else if (streamer.isPaused()) {
                streamer.play();
                response = "Resuming playback.";
            } else {
                if (streamer.getAudioQueue().isEmpty()) {
                    throw new IllegalArgumentException();
                    //return "Usage: `" + DiscordNano.prefix + "music play <url>|<index>|<searchquery>`";
                } else {
                    streamer.play();
                    response = "Starting playback.";
                }
            }
        } else {
            String source = args[0];
            if (NumberUtils.isNumber(source) || !source.contains("http")) {
                // Fetch from library
                String urlFromLibrary = DiscordNano.musicLibrary.get(source);
                if (urlFromLibrary.equals("-1")) {
                    response = "Couldn't find music from the library.";
                } else {
                    DiscordNano.musicLibrary.add(streamer, message.getAuthor(), new RemoteSource(urlFromLibrary), false);
                }
            } else {
                // Direct playback
                RemoteSource remoteSource = new RemoteSource(source);
                if (remoteSource.getInfo().getError() != null) {
                    //System.out.println(remoteSource.getInfo().getError());
                    NanoLogger.error("Failed to play RemoteSource");
                    response = "I fucked up!";
                } else if (remoteSource.getInfo().isLive()) {
                    response = "I don't play livestreams.";
                } else {
                    MusicCommand.threadPool.submit(() -> {
                        DiscordNano.musicLibrary.add(streamer, message.getAuthor(), remoteSource, false);
                    });
                }
            }
        }

        return Optional.of(response);
    }
}
