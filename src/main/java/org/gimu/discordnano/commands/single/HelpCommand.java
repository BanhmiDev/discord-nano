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

package org.gimu.discordnano.commands.single;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import org.gimu.discordnano.DiscordNano;
import org.gimu.discordnano.commands.AbstractCommand;
import org.gimu.discordnano.commands.CommandHandler;
import org.gimu.discordnano.commands.MainCommand;
import org.gimu.discordnano.lib.EmbedFieldListBuilder;
import org.gimu.discordnano.lib.MessageUtil;
import org.gimu.discordnano.lib.NanoPermission;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

@MainCommand(
        alias = "help",
        description = "Command help",
        usage = ""
)
public class HelpCommand extends AbstractCommand {

    public HelpCommand(String description, String usage, String alias) {
        super(description, usage, alias);
    }

    public Optional execute(User author, Message message, String[] args) {
        EmbedFieldListBuilder builder = new EmbedFieldListBuilder();

        StringBuilder botOwnerCommands = new StringBuilder();
        StringBuilder guildOwnerCommands = new StringBuilder();
        StringBuilder defaultUserCommands = new StringBuilder();

        CommandHandler.mainCommandMap.entrySet().stream().filter(entry -> entry.getValue().getAlias() != null).forEach(entry -> {
            NanoPermission permission = entry.getValue().getPermission();
            if (permission == NanoPermission.BOT_OWNER) {
                botOwnerCommands.append("`" + entry.getValue().getAlias() + "` ");
            } else if (permission == NanoPermission.GUILD_OWNER) {
                guildOwnerCommands.append("`" + entry.getValue().getAlias() + "` ");
            } else {
                defaultUserCommands.append("`" + entry.getValue().getAlias() + "` ");
            }
        });
        builder.append("Developer commands", botOwnerCommands.toString());
        builder.append("Guild owner commands", guildOwnerCommands.toString());
        builder.append("Default user commands", defaultUserCommands.toString());

        /*message.getAuthor().openPrivateChannel().queue(success -> {
            success.sendMessage("Visit https://www.gimu.org/discord-nano for a list of commands!").queue();
        });*/

        //Check your private messages (ﾉ´ヮ´)ﾉ*:･ﾟ✧\n
        Message response = MessageUtil.buildFramedMessage(author, "Command prefix is `" + DiscordNano.PREFIX + "`", builder.build(), true);
        return Optional.of(response);
    }
}
