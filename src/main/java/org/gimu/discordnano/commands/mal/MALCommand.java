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

import org.gimu.discordnano.commands.AbstractCommand;
import org.gimu.discordnano.commands.MainCommand;
import org.gimu.discordnano.lib.NanoMessage;

import java.util.Optional;

@MainCommand(
        alias = {"mal"},
        description = "Fetches information from MyAnimeList",
        usage = "<user|anime|manga> <query|view <index>>"
)
public class MALCommand extends AbstractCommand {

    public Optional execute(NanoMessage message, String[] args) throws IllegalArgumentException {
        String response = "test";
        if (args.length == 0) {
            throw new IllegalArgumentException();
        }

        String command = args[0].toLowerCase();
        /*if (command.equals("user")) {
            response = "MyAnimeList Profile\n" + UserSubCommand.respond(args);
        } else if (command.equals("anime")) {
            AnimeSubCommand asc = new AnimeSubCommand();
            response = new AnimeSubCommand().execute(message, args);
        } else if (command.equals("manga")) {
            response = new MangaSubCommand().execute(message, args);
        } else {
            throw new IllegalArgumentException();
        }*/

        return Optional.of(response);
    }
}
