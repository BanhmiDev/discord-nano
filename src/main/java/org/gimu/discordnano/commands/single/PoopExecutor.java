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

import org.apache.commons.lang3.StringUtils;
import org.gimu.discordnano.commands.AbstractCommand;
import org.gimu.discordnano.commands.MainCommand;
import org.gimu.discordnano.lib.NanoMessage;

import java.util.Optional;
import java.util.StringJoiner;

@MainCommand(
        alias = {"poop"},
        description = "Poop out text in ASCII format",
        usage = "<text>"
)
public class PoopExecutor extends AbstractCommand {

    public Optional execute(NanoMessage message, String[] args) throws IllegalArgumentException {
        if (args.length == 0) {
            throw new IllegalArgumentException();
        }

        String poop = StringUtils.join(args, " ");

        StringJoiner response = new StringJoiner("\n", "```\n", "```");

        response.add("░░░░░░░░░░░█▀▀░░█░░░░░░");
        response.add("░░░░░░▄▀▀▀▀░░░░░█▄▄░░░░");
        response.add("░░░░░░█░█░░░░░░░░░░▐░░░");
        response.add("░░░░░░▐▐░░░░░░░░░▄░▐░░░");
        response.add("░░░░░░█░░░░░░░░▄▀▀░▐░░░");
        response.add("░░░░▄▀░░░░░░░░▐░▄▄▀░░░░");
        response.add("░░▄▀░░░▐░░░░░█▄▀░▐░░░░░");
        response.add("░░█░░░▐░░░░░░░░▄░▌░░░░░");
        response.add("░░░█▄░░▀▄░░░░▄▀█░▌░░░░░");
        response.add("░░░▌▐▀▀▀░▀▀▀▀░░█░▌░░░░░");
        response.add("░░▐▌▐▄░░▀▄░░░░░█░█▄▄░░░");


        StringBuilder poopArt = new StringBuilder("░░░▀▀░▄███▄▄░░░▀▄▄▄▀░░░");

        for (int i = 0; i < poop.length(); i++) {
            try {
                poopArt.setCharAt(7 + i, poop.charAt(i));
            } catch (Exception e) {
                return Optional.of("It's t-too big (ノдヽ)");
            }
        }

        response.add(poopArt);
        response.add("░░░░░░░░░░░░░░░░░░░░░░░");

        return Optional.of(response.toString());
    }
}
