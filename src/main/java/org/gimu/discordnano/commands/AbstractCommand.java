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
package org.gimu.discordnano.commands;

import net.dv8tion.jda.entities.MessageChannel;
import org.gimu.discordnano.DiscordNano;
import org.gimu.discordnano.util.NanoMessage;

public abstract class AbstractCommand {

    public String[] triggers = {};
    public String description = "No descriptions provided.";
    public String usage = "";

    protected AbstractCommand() {}

    public abstract void respond(NanoMessage message, String[] args);

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getDescription()
    {
        return description;
    }

    public void setUsage(String usage)
    {
        this.usage = usage;
    }

    public String getUsage()
    {
        return usage;
    }

    public String getUsageText() {
        return "`Usage: " + DiscordNano.prefix + getUsage() + "`";
    }

    public String[] getTriggers() {
        return triggers;
    }

    public void sendUsageText(MessageChannel channel) {
        channel.sendMessage(getUsageText());
    }
}
