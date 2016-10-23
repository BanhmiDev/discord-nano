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

import org.apache.commons.lang3.math.NumberUtils;
import org.gimu.discordnano.commands.NanoExecutor;
import org.gimu.discordnano.util.NanoMessage;

import java.util.Random;

public class RollExecutor extends NanoExecutor {

    public String[] triggers = {"roll"};
    public String description = "Rolls a dice";
    public String usage = "[faces]";

    public void respond(NanoMessage message, String[] args) {
        Random random = new Random();
        int rolled;
        if (args.length == 0) {
            rolled = 1 + random.nextInt(6);
            message.reply(" Rolled dice and got **" + rolled + "**.");
        } else if (NumberUtils.isNumber(args[0]) && Integer.parseInt(args[0]) > 0) {
            rolled = 1 + random.nextInt(Integer.parseInt(args[0]));
            message.reply(" Rolled dice with " + args[0] + " faces and got **" + rolled + "**.");
        } else {
            message.reply("Please give me a valid number!");
        }
    }
}
