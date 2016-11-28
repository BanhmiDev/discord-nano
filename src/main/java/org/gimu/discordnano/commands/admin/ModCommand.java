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
import org.gimu.discordnano.commands.AbstractCommand;
import org.gimu.discordnano.commands.MainCommand;

import java.util.Optional;

@MainCommand(
        alias = "mod",
        description = "Moderation commands",
        isEnabled = false
)
public class ModCommand extends AbstractCommand {

    public ModCommand(String description, String usage, String alias) {
        super(description, usage, alias);
    }

    public Optional execute(Message message, String[] args) throws IllegalArgumentException {
        // TODO: introduce permission bound commands
        return Optional.empty();
    }
}
