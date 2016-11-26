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

import org.gimu.discordnano.commands.AbstractCommand;
import org.gimu.discordnano.commands.MainCommand;
import org.gimu.discordnano.util.PermissionUtil;
import sx.blah.discord.handle.impl.obj.Message;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

import java.util.Optional;

@MainCommand(
		alias = {"clear"},
		description = "Clear messages",
		usage = "clear"
)
public class ClearCommand extends AbstractCommand {

	public ClearCommand(String description, String usage) {
		super(description, usage);
	}

	public Optional execute(Message message, String[] args) throws IllegalArgumentException, RateLimitException, DiscordException, MissingPermissionsException {
		if (!PermissionUtil.isAdmin(message.getAuthor(), message.getGuild())) return Optional.of("I don't listen to you.");
		/*MessageList messages = message.getChannel().getMessages();
		messages.stream().limit(50).forEach(iMessage -> {
			try {
				iMessage.delete();
			} catch (MissingPermissionsException | RateLimitException | DiscordException e) {
				// ...
			}
		});
		message.delete();*/
		return Optional.of("fuck you");
	}
}
