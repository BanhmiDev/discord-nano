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
import org.gimu.discordnano.DiscordNano;
import org.gimu.discordnano.commands.AbstractCommand;
import org.gimu.discordnano.commands.MainCommand;
import org.gimu.discordnano.lib.EmbedFieldListBuilder;
import org.gimu.discordnano.lib.MessageUtil;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.text.NumberFormat;
import java.util.Optional;

@MainCommand(
        alias = "choose",
        description = "Chooses",
        usage = "choose <term1;term2...>"
)
public class ChooseCommand extends AbstractCommand {

    public ChooseCommand(String description, String usage, String alias) {
        super(description, usage, alias);
    }

    public Optional execute(User author, Message message, String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException();
        }

        String content;
        String[] choices = String.join(" ", args).split("\\;");
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
                content = "**" + choices[0] + "** or **" + choices[1] + "**?\nI'll go with **" + choices[choose] + "**.";
            } else {
                String result = "**" + choices[0] + "**";
                for (int i = 1; i < choices.length-1; i++) {
                    result += ", **" + choices[i] + "**";
                }
                result += " or **" + choices[choices.length-1] + "**?\n";
                content = result + "I'll go with **" + choices[choose] + "**.";
            }
        }
        MessageBuilder response = new MessageBuilder().appendString(content);
        return Optional.of(response.build());
    }
}
