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
import org.gimu.discordnano.DiscordNano;
import org.gimu.discordnano.commands.AbstractCommand;
import org.gimu.discordnano.commands.MainCommand;
import org.gimu.discordnano.lib.MessageUtil;
import org.gimu.discordnano.lib.NanoLogger;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.Optional;

@MainCommand(
        alias = "eval",
        description = "Eval",
        usage = "eval",
        isEnabled = false
)
public class EvalCommand extends AbstractCommand {

    public EvalCommand(String description, String usage, String alias) {
        super(description, usage, alias);
    }

    public Optional execute(User author, Message message, String[] args) {
        if (!author.getId().equals(DiscordNano.BOT_OWNER) || args.length == 0) {
            message.addReaction("⛔").queue();
            return Optional.empty();
        }

        String input = String.join(" ", args);
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("js");
        engine.put("input", input);
        engine.put("bot", DiscordNano.bot);
        engine.put("author", author);
        engine.put("channel", message.getChannel());
        engine.put("message", message);
        engine.put("music", DiscordNano.musicLibrary);
        engine.put("guild", message.getGuild());
        engine.put("guilds", DiscordNano.guildLibrary);
        engine.put("api", message.getJDA());
        Object evalObject = null;
        try {
            engine.eval("var imports = new JavaImporter(java.io, java.lang, java.util, Packages.net.dv8tion.jda.core, "
                    + "Packages.net.dv8tion.jda.core.entities, Packages.net.dv8tion.jda.core.managers);");
            evalObject = engine.eval("with(imports){" + input + "\n}");
        } catch (Throwable e) {
            evalObject = e;
        }

        if (evalObject != null) {
            String value = evalObject.toString();
            if (value.length() >= 2000) { // Too long
                NanoLogger.debug(value);
            } else {
                return Optional.of(MessageUtil.frameMessage(author, value, true));
            }
            return Optional.empty();
        }

        return Optional.empty();
    }
}
