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
import org.gimu.discordnano.util.NanoMessage;
import org.gimu.discordnano.util.SongInfo;

import java.util.LinkedList;
import java.util.List;

public class PlaylistCommand extends MusicCommand {

    private String[] triggers = {"todo"};
    public static void respond(NanoMessage message, User author, String input) {
        if (input.length() == 0) {
            message.reply("Please select a playlist using `" + DiscordNano.prefix + "music playlist <url>`");
            return;
        }

        Message playlistStatus = message.reply("*Processing playlist..*");

        Playlist playlist = null;
        try {
            playlist = Playlist.getPlaylist(input);

        } catch (NullPointerException ex) {
            if (ex.getLocalizedMessage().equals("The YT-DL playlist process resulted in a null or zero-length INFO!")) {
                playlistStatus.updateMessage("That's not a valid playlist URL ｢(ﾟﾍﾟ)");
                return;
            } else
                ex.printStackTrace();
        }

        List<AudioSource> sources = new LinkedList<>(playlist.getSources());
        if (sources.size() <= 1) {
            RemoteSource src = new RemoteSource(input);
            threadPool.submit(() -> {
                addSingleSource(src, player, message);
            });
        } else {
            if (playlistLoader.contains(message.getChannelId())) {
                playlistStatus.updateMessage("Currently busy processing another playlist（；¬＿¬)");
                return;
            }

            playlistLoader.add(message.getChannelId());
            playlistStatus.updateMessage("Queuing up " + sources.size() + " songs...");

            threadPool.submit(() -> {
                sources.stream().forEachOrdered(audioSource -> {
                    AudioInfo audioInfo = audioSource.getInfo();
                    if (audioInfo.isLive()) {
                        message.reply("I don't play livestreams ｢(ﾟﾍﾟ)");
                        return;
                    }

                    List<AudioSource> audioQueue = player.getAudioQueue();
                    if (audioInfo.getError() == null) {
                        musicQueue.put(audioSource, new SongInfo(author, null));
                        audioQueue.add(audioSource);
                        if (player.isStopped())
                            player.play();
                    } else {
                        String err = audioInfo.getError();
                        System.err.println(err);
                    }
                });
                playlistLoader.remove(message.getChannelId());
                message.deleteMessage();
                playlistStatus.updateMessage("Successfully loaded `" + input + "`!");
            });
        }
    }
}
