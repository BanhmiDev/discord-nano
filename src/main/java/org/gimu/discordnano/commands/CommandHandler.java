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

import org.gimu.discordnano.DiscordNano;
import org.gimu.discordnano.lib.NanoMessage;

import java.util.*;

public class CommandHandler {

    private static HashMap<String, AbstractCommand> mainCommandMap = new HashMap<String, AbstractCommand>();

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

    public void parseMessage(NanoMessage nanoMessage) {
        String[] args;
        String message = nanoMessage.getRawContent();
        String[] sections = message.split(" "); // Split message by whitespace
        String commandString = sections[0].replace(DiscordNano.prefix, ""); // Main command
        String subcommandString = (sections.length >= 2) ? sections[1] : ""; // Sub command

        Optional<String> response = null;

        // Main command parsing
        AbstractCommand mainCommand = mainCommandMap.get(commandString.toLowerCase());
        if (mainCommand != null) {
            // Sub command parsing
            AbstractSubCommand subCommand = mainCommand.getSubCommand(subcommandString);

            if (subCommand != null) {
                try {
                    args = (sections.length >= 2) ? Arrays.copyOfRange(sections, 2, sections.length) : new String[0]; // Only arguments (excludes sub command alias)
                    response = subCommand.execute(nanoMessage, args);
                } catch (IllegalArgumentException e) {
                    nanoMessage.reply(mainCommand.getUsage());
                }
            } else {
                try {
                    args = (sections.length >= 1) ? Arrays.copyOfRange(sections, 1, sections.length) : new String[0]; // Only arguments (excludes main command alias)
                    response = mainCommand.execute(nanoMessage, args);
                } catch (IllegalArgumentException e) {
                    nanoMessage.reply(mainCommand.getUsage());
                }
            }
        }

        if (response != null && response.isPresent()) {
            String responseString = response.get();
            if (responseString.trim().length() > 0) {
                nanoMessage.reply(responseString);
            }
        }
    }
}
