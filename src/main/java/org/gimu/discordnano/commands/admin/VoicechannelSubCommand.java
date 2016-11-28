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

package org.gimu.discordnano.commands.admin;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.VoiceChannel;
import org.gimu.discordnano.DiscordNano;
import org.gimu.discordnano.commands.AbstractSubCommand;
import org.gimu.discordnano.commands.SubCommand;
import org.gimu.discordnano.lib.MessageUtil;
import org.gimu.discordnano.lib.NanoGuild;
import org.gimu.discordnano.lib.NanoGuildLibrary;

import java.util.Optional;

@SubCommand(
        mainCommandAlias = "mod",
        alias = {"voicechannel"},
        description = "Get or set Nano's voice channel",
        usage = "mod voicechannel [id]"
)
public class VoicechannelSubCommand extends AbstractSubCommand {

    public VoicechannelSubCommand(String description, String usage) {
        super(description, usage);
    }

    public Optional execute(Message message, String[] args) {
        // TODO: introduce permission bound commands
        String content = "Invalid voice channel ID. Right-click on a voice channel to get its ID!";
        NanoGuildLibrary guildLibrary = DiscordNano.guildLibrary; // TODO: Better reference?
        NanoGuild currentGuild = guildLibrary.get(message.getGuild().getId()); // The nano guild
        VoiceChannel currentVoicechannel = message.getGuild().getVoiceChannelById(currentGuild.getVoicechannel()); // Previous voicechannel (if set)

        if (args.length == 0) {
            if (currentVoicechannel == null) {
                content = "No voice channel set for this guild, use `!mod voicechannel <id>` first.";
            } else {
                content = "Current voice channel is `" + currentVoicechannel.getName() + "`";
            }
        } else { // Possible voice channel in argument
            VoiceChannel newVoicechannel = message.getGuild().getVoiceChannelById(args[0]); // Get new voice channel
            if (newVoicechannel != null) { // New voice channel was found
                if (!currentGuild.getVoicechannel().isEmpty() && currentGuild.getVoicechannel().equals(args[0])) {
                    content = "Voice channel is already set to that one!";
                } else {
                    guildLibrary.setVoicechannel(message.getGuild().getId(), args[0]);
                    content = "Set voice channel to `" + newVoicechannel.getName() + "`";
                }
            }
        }

        Message response = MessageUtil.frameMessage(content, true);
        return Optional.of(response);
    }
}
