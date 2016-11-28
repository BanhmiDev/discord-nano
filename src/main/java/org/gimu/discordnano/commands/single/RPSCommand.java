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

import net.dv8tion.jda.core.entities.Message;
import org.gimu.discordnano.commands.AbstractCommand;
import org.gimu.discordnano.commands.MainCommand;
import org.gimu.discordnano.lib.MessageUtil;

import java.util.Optional;

@MainCommand(
        alias = "rps",
        description = "Play rock-paper-scissors",
        usage = "rps <rock|paper|scissors>"
)
public class RPSCommand extends AbstractCommand {

    public RPSCommand(String description, String usage, String alias) {
        super(description, usage, alias);
    }

    public Optional execute(Message message, String[] args) throws IllegalArgumentException {
        String content = "";
        if (args.length == 0 || (!args[0].equalsIgnoreCase("rock") && !args[0].equalsIgnoreCase("paper") && !args[0].equalsIgnoreCase("scissors"))) {
            throw new IllegalArgumentException();
        } else {
            int choice = (int) Math.floor(Math.random() * 3);
            if (choice == 0) {
                content = "You picked " + args[0] + ".\nI picked **rock**!";
            } else if (choice == 1) {
                content = "You picked " + args[0] + "\nI picked **paper**!";
            } else if (choice == 2) {
                content = "You picked " + args[0] + "\nI picked **scissors**!";
            }
        }

        Message response = MessageUtil.frameMessage(content, true);
        return Optional.of(response);
    }
}
