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
import org.gimu.discordnano.commands.MainCommand;
import org.gimu.discordnano.lib.NanoMessage;

import java.util.Optional;

@MainCommand(
        alias = {"exhentai"},
        description = "Displays a random ex-hentai entry"
)
public class EXHentaiExecutor extends AbstractCommand {

    public Optional execute(NanoMessage message, String[] args) throws IllegalArgumentException {
        return Optional.of("D-don't push me! I'm working on it~");
    }
}
