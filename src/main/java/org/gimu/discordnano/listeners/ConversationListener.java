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
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class ConversationListener extends ListenerAdapter {

    public static JDA jda;

    private ChatterBotFactory factory = new ChatterBotFactory();
    private ChatterBot bot = null;

    public ConversationListener(JDA jda) {
        this.jda = jda;
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        Message message = event.getMessage();

        // Conversation (CleverBot)
        if (message.isMentioned(jda.getSelfUser())) {
            message.getChannel().sendTyping().queue();
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

            message.getChannel().sendMessage(response).queue();
        }
    }
}
