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
        alias = {"flip", "coin"},
        description = "Flips a coin"
)
public class FlipCommand extends AbstractCommand {

    public Optional<String> execute(NanoMessage message, String[] args) {
        String response = "";
        double random = Math.random();
        if (random < 0.5) {
            response = "**Tails!**";
        } else {
            response = "**Heads!**";
        }
        return Optional.of(response);
    }
}
