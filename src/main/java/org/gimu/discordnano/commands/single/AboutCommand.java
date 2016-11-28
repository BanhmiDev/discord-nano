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
        alias = "about",
        description = "About Nano",
        usage = "about"
)
public class AboutCommand extends AbstractCommand {

    public AboutCommand(String description, String usage, String alias) {
        super(description, usage, alias);
    }

    public Optional execute(Message message, String[] args) throws IllegalArgumentException {
        OperatingSystemMXBean bean = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        EmbedFieldListBuilder builder = new EmbedFieldListBuilder();
        String content = "About me";
        builder.append("Environment", bean.getName() + " " + bean.getVersion());
        builder.append("Architecture", bean.getArch());
        builder.append("Available processors", Integer.toString(bean.getAvailableProcessors()));
        builder.append("Average load", bean.getSystemLoadAverage() + "%");
        builder.append("Guilds connected", Integer.toString(DiscordNano.guildLibrary.getLibraryMap().size()) + " total guilds");
        builder.append("Creator", "Gimu#8616");

        Message response = MessageUtil.frameMessage(content, builder.build(), true);
        return Optional.of(response);
    }
}
