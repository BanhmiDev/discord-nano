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

import org.gimu.discordnano.DiscordNano;
import org.gimu.discordnano.commands.NanoExecutor;
import org.gimu.discordnano.util.NanoMessage;

import java.util.concurrent.TimeUnit;

public class UptimeCommand extends NanoExecutor {

    public String[] triggers = {"uptime"};
    public String description = "Show Nano's uptime";
    public String usage = "";

    public void respond(NanoMessage message, String[] args) {
        long duration = (System.currentTimeMillis() - DiscordNano.START_TIME) / 1000;
        long days = TimeUnit.SECONDS.toDays(duration);
        duration -= TimeUnit.DAYS.toSeconds(days);

        long hours = TimeUnit.SECONDS.toHours(duration);
        duration -= TimeUnit.HOURS.toSeconds(hours);

        long minutes = TimeUnit.SECONDS.toMinutes(duration);
        duration -= TimeUnit.MINUTES.toSeconds(minutes);

        long seconds = TimeUnit.SECONDS.toSeconds(duration);

        StringBuilder msg = new StringBuilder( );
        if (days!=0) {
            msg.append(days + " day(s) ");
        }
        if (hours!=0) {
            msg.append(hours + " hours(s) ");
        }
        if (minutes!=0) {
            msg.append(minutes + " minutes(s) ");
        }
        if (seconds!=0) {
            msg.append(seconds + " seconds(s)");
        }
        message.reply("**Uptime**: " + msg.toString());
    }
}
