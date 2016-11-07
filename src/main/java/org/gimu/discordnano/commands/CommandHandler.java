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

    private static HashMap<String, AbstractCommand> mainCommandMap = new HashMap<>();

    public boolean addMainCommand(String alias, AbstractCommand command) {
        if (mainCommandMap.get(alias) != null) {
            return false;
        } else {
            mainCommandMap.put(alias, command);
            return true;
        }
    }

    public boolean addSubCommand(String alias, AbstractCommand mainCommand, AbstractCommand command) {
        if (!mainCommand.hasSubCommand(alias)) {
            mainCommand.addSubCommand(command);
            return true;
        }
        return false;
    }

    public void parseMessage(NanoMessage nanoMessage) {
        String message = nanoMessage.getRawContent();
        String[] sections = message.split(" "); // Split message by whitespace
        String commandString = sections[0].replace(DiscordNano.prefix, ""); // Main command
        String subcommandString = (sections.length >= 1) ? sections[0] : ""; // Sub command
        String[] args = (sections.length >= 1) ? Arrays.copyOfRange(sections, 1, sections.length) : new String[0]; // Only arguments, with possible sub command?

        Optional<String> response = null;

        // Main command parsing
        AbstractCommand mainCommand = mainCommandMap.get(commandString.toLowerCase());
        if (mainCommand != null) {
            // Sub command parsing
            AbstractCommand subCommand = mainCommand.getSubCommand(subcommandString);
            if (subCommand != null) {
                response = subCommand.execute(nanoMessage, args);
            } else {
                response = mainCommand.execute(nanoMessage, args);
            }
        }

        if (response != null && response.isPresent()) {
            nanoMessage.reply(response.orElse(new String()));
        }
    }
}
