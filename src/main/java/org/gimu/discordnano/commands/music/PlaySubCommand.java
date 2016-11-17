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

import net.dv8tion.jda.player.Playlist;
import net.dv8tion.jda.player.source.AudioInfo;
import net.dv8tion.jda.player.source.AudioSource;
import org.apache.commons.lang3.math.NumberUtils;
import org.gimu.discordnano.DiscordNano;
import org.gimu.discordnano.commands.AbstractSubCommand;
import org.gimu.discordnano.commands.SubCommand;
import org.gimu.discordnano.lib.NanoLogger;
import org.gimu.discordnano.lib.NanoPlayer;
import sx.blah.discord.handle.audio.IAudioManager;
import sx.blah.discord.handle.audio.impl.DefaultProvider;
import sx.blah.discord.handle.impl.obj.Message;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@SubCommand(
        mainCommandAlias = "music",
        alias = {"play"},
        description = "The music module",
        usage = ""
)
public class PlaySubCommand extends AbstractSubCommand {

    private void playSource(Message message, Playlist playlist) throws RateLimitException, DiscordException, MissingPermissionsException {
        List<AudioSource> sources = new LinkedList(playlist.getSources());
        IAudioManager manager = message.getGuild().getAudioManager();
        NanoPlayer player;
        if (manager.getAudioProvider() instanceof DefaultProvider) {
            player = new NanoPlayer();
            player.setVolume(0.25f);
            manager.setAudioProvider(player);
        } else {
            player = (NanoPlayer) manager.getAudioProvider();
        }

        if (sources.size() > 1) {
            message.getChannel().sendMessage("Found a playlist with **" + sources.size() + "** entries.\nProceeding to gather information and queue sources. This may take some time...");
            final NanoPlayer fPlayer = player;
            Thread thread = new Thread() {
                @Override
                public void run() {
                    for (Iterator<AudioSource> it = sources.iterator(); it.hasNext();) {
                        AudioSource source = it.next();
                        AudioInfo info = source.getInfo();
                        List<AudioSource> queue = fPlayer.getAudioQueue();
                        if (info.getError() == null) {
                            queue.add(source);
                            if (fPlayer.isStopped()) fPlayer.play();
                        } else {
                            NanoLogger.error("Playback error (playlist)\n" + info.getError());
                            it.remove();
                        }
                    }
                    try {
                        message.reply("Finished queuing provided playlist. Successfully queued **" + sources.size() + "** sources");
                    } catch (Exception e) {
                        // ...
                    }
                }
            };
            thread.start();
        } else {
            AudioSource audioSource = sources.get(0);
            AudioInfo info = audioSource.getInfo();
            if (info.getError() == null) {
                player.getAudioQueue().add(audioSource);
                if (player.isStopped()) {
                    player.play();
                }
                message.reply("Added `" + info.getTitle() + "` to the music queue.");
            } else {
                message.reply("There was an error while loading the provided URL.");
                NanoLogger.error("Playback error\n" + info.getError());
            }
        }
    }

    public Optional execute(Message message, String[] args) throws IllegalArgumentException, RateLimitException, DiscordException, MissingPermissionsException {
        if (args.length == 0) {
            throw new IllegalArgumentException();
        }

        String response = "";
        String source = args[0];

        if (NumberUtils.isNumber(source) || !source.contains("http")) {
            // Playback from library
            String urlFromLibrary = DiscordNano.musicLibrary.get(source);
            if (urlFromLibrary.equals("-1")) {
                response = "Couldn't find music from the library.";
            } else {
                playSource(message, Playlist.getPlaylist(urlFromLibrary, message.getGuild().getID()));
            }
        } else {
            // Playback with source
            playSource(message, Playlist.getPlaylist(source, message.getGuild().getID()));
        }

        return Optional.of(response);
    }
}
