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

import net.dv8tion.jda.entities.Guild;
import org.gimu.discordnano.lib.NanoExecutor;
import org.gimu.discordnano.lib.NanoMessage;

public class ServerinfoExecutor extends NanoExecutor {

    public String[] triggers = {"serverinfo"};
    public String description = "Get information about the server";
    public String usage = "";

    @Override
    public void respond(NanoMessage message, String[] args) {
        Guild guild = message.getGuild();
        StringBuilder response = new StringBuilder();
        response.append("Server: " + guild.getName() + "\n");
        response.append("ID: " + guild.getId() + "\n");
        response.append("Owner: " + guild.getOwner().getUsername() + "#" + guild.getOwner().getDiscriminator() + "\n");
        response.append("Region: " + guild.getRegion());
        message.replyFramed(response.toString());
    }
}
