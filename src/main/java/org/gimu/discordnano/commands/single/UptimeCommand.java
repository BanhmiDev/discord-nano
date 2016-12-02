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
import net.dv8tion.jda.core.entities.User;
import org.gimu.discordnano.DiscordNano;
import org.gimu.discordnano.commands.AbstractCommand;
import org.gimu.discordnano.commands.MainCommand;
import org.gimu.discordnano.lib.MessageUtil;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@MainCommand(
        alias = "uptime",
        description = "Get Nano's uptime",
        usage = "uptime"
)
public class UptimeCommand extends AbstractCommand {

    public UptimeCommand(String description, String usage, String alias) {
        super(description, usage, alias);
    }

    public Optional execute(User author, Message message, String[] args) {
        long duration = (System.currentTimeMillis() - DiscordNano.START_TIME) / 1000;
        long days = TimeUnit.SECONDS.toDays(duration);
        duration -= TimeUnit.DAYS.toSeconds(days);

        long hours = TimeUnit.SECONDS.toHours(duration);
        duration -= TimeUnit.HOURS.toSeconds(hours);

        long minutes = TimeUnit.SECONDS.toMinutes(duration);
        duration -= TimeUnit.MINUTES.toSeconds(minutes);

        long seconds = TimeUnit.SECONDS.toSeconds(duration);

        StringBuilder sb = new StringBuilder("Uptime: ");
        if (days != 0) {
            sb.append(days + " day(s) ");
        }
        if (hours != 0) {
            sb.append(hours + " hours(s) ");
        }
        if (minutes != 0) {
            sb.append(minutes + " minutes(s) ");
        }
        if (seconds != 0) {
            sb.append(seconds + " seconds(s)");
        }

        return Optional.of(MessageUtil.buildMessage(sb.toString()));
    }
}
