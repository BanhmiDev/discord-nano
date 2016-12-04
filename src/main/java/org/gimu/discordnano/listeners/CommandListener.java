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

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.gimu.discordnano.DiscordNano;
import org.gimu.discordnano.commands.*;
import org.gimu.discordnano.lib.MessageUtil;
import org.gimu.discordnano.lib.NanoDatabase;
import org.gimu.discordnano.lib.NanoGuild;
import org.gimu.discordnano.lib.NanoLogger;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.List;
import java.util.Set;

public class CommandListener extends ListenerAdapter {

    private CommandHandler commandHandler = new CommandHandler();

    public static JDA jda;

    public CommandListener(JDA jda) {
        this.jda = jda;
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        // Welcome message
        event.getGuild().getTextChannels().get(0).sendMessage(MessageUtil.buildFramedMessage(null, "Type `!help` if you don't know what you are doing.\nUse `!mod textchannel <id>` if you don't want me in this text channel.\nThank you for letting me stay.", true)).queue();
        // Add to guild library
        DiscordNano.guildLibrary.add(event.getGuild());
        NanoLogger.debug("Joined guild for the first time");
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        // Remove from guild library
        DiscordNano.guildLibrary.remove(event.getGuild().getId());
        NanoLogger.debug("Left guild");
    }

    @Override
    public void onTextChannelDelete(TextChannelDeleteEvent event) {
        // Fallback to first text channel in list
        Guild guild = event.getGuild();
        DiscordNano.guildLibrary.setTextchannel(guild.getId(), guild.getTextChannels().get(0).getId());
    }

    @Override
    public void onReady(ReadyEvent event) {
        // Init music library
        NanoLogger.debug("Initializing music library");

        Connection conn = NanoDatabase.getConnection(); // Create database connection
        try {
            // Add guilds from database
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT source FROM MusicLibrary");
            while (rs.next()) {
                DiscordNano.musicLibrary.add(rs.getString("source"));
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        // Init guild stuff
        NanoLogger.debug("Initializing guild library");
        List<Guild> guilds = jda.getGuilds();
        guilds.forEach(guild -> {
            DiscordNano.guildLibrary.add(guild);
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

                    if (mainAnnotation.isEnabled()) {
                        String alias = mainAnnotation.alias();
                        try {
                            commandHandler.addMainCommand(alias, command.getDeclaredConstructor(String.class, String.class, String.class).newInstance(mainAnnotation.description(), mainAnnotation.usage(), mainAnnotation.alias()));
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

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        Message message = event.getMessage();

        NanoGuild nanoGuild = DiscordNano.guildLibrary.get(message.getGuild().getId());
        if (!message.getContent().startsWith(DiscordNano.PREFIX) || !message.getChannel().getId().equals(nanoGuild.getTextchannel()) || message.getAuthor().isBot()) return;

        commandHandler.parseMessage(message);
    }
}