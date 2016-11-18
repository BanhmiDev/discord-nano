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
import sx.blah.discord.handle.impl.obj.Message;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;

import java.util.LinkedList;
import java.util.Optional;

@MainCommand(
        alias = {"serverinfo"},
        description = "Gets server information",
        usage = "serverinfo"
)
public class ServerinfoCommand extends AbstractCommand {

    public ServerinfoCommand(String description, String usage) {
        super(description, usage);
    }

    public Optional execute(Message message, String[] args) {
        IGuild guild = message.getGuild();
        StringBuilder response = new StringBuilder();
        response.append("**Server**: " + guild.getName() + "\n");
        response.append("**ID**: " + guild.getID() + "\n");
        response.append("**Creation**: " + guild.getCreationDate() + "\n");
        response.append("**Roles**: ");
        LinkedList<IRole> roles = new LinkedList<IRole>(guild.getRoles());
        for (int i = 0; i < roles.size()-1; i++) {
            System.out.println(roles.get(i));
            if (!roles.get(i).toString().equals("@everyone")) response.append(roles.get(i) + ", ");
        }
        response.append(roles.get(roles.size()-1) + "\n");
        response.append("**Owner**: " + guild.getOwner().getName() + "#" + guild.getOwner().getDiscriminator() + "\n");
        response.append("**Region**: " + guild.getRegion());
        return Optional.of(response.toString());
    }
}
