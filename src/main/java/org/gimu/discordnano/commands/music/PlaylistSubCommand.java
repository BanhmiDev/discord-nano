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

import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.player.Playlist;
import net.dv8tion.jda.player.source.AudioInfo;
import net.dv8tion.jda.player.source.AudioSource;
import net.dv8tion.jda.player.source.RemoteSource;
import org.gimu.discordnano.DiscordNano;
import org.gimu.discordnano.commands.AbstractSubCommand;
import org.gimu.discordnano.commands.SubCommand;
import org.gimu.discordnano.lib.MusicStreamer;
import org.gimu.discordnano.lib.NanoMessage;

import java.util.*;

@SubCommand(
        mainCommandAlias = "music",
        alias = {"playlist"},
        description = "",
        usage = ""
)
public class PlaylistSubCommand extends AbstractSubCommand {

    public static final Set<String> playlistLoader = new HashSet<>();

    public Optional execute(NanoMessage message, String[] args) throws IllegalArgumentException {
        if (args.length == 0) {
            //message.reply("Please select a playlist using `" + DiscordNano.prefix + "music playlist <url>`");
            throw new IllegalArgumentException();
        }

        String source = args[0];
        MusicStreamer streamer = MusicCommand.musicStreamer;
        Message playlistStatus = message.reply("*Processing playlist..*");

        Playlist playlist = null;
        try {
            playlist = Playlist.getPlaylist(source);
        } catch (NullPointerException ex) {
            if (ex.getLocalizedMessage().equals("The YT-DL playlist process resulted in a null or zero-length INFO!")) {
                message.reply("That's not a valid playlist source.");
            } else {
                ex.printStackTrace();
            }
        }

        List<AudioSource> sources = new LinkedList<>(playlist.getSources());
        if (sources.size() <= 1) {
            // Single source
            RemoteSource remoteSource = new RemoteSource(source);
            MusicCommand.threadPool.submit(() -> {
                message.deleteMessage();
                MusicCommand.musicLibrary.add(streamer, message.getAuthor(), remoteSource, false);
            });
        } else {
            // Multiple sources
            if (playlistLoader.contains(message.getChannelId())) { // Limit processing calls
                playlistStatus.updateMessage("Currently busy processing another playlist（；¬＿¬)");
            } else {
                playlistLoader.add(message.getChannelId());
                playlistStatus.updateMessage("Queuing up " + sources.size() + " songs...");

                MusicCommand.threadPool.submit(() -> {
                    sources.stream().forEachOrdered(audioSource -> {
                        AudioInfo audioInfo = audioSource.getInfo();
                        if (audioInfo.isLive()) {
                            message.reply("I don't play livestreams ｢(ﾟﾍﾟ)");
                            return;
                        }

                        MusicCommand.musicLibrary.add(streamer, message.getAuthor(), audioSource, false);
                    });
                    playlistLoader.remove(message.getChannelId());
                    message.deleteMessage();
                    playlistStatus.updateMessage("Successfully loaded `" + source + "`!");
                });
            }
        }

        return Optional.empty();
    }
}
