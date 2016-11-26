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
import sx.blah.discord.handle.obj.IChannel;

import java.util.Optional;

@SubCommand(
        mainCommandAlias = "mod",
        alias = {"textchannel"},
        description = "Get or set Nano's text channel",
        usage = "mod textchannel [id]"
)
public class TextchannelSubCommand extends AbstractSubCommand {

    public TextchannelSubCommand(String description, String usage) {
        super(description, usage);
    }

    public Optional execute(Message message, String[] args) {
        if (!PermissionUtil.isAdmin(message.getAuthor(), message.getGuild())) return Optional.of("I don't listen to you.");

        String response = "Invalid voice channel ID. Right-click on a voice channel to get its ID!";
        NanoGuildLibrary guildLibrary = DiscordNano.guildLibrary; // TODO: Better reference?
        NanoGuild currentGuild = guildLibrary.get(message.getGuild().getID()); // The nano guild
        IChannel currentTextchannel = message.getGuild().getChannelByID(currentGuild.getTextchannel()); // Previous textchannel (if set)

        if (args.length == 0) {
            if (currentTextchannel == null) {
                response = "No text channel set for this guild, use `!mod textchannel <id>` first.";
            } else {
                response = "Current text channel is `" + currentTextchannel.getName() + "`";
            }
        } else { // Possible voice channel in argument
            IChannel newTextchannel = message.getGuild().getChannelByID(args[0]); // Get new text channel
            if (newTextchannel != null) { // Found
                if (currentGuild.getTextchannel() != null && currentGuild.getTextchannel().equals(args[0])) {
                    response = "Text channel is already set to that one!";
                } else {
                    guildLibrary.setTextchannel(message.getGuild().getID(), args[0]);
                    response = "Set text channel to `" + newTextchannel.getName() + "`";
                }
            }
        }

        return Optional.of(response);
    }
}
