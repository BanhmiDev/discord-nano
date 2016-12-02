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

package org.gimu.discordnano.commands.single;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import org.gimu.discordnano.commands.AbstractCommand;
import org.gimu.discordnano.commands.MainCommand;
import org.gimu.discordnano.lib.MessageUtil;

import java.util.Optional;
import java.util.Random;

@MainCommand(
        alias = "decide",
        description = "Decides for you",
        usage = "decide <question>"
)
public class DecideCommand extends AbstractCommand {

    public DecideCommand(String description, String usage, String alias) {
        super(description, usage, alias);
    }

    public Optional execute(User author, Message message, String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException();
        }

        String content;
        Random random = new Random();
        if (random.nextBoolean()) {
            content = "Yes.";
        } else {
            content = "No.";
        }

        return Optional.of(MessageUtil.buildMessage(content));
    }
}
