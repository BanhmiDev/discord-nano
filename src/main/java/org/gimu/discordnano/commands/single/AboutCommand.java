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
import org.gimu.discordnano.commands.AbstractCommand;
import org.gimu.discordnano.commands.MainCommand;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.text.NumberFormat;
import java.util.Optional;

@MainCommand(
        alias = {"about"},
        description = "About Nano",
        usage = "about"
)
public class AboutCommand extends AbstractCommand {

    public AboutCommand(String description, String usage) {
        super(description, usage);
    }

    public Optional execute(Message message, String[] args) throws IllegalArgumentException {
        OperatingSystemMXBean bean = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

        StringBuilder sb = new StringBuilder();
        sb.append("**Environment**: " + bean.getName() + " " + bean.getVersion() + "\n");
        sb.append("**Architecture**: " + bean.getArch() + "\n");
        sb.append("**Available processors**: " + bean.getAvailableProcessors() + "\n");
        sb.append("**Average load**: " + bean.getSystemLoadAverage() + "% \n");
        return Optional.of(sb.toString());
    }
}
