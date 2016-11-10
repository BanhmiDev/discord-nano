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

import java.util.*;

public abstract class AbstractCommand implements CommandExecutor {

    private String description;
    private String usage;
    private List<String> aliases;
    private HashMap<String, AbstractSubCommand> subCommandMap = new HashMap<String, AbstractSubCommand>();

    public void addSubCommand(String alias, AbstractSubCommand subCommand) { // todo: all aliases from subcommand, stored in class level
        //for (String alias : subCommand.getAliases()) {
            //if (subCommandMap.get(alias) != null) ?
            subCommandMap.put(alias, subCommand);
        //}
    }

    public boolean hasSubCommand(String input) {
        return !subCommandMap.isEmpty() && subCommandMap.get(input) != null;
    }

    public AbstractSubCommand getSubCommand(String input) {
        return subCommandMap.get(input);
    }

    public String getDescription() {
        return description;
    }

    public String getUsage() {
        return usage;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public Map<String, AbstractSubCommand> getSubCommandMap() {
        return subCommandMap;
    }
}
