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
import net.dv8tion.jda.JDA;
import net.dv8tion.jda.entities.*;
import net.dv8tion.jda.events.ReadyEvent;
import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;
import org.gimu.discordnano.DiscordNano;
import org.gimu.discordnano.commands.*;
import org.gimu.discordnano.lib.NanoLogger;
import org.gimu.discordnano.lib.NanoMessage;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

public class CommandListener extends ListenerAdapter {

    private CommandHandler commandHandler = new CommandHandler();

    private Set<String> whitelist = new HashSet<String>();
    private ChatterBotFactory factory = new ChatterBotFactory();
    private ChatterBot bot = null;


    @Override
    public void onReady(ReadyEvent event) {
        DiscordNano.jda.getAccountManager().setGame(DiscordNano.DEFAULT_STATUS);

        // Init commands
        NanoLogger.debug("Initializing main commands");
        Reflections reflections = new Reflections("org.gimu.discordnano.commands");
        Set<Class<? extends AbstractCommand>> allCommands = reflections.getSubTypesOf(AbstractCommand.class);

        for (Class<? extends AbstractCommand> command : allCommands) {
            Annotation[] annotations = command.getAnnotations();

            for (Annotation annotation : annotations) {
                if (annotation instanceof MainCommand) {
                    MainCommand myAnnotation = (MainCommand) annotation;

                    for (String alias : myAnnotation.alias()) {
                        try {
                            commandHandler.addMainCommand(alias, command.newInstance());
                        } catch (InstantiationException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        // Init sub commands
        NanoLogger.debug("Initializing sub commands");
        Set<Class<? extends AbstractSubCommand>> allSubCommands = reflections.getSubTypesOf(AbstractSubCommand.class);

        for (Class<? extends AbstractSubCommand> command : allSubCommands) {
            Annotation[] annotations = command.getAnnotations();

            for (Annotation annotation : annotations) {
                if (annotation instanceof SubCommand) {
                    SubCommand myAnnotation = (SubCommand) annotation;

                    for (String alias : myAnnotation.alias()) {
                        try {
                            commandHandler.addSubCommand(alias, myAnnotation.mainCommandAlias(), command.newInstance());
                        } catch (InstantiationException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        TextChannel channel = event.getChannel();
        JDA jda = event.getJDA();
        Message message = event.getMessage();
        String messageContent = message.getRawContent();
        User author = event.getAuthor();

        if (DiscordNano.TESTMODE && !channel.getId().equals(DiscordNano.TESTCHANNEL_ID)) {
            channel.sendMessage("Currently in test-mode, not accepting commands from you .( ̵˃﹏˂̵ )");
            return;
        /*} else if (!whitelist.contains(channel.getId())) {
            channel.sendMessage("Server is not whitelisted .( ̵˃﹏˂̵ )");
            return;*/
        }

        // Conversation (CleverBot)
        if (message.isMentioned(DiscordNano.jda.getSelfInfo())) {
            try {
                bot = factory.create(ChatterBotType.CLEVERBOT);
            } catch (Exception e) {
                e.printStackTrace();
            }
            ChatterBotSession botSession = bot.createSession();

            String response = "";
            try {
                response = botSession.think(message.getRawContent());
            } catch (Exception e) {
                e.printStackTrace();
            }
            message.getChannel().sendMessage(response);
        }

        if (!messageContent.startsWith(DiscordNano.prefix) || author.isBot() || author == jda.getSelfInfo()) return;

        commandHandler.parseMessage(new NanoMessage(message, event.getGuild()));

        /*
            while (channel.getHistory().retrieve(100) != null) {
                channel.deleteMessages(channel.getHistory().retrieve(100));
            }
        }*/
    }
}