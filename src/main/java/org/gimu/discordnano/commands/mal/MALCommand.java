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
package org.gimu.discordnano.commands.mal;

import net.dv8tion.jda.core.entities.Message;
import org.gimu.discordnano.commands.AbstractCommand;
import org.gimu.discordnano.commands.MainCommand;

import java.util.Optional;

@MainCommand(
        alias = {"mal"},
        description = "Fetch information from MyAnimeList",
        usage = "mal <user|anime|manga> <query|view <index>>"
)
public class MALCommand extends AbstractCommand {

    public MALCommand(String description, String usage) {
        super(description, usage);
    }

    public Optional execute(Message message, String[] args) throws IllegalArgumentException {
        if (args.length == 0) {
            throw new IllegalArgumentException();
        }

        return Optional.empty();
    }
}
