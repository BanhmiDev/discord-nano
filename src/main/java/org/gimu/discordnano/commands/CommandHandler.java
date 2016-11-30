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
import org.gimu.discordnano.DiscordNano;
import org.gimu.discordnano.lib.NanoLogger;

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

    public void parseMessage(Message message) {
        String[] args;
        String messageRaw = message.getContent();
        String[] sections = messageRaw.split(" "); // Split message by whitespace
        String commandString = sections[0].replace(DiscordNano.PREFIX, ""); // Main command
        String subcommandString = (sections.length >= 2) ? sections[1] : ""; // Sub command

        Optional<Message> response = null;

        // Main command parsing
        AbstractCommand mainCommand = mainCommandMap.get(commandString.toLowerCase());

        if (mainCommand != null) {
            // Sub command parsing
            AbstractSubCommand subCommand = mainCommand.getSubCommand(subcommandString);

            if (subCommand != null) {
                try {
                    args = (sections.length >= 2) ? Arrays.copyOfRange(sections, 2, sections.length) : new String[0]; // Only arguments (excludes sub command alias)
                    //message.getChannel().sendTyping().queue();
                    response = subCommand.execute(message.getAuthor(), message, args);
                } catch (IllegalArgumentException e) {
                    NanoLogger.error(e.getMessage());
                    if (!subCommand.getUsage().isEmpty()) message.getChannel().sendMessage("`" + DiscordNano.PREFIX + subCommand.getUsage() + "`").queue();
                }
            } else {
                try {
                    args = (sections.length >= 1) ? Arrays.copyOfRange(sections, 1, sections.length) : new String[0]; // Only arguments (excludes main command alias)
                    //message.getChannel().sendTyping().queue();
                    response = mainCommand.execute(message.getAuthor(), message, args);
                } catch (IllegalArgumentException e) {
                    NanoLogger.error(e.getMessage());
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

        if (response != null && response.isPresent()) {
            Message responseMessage = response.get();
            message.getChannel().sendMessage(responseMessage).queue();
        }
    }
}
