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

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.exceptions.ErrorResponseException;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import org.gimu.discordnano.commands.AbstractCommand;
import org.gimu.discordnano.commands.MainCommand;
import org.gimu.discordnano.lib.MessageUtil;
import org.gimu.discordnano.lib.NanoPermission;

import java.util.List;
import java.util.Optional;

@MainCommand(
        alias = "pin",
        description = "Pin or unpin messages",
        usage = "pin <id>"
)
public class PinCommand extends AbstractCommand {

    public PinCommand(String description, String usage, String alias) {
        super(description, usage, alias);
        this.setPermission(NanoPermission.GUILD_OWNER);
    }

    public Optional execute(User author, Message message, String[] args) throws RateLimitedException {
        if (args.length == 0) {
            throw new IllegalArgumentException();
        }

        TextChannel channel = message.getTextChannel();
        String messageId = args[0];

        if (!message.getGuild().getSelfMember().hasPermission(Permission.MESSAGE_MANAGE)) {
            channel.sendMessage("I'm not allowed to manage messages.").queue();
        }

        Message targetMessage = null;
        String content = "";

        try {
            targetMessage = channel.getMessageById(messageId).block();
        } catch (ErrorResponseException e) {
            // Stop execution if an invalid message id was given
            return Optional.of(MessageUtil.buildMessage("Invalid message id."));
        }

        if (targetMessage.isPinned()) {
            targetMessage.unpin().queue();
            //content = "Successfully unpinned message."; Discord automatically sends a message after pinning
        } else {
            targetMessage.pin().queue();
            content = "Successfully pinned message.";
        }

        return Optional.of(MessageUtil.buildMessage(content));
    }
}
