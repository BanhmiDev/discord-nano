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

package org.gimu.discordnano.commands.admin;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import org.gimu.discordnano.commands.AbstractCommand;
import org.gimu.discordnano.commands.MainCommand;
import org.gimu.discordnano.lib.NanoLogger;
import org.gimu.discordnano.lib.NanoPermission;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@MainCommand(
        alias = "update",
        description = "Update nano",
        usage = "update"
)
public class UpdateCommand extends AbstractCommand {

    public UpdateCommand(String description, String usage, String alias) {
        super(description, usage, alias);
        this.setPermission(NanoPermission.BOT_OWNER);
    }

    public Optional execute(User author, Message message, String[] args) throws RateLimitedException {
        Message msg = message.getChannel().sendMessage("**Updating myself...**\nFetching source... ").block();
        try {
            Runtime rt = Runtime.getRuntime();

            // Clear old update folder
            try {
                Process rm = rt.exec("rm -rf update");
                rm.waitFor(5, TimeUnit.SECONDS);
            } catch (Exception e) {
                NanoLogger.error(e.getMessage());
            }

            // First phase, fetch from master branch (stored in update folder)
            Process git = rt.exec("git clone https://github.com/gimu/discord-nano.git --branch master --single-branch update");

            if (!git.waitFor(180, TimeUnit.SECONDS)) {
                msg = msg.editMessage(msg.getRawContent() + " [timed out] :poop:\n\n").block();
                return Optional.empty();
            } else if (git.exitValue() != 0) {
                msg = msg.editMessage(msg.getRawContent() + " :poop:\n\n").block();
                return Optional.empty();
            }

            // Second phase, build from source
            msg = msg.editMessage(msg.getRawContent() + "👌🏽\nBuilding from source... ").block();
            Process mvn = rt.exec("mvn clean compile assembly:single");

            if (!mvn.waitFor(360, TimeUnit.SECONDS)) {
                msg = msg.editMessage(msg.getRawContent() + " [timed out] :poop:\n\n").block();
                return Optional.empty();
            } else if (mvn.exitValue() != 0) {
                msg = msg.editMessage(msg.getRawContent() + " :poop:\n\n").block();
                return Optional.empty();
            }

            // Third phase, move jar file
            msg = msg.editMessage(msg.getRawContent() + "👌🏽\nMoving files...").block();
            if (!new File("target/discordnano-1.0-SNAPSHOT-jar-with-dependencies.jar").renameTo(new File("discord-nano.jar"))) {
                msg = msg.editMessage(msg.getRawContent() + " :poop:\n\n").block();
            }

            // Fourth phase, restart bot
            msg = msg.editMessage(msg.getRawContent() + "👌🏽\nRestarting myself... ").block();
            rt.exec("java -jar discord-nano.jar");
            System.exit(0);
        } catch (RuntimeException | InterruptedException | IOException e) {
            NanoLogger.error(e.getMessage());
            msg.editMessage(msg.getRawContent() + "\nSomething went wrong...").queue();
        }

        return Optional.empty();
    }
}
