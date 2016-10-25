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

import org.gimu.discordnano.lib.NanoExecutor;
import org.gimu.discordnano.lib.NanoMessage;

public class MALExecutor extends NanoExecutor {

    public String[] triggers = {"mal"};
    public String description = "Search and display anime/manga from MyAnimeList";
    public String usage = "<user|anime|manga> <query|view <index>>";

    @Override
    public void respond(NanoMessage message, String[] args) throws IllegalArgumentException {
        if (args.length == 0) {
            throw new IllegalArgumentException();
        }

        String command = args[0].toLowerCase();
        if (command.equals("user")) {
            message.replyFramed("MyAnimeList Profile", UserCommand.respond(args));
        } else if (command.equals("anime")) {
            message.reply(AnimeCommand.respond(args));
        } else if (command.equals("manga")) {
            message.reply(MangaCommand.respond(args));
        } else {
            throw new IllegalArgumentException();
        }
    }
}
