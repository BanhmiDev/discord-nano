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

import net.dv8tion.jda.player.source.AudioSource;
import org.gimu.discordnano.DiscordNano;
import org.gimu.discordnano.commands.AbstractCommand;
import org.gimu.discordnano.commands.MainCommand;
import org.gimu.discordnano.lib.NanoPlayer;
import sx.blah.discord.handle.audio.IAudioManager;
import sx.blah.discord.handle.audio.impl.DefaultProvider;
import sx.blah.discord.handle.impl.obj.Message;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

import java.util.LinkedList;
import java.util.Optional;

@MainCommand(
        alias = {"music"},
        description = "Plays music"
)
public class MusicCommand extends AbstractCommand {

    public MusicCommand(String description, String usage) {
        super(description, usage);
    }

    public Optional execute(Message message, String[] args) throws IllegalArgumentException, RateLimitException, DiscordException, MissingPermissionsException {
        if (args.length == 0) {
            throw new IllegalArgumentException();
        }

        IVoiceChannel voicechannel = message.getGuild().getVoiceChannelByID(DiscordNano.VOICECHANNEL_ID);

        IAudioManager manager = message.getGuild().getAudioManager();
        NanoPlayer player;
        if (manager.getAudioProvider() instanceof DefaultProvider) {
            player = new NanoPlayer();
            player.setVolume(0.25f);
            manager.setAudioProvider(player);
        } else {
            player = (NanoPlayer) manager.getAudioProvider();
        }

        // Commands
        switch (args[0].toLowerCase()) {
            case "repeat":
                if (player.isRepeat()) {
                    player.setRepeat(false);
                    message.reply("Disabled music repeat mode.");
                } else {
                    player.setRepeat(true);
                    message.reply("Enabled music repeat mode.");
                }
                break;
            case "shuffle":
                if (player.isShuffle()) {
                    player.setShuffle(false);
                    message.reply("Disabled music shuffle mode.");
                } else {
                    player.setShuffle(true);
                    message.reply("Enabled music shuffle mode.");
                }
                break;
            case "queue":
                LinkedList<AudioSource> queue = player.getAudioQueue();
                for (AudioSource audioSource : queue) {
                    System.out.println("in queue: " + audioSource.getInfo().getTitle());
                }
                break;
            case "join":
                // Get the user's voice channel
                voicechannel.join();
                message.reply("Joining Voicechannel `" + voicechannel.getName() + "`.");
                break;
            case "leave":
                message.reply("Leaving Voicechannel `" + voicechannel.getName() + "`.");
                voicechannel.leave();
                break;
        }

        return Optional.empty();
    }
}
