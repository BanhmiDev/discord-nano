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

import org.gimu.discordnano.DiscordNano;
import org.gimu.discordnano.commands.AbstractSubCommand;
import org.gimu.discordnano.commands.SubCommand;
import org.gimu.discordnano.lib.NanoGuild;
import org.gimu.discordnano.lib.NanoGuildLibrary;
import org.gimu.discordnano.util.PermissionUtil;
import sx.blah.discord.handle.impl.obj.Message;
import sx.blah.discord.handle.obj.IVoiceChannel;

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
        if (!PermissionUtil.isAdmin(message.getAuthor(), message.getGuild())) return Optional.of("I don't listen to you.");

        String response = "Invalid voice channel ID. Right-click on a voice channel to get its ID!";
        NanoGuildLibrary guildLibrary = DiscordNano.guildLibrary; // TODO: Better reference?
        NanoGuild currentGuild = guildLibrary.get(message.getGuild().getID()); // The nano guild
        IVoiceChannel currentVoicechannel = message.getGuild().getVoiceChannelByID(currentGuild.getVoicechannel()); // Previous voicechannel (if set)

        if (args.length == 0) {
            if (currentVoicechannel == null) {
                response = "No voice channel set for this guild, use `!mod voicechannel <id>` first.";
            } else {
                response = "Current voice channel is `" + currentVoicechannel.getName() + "`";
            }
        } else { // Possible voice channel in argument
            IVoiceChannel newVoicechannel = message.getGuild().getVoiceChannelByID(args[0]); // Get new voice channel
            if (newVoicechannel != null) { // Found
                if (currentGuild.getVoicechannel() != null && currentGuild.getVoicechannel().equals(args[0])) {
                    response = "Voice channel is already set to that one!";
                } else {
                    if (currentGuild.getVoicechannel() != null) {
                        currentVoicechannel.leave(); // Leave old voice channel first
                    }
                    guildLibrary.setVoicechannel(message.getGuild().getID(), args[0]);
                    response = "Set voice channel to `" + newVoicechannel.getName() + "`";
                }
            }
        }

        return Optional.of(response);
    }
}
