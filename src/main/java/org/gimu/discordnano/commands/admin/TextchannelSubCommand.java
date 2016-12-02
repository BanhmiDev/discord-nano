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
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import org.gimu.discordnano.DiscordNano;
import org.gimu.discordnano.commands.AbstractSubCommand;
import org.gimu.discordnano.commands.SubCommand;
import org.gimu.discordnano.lib.MessageUtil;
import org.gimu.discordnano.lib.NanoGuild;
import org.gimu.discordnano.lib.NanoGuildLibrary;

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

    public Optional execute(User author, Message message, String[] args) {
        // TODO: introduce permission bound commands
        String content = "Invalid voice channel ID. Right-click on a voice channel to get its ID!";
        NanoGuildLibrary guildLibrary = DiscordNano.guildLibrary; // TODO: Better reference?
        NanoGuild currentGuild = guildLibrary.get(message.getGuild().getId()); // The nano guild
        TextChannel currentTextchannel = message.getGuild().getTextChannelById(currentGuild.getTextchannel()); // Previous textchannel (if set)

        if (args.length == 0) {
            if (currentTextchannel == null) {
                content = "No text channel set for this guild, use `!mod textchannel <id>` first.";
            } else {
                content = "Current text channel is `" + currentTextchannel.getName() + "`";
            }
        } else { // Possible voice channel in argument
            TextChannel newTextchannel = message.getGuild().getTextChannelById(args[0]);

            // Found our new textchannel
            if (newTextchannel != null) {
                if (!currentGuild.getTextchannel().isEmpty() && currentGuild.getTextchannel().equals(args[0])) {
                    content = "Text channel is already set to that one!";
                } else {
                    guildLibrary.setTextchannel(message.getGuild().getId(), args[0]);
                    content = "Set text channel to `" + newTextchannel.getName() + "`";
                }
            }
        }

        Message response = MessageUtil.buildFramedMessage(author, content, true);
        return Optional.of(response);
    }
}
