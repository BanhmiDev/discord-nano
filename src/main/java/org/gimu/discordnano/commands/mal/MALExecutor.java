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
package org.gimu.discordnano.commands.media;

import org.gimu.discordnano.commands.NanoExecutor;
import org.gimu.discordnano.units.MALUnit;
import org.gimu.discordnano.util.NanoMessage;

public class MALCommand extends NanoExecutor {

    private String[] triggers = {"mal"};
    private String description = "Search and display anime/manga from MyAnimeList";
    private String usage = "<anime|manga> <query|view <index>>";

    @Override
    public void respond(NanoMessage message, String[] args) throws IllegalArgumentException {
        if (args.length == 0) {
            throw new IllegalArgumentException();
        }

        String query = args.length >= 2 ? args[1] : "";
        if (args[0].toLowerCase().equals("anime")) {
            if (query.length() == 0) {
                throw new IllegalArgumentException();
            }
            MALUnit.search(message, query);
        }

        String index = args.length >= 2 ? args[1] : "";
        if (args[0].toLowerCase().equals("view")) {
            if (index.length() == 0) {
                MALUnit.viewRecent(message, true);
            }
            MALUnit.view(message, index);
        }
    }
}
