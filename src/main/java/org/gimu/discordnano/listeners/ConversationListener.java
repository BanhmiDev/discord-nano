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

package org.gimu.discordnano.listeners;

import com.google.code.chatterbotapi.ChatterBot;
import com.google.code.chatterbotapi.ChatterBotFactory;
import com.google.code.chatterbotapi.ChatterBotSession;
import com.google.code.chatterbotapi.ChatterBotType;
import org.gimu.discordnano.DiscordNano;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

public class ConversationListener {

    public static IDiscordClient client;

    private ChatterBotFactory factory = new ChatterBotFactory();
    private ChatterBot bot = null;

    public ConversationListener(IDiscordClient client) {
        this.client = client;
    }

    @EventSubscriber
    public void onMessageReceivedEvent(MessageReceivedEvent event) {
        IMessage message = event.getMessage();

        // Conversation (CleverBot)
        if (message.getMentions().contains(client.getOurUser())) {
            message.getChannel().setTypingStatus(true);
            try {
                bot = factory.create(ChatterBotType.CLEVERBOT);
            } catch (Exception e) {
                e.printStackTrace();
            }
            ChatterBotSession botSession = bot.createSession();

            String response = "";
            try {
                response = botSession.think(message.getContent());
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                message.getChannel().sendMessage(response);
                message.getChannel().setTypingStatus(false);
            } catch (MissingPermissionsException e) {
                e.printStackTrace();
            } catch (RateLimitException e) {
                e.printStackTrace();
            } catch (DiscordException e) {
                e.printStackTrace();
            }
        }
    }
}
