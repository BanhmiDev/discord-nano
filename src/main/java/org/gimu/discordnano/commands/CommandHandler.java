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
package org.gimu.discordnano.commands;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import org.gimu.discordnano.DiscordNano;
import org.gimu.discordnano.lib.NanoPermission;

import java.util.*;

public class CommandHandler {

    public static HashMap<String, AbstractCommand> mainCommandMap = new HashMap<String, AbstractCommand>();

    public boolean addMainCommand(String alias, AbstractCommand command) {
        if (mainCommandMap.get(alias) != null) {
            return false;
        } else {
            mainCommandMap.put(alias, command);
            return true;
        }
    }

    public boolean addSubCommand(String alias, String mainCommandAlias, AbstractSubCommand command) {
        AbstractCommand mainCommand = mainCommandMap.get(mainCommandAlias);
        if (mainCommand != null) {
            mainCommand.addSubCommand(alias, command);
            return true;
        }
        return false;
    }

    public void parseMessage(Message message) throws IllegalArgumentException {
        String[] sections = message.getContent().split(" "); // Split message by whitespace
        String commandString = sections[0].replace(DiscordNano.PREFIX, ""); // Main command
        String subcommandString = (sections.length >= 2) ? sections[1] : ""; // Sub command

        // Main command parsing
        AbstractCommand mainCommand = mainCommandMap.get(commandString.toLowerCase());

        if (mainCommand != null) {
            // Sub command parsing
            AbstractSubCommand subCommand = mainCommand.getSubCommand(subcommandString);

            if (subCommand != null) {
                try {
                    parseSubCommand(subCommand, message, sections);
                } catch (IllegalArgumentException e) {
                    if (!subCommand.getUsage().isEmpty()) message.getChannel().sendMessage("`" + DiscordNano.PREFIX + subCommand.getUsage() + "`").queue();
                }
            } else {
                try {
                    parseMainCommand(mainCommand, message, sections);
                } catch (IllegalArgumentException e) {
                    if (!mainCommand.getUsage().isEmpty()) {
                        message.getChannel().sendMessage("`" + DiscordNano.PREFIX + mainCommand.getUsage() + "`").queue();
                    } else {
                        // Look for usage descriptions in sub commands
                        StringBuilder sb = new StringBuilder();
                        mainCommand.getSubCommandMap().entrySet().stream().filter(entry -> !entry.getValue().getUsage().isEmpty()).forEach(entry -> sb.append("`" + DiscordNano.PREFIX + entry.getValue().getUsage() + "`\n"));
                        message.getChannel().sendMessage(sb.toString()).queue();
                    }
                }
            }
        }

    }

    private void parseMainCommand(AbstractCommand mainCommand, Message message, String[] sections) throws IllegalArgumentException {
        String[] args = (sections.length >= 1) ? Arrays.copyOfRange(sections, 1, sections.length) : new String[0]; // Only arguments (excludes main command alias)
        NanoPermission permission = mainCommand.getPermission();
        User author = message.getAuthor();
        Optional<Message> response = null;

        if (permission == NanoPermission.BOT_OWNER) {
            // Bot owner specific main command
            if (author.getId().equals(DiscordNano.BOT_OWNER)) {
                response = mainCommand.execute(author, message, args);
            } else {
                message.addReaction("⛔").queue();
            }
        } else if (permission == NanoPermission.GUILD_OWNER) {
            // Guild owner specific main command
            if (author.getId().equals(DiscordNano.BOT_OWNER) || author.getId().equals(message.getGuild().getOwner().getUser().getId())) {
                response = mainCommand.execute(author, message, args);
            } else {
                message.addReaction("⛔").queue();
            }
        } else {
            response = mainCommand.execute(author, message, args);
        }

        if (response != null && response.isPresent()) {
            Message responseMessage = response.get();
            message.getChannel().sendMessage(responseMessage).queue();
        }
    }

    private void parseSubCommand(AbstractSubCommand subCommand, Message message, String[] sections) throws IllegalArgumentException {
        String[] args = (sections.length >= 2) ? Arrays.copyOfRange(sections, 2, sections.length) : new String[0]; // Only arguments (excludes sub command alias)
        Optional<Message> response = subCommand.execute(message.getAuthor(), message, args);

        if (response.isPresent()) {
            Message responseMessage = response.get();
            message.getChannel().sendMessage(responseMessage).queue();
        }
    }
}
