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

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.player.Playlist;

import org.gimu.discordnano.DiscordNano;
import org.gimu.discordnano.commands.AbstractSubCommand;
import org.gimu.discordnano.commands.SubCommand;
import org.gimu.discordnano.lib.MessageUtil;
import org.gimu.discordnano.listeners.CommandListener;

import java.util.Optional;

@SubCommand(
        mainCommandAlias = "music",
        alias = {"play"},
        description = "Music play subcommand",
        usage = "music play <index|source>"
)
public class PlaySubCommand extends AbstractSubCommand {

    public PlaySubCommand(String description, String usage) {
        super(description, usage);
    }

    private void playSource(Message message, Playlist playlist) {
        /*List<AudioSource> sources = new LinkedList(playlist.getSources());
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
                        message.getChannel().sendMessage("Finished queuing provided playlist. Successfully queued **" + sources.size() + "** sources");
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
                message.getChannel().sendMessage("Added `" + info.getTitle() + "` to the music queue.");
            } else {
                message.getChannel().sendMessage("There was an error while loading the provided URL.");
                NanoLogger.error("Playback error\n" + info.getError());
            }
        }*/
    }

    public Optional execute(Message message, String[] args) throws IllegalArgumentException {
        /*if (args.length == 0) {
            throw new IllegalArgumentException();
        }

        message.getChannel().toggleTypingStatus();

        String voicechannelID = DiscordNano.guildLibrary.get(message.getGuild().getID()).getVoicechannel();
        IVoiceChannel voicechannel = message.getGuild().getVoiceChannelByID(voicechannelID); // TODO: every command has reference to the nanoguild object
        if (voicechannel == null) {
            return Optional.of("Voice channel not set for this guild, use `!mod voicechannel <id>` first.");
        } else if (!CommandListener.client.getConnectedVoiceChannels().contains(voicechannel)) {
            return Optional.of("Nano is not in a voice channel, use `!music join` first.");
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

        return Optional.of(response);*/
        Message response = MessageUtil.frameMessage("disabled", true);
        return Optional.of(response);
    }
}
