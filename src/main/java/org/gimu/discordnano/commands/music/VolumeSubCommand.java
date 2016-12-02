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
import net.dv8tion.jda.core.entities.User;
import org.gimu.discordnano.commands.AbstractSubCommand;
import org.gimu.discordnano.commands.SubCommand;
import org.gimu.discordnano.lib.MessageUtil;

import java.util.Optional;

@SubCommand(
        mainCommandAlias = "music",
        alias = {"volume"},
        description = "Set Nano's voice volume",
        usage = "music volume [0.0, 1.0]"
)
public class VolumeSubCommand extends AbstractSubCommand {

    public VolumeSubCommand(String description, String usage) {
        super(description, usage);
    }

    public Optional execute(User author, Message message, String[] args) throws IllegalArgumentException {
        /*IAudioManager manager = message.getGuild().getAudioManager();
        NanoPlayer player;
        if (manager.getAudioProvider() instanceof DefaultProvider) {
            player = new NanoPlayer();
            player.setVolume(0.25f);
            manager.setAudioProvider(player);
        } else {
            player = (NanoPlayer) manager.getAudioProvider();
        }

        if (args.length == 0) {
            message.getChannel().sendMessage("Current volume: **" + player.getVolume() + "**");
        } else {
            Float newVolume = Float.parseFloat(args[0]);
            if (newVolume >= 0.0f && newVolume <= 1.0f) {
                player.setVolume(newVolume);
                message.getChannel().sendMessage("Volume set to **" + newVolume + "**");
            } else {
                throw new IllegalArgumentException();
            }
        }*/

        Message response = MessageUtil.buildFramedMessage(author, "disabled", true);
        return Optional.of(response);
    }
}
