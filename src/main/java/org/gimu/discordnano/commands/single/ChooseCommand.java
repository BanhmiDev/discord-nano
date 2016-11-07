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

import org.gimu.discordnano.commands.AbstractCommand;
import org.gimu.discordnano.commands.CommandExecutor;
import org.gimu.discordnano.commands.MainCommand;
import org.gimu.discordnano.lib.NanoMessage;

import java.util.Optional;

@MainCommand(
        alias = {"choose"},
        description = "Chooses from a minimum of two options",
        usage = "<option1;option2...>"
)
public class ChooseCommand extends AbstractCommand {

    public Optional execute(NanoMessage message, String[] args) throws IllegalArgumentException {
        String response;
        if (args.length == 0) {
            throw new IllegalArgumentException();
        }
        String[] choices = args[0].split("\\;");
        if (choices.length < 2) {
            throw new IllegalArgumentException();
        } else {
            for (int i = 0; i < choices.length; i++) {
                if (choices[i].equals("")) {
                    throw new IllegalArgumentException();
                }
            }

            int choose = (int) (Math.random() * choices.length - 1);
            if (choices.length == 2) {
                response = "**" + choices[0] + "** or **" + choices[1] + "**?\nI'll go with **" + choices[choose] + "**.";
            } else {
                String result = "**" + choices[0] + "**";
                for (int i = 1; i < choices.length-1; i++) {
                    result += ", **" + choices[i] + "**";
                }
                result += " or **" + choices[choices.length-1] + "**?\n";
                response = result + "I'll go with **" + choices[choose] + "**.";
            }
        }
        return Optional.of(response);
    }
}
