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
import net.dv8tion.jda.player.source.AudioInfo;
import net.dv8tion.jda.player.source.AudioSource;
import net.dv8tion.jda.player.source.RemoteSource;

import org.gimu.discordnano.DiscordNano;
import org.gimu.discordnano.commands.AbstractSubCommand;
import org.gimu.discordnano.commands.SubCommand;
import org.gimu.discordnano.lib.MessageUtil;
import org.gimu.discordnano.lib.NanoPlayer;

import java.util.Optional;

@SubCommand(
        mainCommandAlias = "music",
        alias = {"add"},
        description = "Add music to the library",
        usage = "music add <source>"
)
public class AddSubCommand extends AbstractSubCommand {

    public AddSubCommand(String description, String usage) {
        super(description, usage);
    }

    public Optional execute(Message message, String[] args) throws IllegalArgumentException {
        /*
        if (args.length == 0) {
            throw new IllegalArgumentException();
        }

        if (DiscordNano.musicLibrary.add(args[0])) {
            IAudioManager manager = message.getGuild().getAudioManager();
            NanoPlayer player;
            if (manager.getAudioProvider() instanceof DefaultProvider) {
                player = new NanoPlayer();
                player.setVolume(0.25f);
                manager.setAudioProvider(player);
            } else {
                player = (NanoPlayer) manager.getAudioProvider();
            }
            AudioSource audioSource = new RemoteSource(args[0], message.getGuild().getID());
            AudioInfo info = audioSource.getInfo();
            if (info.getError() == null) {
                player.getAudioQueue().add(audioSource);
                message.getChannel().sendMessage("Added `" + info.getTitle() + "` to library and current queue!");
            }
        }*/

        Message response = MessageUtil.frameMessage("disabled", true);
        return Optional.of(response);
    }
}
