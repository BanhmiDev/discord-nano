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

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;

import net.dv8tion.jda.core.entities.User;
import org.gimu.discordnano.commands.AbstractCommand;
import org.gimu.discordnano.commands.MainCommand;
import org.gimu.discordnano.lib.EmbedFieldListBuilder;
import org.gimu.discordnano.lib.MessageUtil;

import java.util.LinkedList;
import java.util.Optional;

@MainCommand(
        alias = "server",
        description = "Get server information",
        usage = "server"
)
public class ServerCommand extends AbstractCommand {

    public ServerCommand(String description, String usage, String alias) {
        super(description, usage, alias);
    }

    public Optional execute(User author, Message message, String[] args) {
        Guild guild = message.getGuild();

        EmbedFieldListBuilder builder = new EmbedFieldListBuilder();
        builder.append("Server", guild.getName());
        builder.append("ID", guild.getId());
        builder.append("Creation", guild.getCreationTime().toString());

        LinkedList<Role> roles = new LinkedList<Role>(guild.getRoles());
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < roles.size()-1; i++) {
            sb.append(roles.get(i).getName() + ", ");
        }
        sb.append(roles.get(roles.size()-1).getName());
        builder.append("Roles", sb.toString());

        builder.append("Owner", guild.getOwner().getEffectiveName());
        builder.append("Region", guild.getRegion().getName());

        Message response = MessageUtil.buildFramedMessage(author, "Displaying server information", builder.build(), true);
        return Optional.of(response);
    }
}
