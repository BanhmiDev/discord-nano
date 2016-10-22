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
package org.gimu.discordnano.commands.text;

import org.gimu.discordnano.commands.NanoExecutor;
import org.gimu.discordnano.util.NanoMessage;

public class RPSCommand extends NanoExecutor {

    public String[] triggers = {"rps"};
    public String description = "Plays rock-paper-scissors";
    public String usage = "<rock|paper|scissors>";

    public void respond(NanoMessage message, String[] args) throws IllegalArgumentException {
        if (args.length == 0 || (!args[0].equalsIgnoreCase("rock") && !args[0].equalsIgnoreCase("paper") && !args[0].equalsIgnoreCase("scissors"))) {
            throw new IllegalArgumentException();
        }

        int choice = (int)Math.floor(Math.random() * 3);
        if (choice == 0) {
            message.reply("You picked " + args[0] + ".\nI picked **rock**!");
        } else if (choice == 1) {
            message.reply("You picked " + args[0] + "\nI picked **paper**!");
        } else if (choice == 2) {
            message.reply("You picked " + args[0] + "\nI picked **scissors**!");
        }
    }
}
