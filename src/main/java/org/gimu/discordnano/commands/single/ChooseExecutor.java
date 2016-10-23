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

import org.gimu.discordnano.commands.NanoExecutor;
import org.gimu.discordnano.util.NanoMessage;

public class ChooseExecutor extends NanoExecutor {

    public String[] triggers = {"choose"};
    public String description = "Chooses an option from a list of options";
    public String usage = "<option1;option2...>";

    public void respond(NanoMessage message, String[] args) throws IllegalArgumentException {
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
                message.reply("**" + choices[0] + "** or **" + choices[1] + "**?\nI'll go with **" + choices[choose] + "**.");
            } else {
                String result = "**" + choices[0] + "**";
                for (int i = 1; i < choices.length-1; i++) {
                    result += ", **" + choices[i] + "**";
                }
                result += " or **" + choices[choices.length-1] + "**?\n";
                message.reply(result + "I'll go with **" + choices[choose] + "**.");
            }
        }
    }
}
