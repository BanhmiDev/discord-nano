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
import org.gimu.discordnano.DiscordNano;
import org.gimu.discordnano.commands.*;
import org.gimu.discordnano.lib.NanoLogger;
import org.reflections.Reflections;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.obj.Message;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

public class CommandListener {

    private CommandHandler commandHandler = new CommandHandler();

    private Set<String> whitelist = new HashSet<String>();
    private ChatterBotFactory factory = new ChatterBotFactory();
    private ChatterBot bot = null;


    @EventSubscriber
    public void onReady(ReadyEvent event) {
        //DiscordNano.JDA.getAccountManager().setGame(DiscordNano.DEFAULT_STATUS);

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

    @EventSubscriber
    public void onMessageReceivedEvent(MessageReceivedEvent event) {
        IChannel channel = event.getMessage().getChannel();
        Message message = (Message)event.getMessage();
        if (!DiscordNano.PRODUCTION && !channel.getID().equals(DiscordNano.TESTCHANNEL_ID)) {
            try {
                channel.sendMessage("Currently in test-mode, not accepting commands from you .( ̵˃﹏˂̵ )");
            } catch (MissingPermissionsException e) {
                e.printStackTrace();
            } catch (RateLimitException e) {
                e.printStackTrace();
            } catch (DiscordException e) {
                e.printStackTrace();
            }
            return;
        /*} else if (!whitelist.contains(channel.getId())) {
            channel.sendMessage("Server is not whitelisted .( ̵˃﹏˂̵ )");
            return;*/
        }

        // Conversation (CleverBot)
        /*if (message.isMentioned(DiscordNano.JDA.getSelfInfo())) {
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
        }*/

        if (!message.getContent().startsWith(DiscordNano.PREFIX) || message.getAuthor().isBot()) return;

        try {
            commandHandler.parseMessage(message);
        } catch (RateLimitException e) {
            e.printStackTrace();
        } catch (DiscordException e) {
            e.printStackTrace();
        } catch (MissingPermissionsException e) {
            e.printStackTrace();
        }

        /*
            while (channel.getHistory().retrieve(100) != null) {
                channel.deleteMessages(channel.getHistory().retrieve(100));
            }
        }*/
    }
}