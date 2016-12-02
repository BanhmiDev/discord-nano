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

package org.gimu.discordnano.commands.music;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import org.gimu.discordnano.DiscordNano;
import org.gimu.discordnano.commands.AbstractSubCommand;
import org.gimu.discordnano.commands.SubCommand;
import org.gimu.discordnano.lib.MessageUtil;
import org.gimu.discordnano.util.HastebinUtil;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@SubCommand(
        mainCommandAlias = "music",
        alias = {"list"},
        description = "List the music library",
        usage = "music list"
)
public class ListSubCommand extends AbstractSubCommand {

    public ListSubCommand(String description, String usage) {
        super(description, usage);
    }

    public Optional execute(User author, Message message, String[] args) throws IllegalArgumentException {
        LinkedHashMap<String, String> musicLibraryMap = DiscordNano.musicLibrary.getLibraryMap();

        StringBuilder content = new StringBuilder("The music library has " + musicLibraryMap.size() + " entries.\n\n");
        int iterator = 0;
        if (musicLibraryMap.size() == 0) {
            content.append("The music library is empty.");
        } else if (musicLibraryMap.size() <= 10) {
            for (Map.Entry<String, String> entry : musicLibraryMap.entrySet()) {
                content.append("**[" + iterator + "]** " + entry.getKey() + " **<" + entry.getValue() + ">**\n");
                iterator++;
            }
        } else {
            StringBuilder body = new StringBuilder();
            for (Map.Entry<String, String> entry : musicLibraryMap.entrySet()) {
                body.append("[" + iterator + "] " + entry.getKey() + " <" + entry.getValue() + ">\n");
                iterator++;
            }
            content.append(HastebinUtil.post(body.deleteCharAt(body.length()-1).toString()));
        }

        Message response = MessageUtil.buildFramedMessage(author, "disabled", true);
        return Optional.of(response);
    }
}
