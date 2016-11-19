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
import org.gimu.discordnano.util.MathUtil;
import sx.blah.discord.handle.impl.obj.Message;

import java.text.DecimalFormat;
import java.util.Optional;

@MainCommand(
        alias = {"math"},
        description = "Evaluates mathematical expressions",
        usage = "math <expression>"
)
public class MathCommand extends AbstractCommand {

    private final DecimalFormat formatter = new DecimalFormat() {{setDecimalSeparatorAlwaysShown(false);}};

    public MathCommand(String description, String usage) {
        super(description, usage);
    }

    public Optional execute(Message message, String[] args) throws IllegalArgumentException {
        if (args.length == 0) {
            throw new IllegalArgumentException();
        }

        String exp = StringUtils.join(args, " ");
        double result = 0.0;
        try {
            result = new MathUtil(exp).eval();
        } catch (RuntimeException e) {
            return Optional.of("I'm too stupid for that");
        }
        return Optional.of("Final answer for `" + exp + "`: `" + formatter.format(result) + "`");
    }

}
