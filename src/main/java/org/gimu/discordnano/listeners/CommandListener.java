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
import org.gimu.discordnano.lib.NanoDatabase;
import org.gimu.discordnano.lib.NanoGuild;
import org.gimu.discordnano.lib.NanoGuildLibrary;
import org.gimu.discordnano.lib.NanoLogger;
import org.reflections.Reflections;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.DiscordDisconnectedEvent;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.obj.Message;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.Status;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CommandListener {

    private CommandHandler commandHandler = new CommandHandler();

    public static IDiscordClient client;

    public CommandListener(IDiscordClient client) {
        this.client = client;
    }

    @EventSubscriber
    public void onReady(ReadyEvent event) {
        //CommandListener.client.changeStatus(Status.game("nothing"));

        // Init guild stuff
        NanoLogger.debug("Initializing guild library");

        Connection conn = NanoDatabase.getConnection();
        try {
            // Add guilds from database
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT guild_id, textchannel, voicechannel FROM NanoGuilds");
            while (rs.next()) {
                if (!rs.getString("textchannel").isEmpty()) {
                    DiscordNano.guildLibrary.add(rs.getString("guild_id"), new NanoGuild(rs.getString("textchannel"), rs.getString("voicechannel")));
                    NanoLogger.debug("Registering from DB: " + rs.getString("guild_id") + " - " + rs.getString("textchannel"));
                } else {
                    DiscordNano.guildLibrary.remove(rs.getString("guild_id"));
                    NanoLogger.debug("Corrupt guild information, removing from database");
                }
            }
        } catch (SQLException ex) {
            // ...
        }

        // Add possible new guilds
        List<IGuild> guilds = event.getClient().getGuilds();
        guilds.forEach(guild -> {
            // Set the first text channel for Nano
            DiscordNano.guildLibrary.add(guild.getID(), guild.getChannels().get(0).getID());
        });

        // Init commands
        NanoLogger.debug("Initializing main commands");
        Reflections reflections = new Reflections("org.gimu.discordnano.commands");
        Set<Class<? extends AbstractCommand>> allCommands = reflections.getSubTypesOf(AbstractCommand.class);

        for (Class<? extends AbstractCommand> command : allCommands) {
            Annotation[] annotations = command.getAnnotations();

            for (Annotation annotation : annotations) {
                if (annotation instanceof MainCommand) {
                    MainCommand mainAnnotation = (MainCommand) annotation;

                    for (String alias : mainAnnotation.alias()) {
                        try {
                            commandHandler.addMainCommand(alias, command.getDeclaredConstructor(String.class, String.class).newInstance(mainAnnotation.description(), mainAnnotation.usage()));
                        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
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
                    SubCommand subAnnotation = (SubCommand) annotation;

                    for (String alias : subAnnotation.alias()) {

                        try {
                            commandHandler.addSubCommand(alias, subAnnotation.mainCommandAlias(), command.getDeclaredConstructor(String.class, String.class).newInstance(subAnnotation.description(), subAnnotation.usage()));
                        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
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
        /*} else if (!whitelist.contains(channel.getId())) {
            channel.sendMessage("Server is not whitelisted .( ̵˃﹏˂̵ )");
            return;*/

        NanoGuild nanoGuild = DiscordNano.guildLibrary.get(message.getGuild().getID());
        if (!message.getContent().startsWith(DiscordNano.PREFIX) || !message.getChannel().getID().equals(nanoGuild.getTextchannel()) || message.getAuthor().isBot()) return;

        try {
            commandHandler.parseMessage(message);
        } catch (RateLimitException e) {
            e.printStackTrace();
        } catch (DiscordException e) {
            e.printStackTrace();
        } catch (MissingPermissionsException e) {
            e.printStackTrace();
        }

    }
}