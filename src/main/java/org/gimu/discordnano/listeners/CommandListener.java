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
import org.gimu.discordnano.lib.NanoExecutor;
import org.gimu.discordnano.lib.NanoMessage;
import org.reflections.Reflections;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class CommandListener extends ListenerAdapter {

    public static HashMap<String, Class> commands = new HashMap<>();
    public static Guild guild;

    private Set<String> whitelist = new HashSet<String>();

    private ChatterBotFactory factory = new ChatterBotFactory();
    private ChatterBot bot = null;

    @Override
    public void onReady(ReadyEvent event) {
        DiscordNano.jda.getAccountManager().setGame(DiscordNano.DEFAULT_STATUS);

        // Register commands
        // TODO: refactor
        Reflections reflections = new Reflections("org.gimu.discordnano.commands");
        Set<Class<? extends NanoExecutor>> allCommands = reflections.getSubTypesOf(NanoExecutor.class);

        for (Class<? extends NanoExecutor> command : allCommands) {
            Class cls = null;
            Object instance = null;
            String[] triggers = null;
            Field field = null;
            try {
                cls = Class.forName(command.getName());
                instance = cls.newInstance();
                field = cls.getField("triggers");
                triggers = (String[])field.get(instance);
            } catch (Exception e) {
                e.printStackTrace();
            }

            for (String trigger : triggers) {
                commands.put(trigger, cls);
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

        guild = event.getGuild();

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
            ChatterBotSession bot1session = bot.createSession();

            String response = "";
            try {
                response = bot1session.think(message.getRawContent());
            } catch (Exception e) {
                e.printStackTrace();
            }
            message.getChannel().sendMessage(response);
        }

        if (!messageContent.startsWith(DiscordNano.prefix) || author.isBot() || author == jda.getSelfInfo()) return;

        String[] sections = messageContent.split(" ");
        String command = sections[0].replace(DiscordNano.prefix, "");
        String[] args = Arrays.copyOfRange(sections, 1, sections.length); // without command

        if (commands.get(command.toLowerCase()) != null) {
            Class cls = commands.get(command.toLowerCase());
            Method method = null;
            Object instance = null;
            try {
                instance = cls.newInstance();
                method = cls.getMethod("respond", NanoMessage.class, String[].class);
                method.invoke(instance, new NanoMessage(event.getMessage(), event.getGuild()), args);
            } catch (InvocationTargetException e) {
                // TODO: print out usage text
                System.out.println(e.getTargetException());
                System.out.println(method.getDeclaringClass());
                System.out.println(method.getName());

                String usageText = null;
                Field field = null;
                try {
                    // TODO: fix this mess
                    field = cls.getField("usage");
                    usageText = (String)field.get(instance);
                } catch (Exception er) {
                    er.printStackTrace();
                }
                StringBuilder sb = new StringBuilder();
                sb.append("```");
                sb.append("Usage: " + DiscordNano.prefix + command.toLowerCase() + " ");
                sb.append(usageText);
                sb.append("```");
                event.getChannel().sendMessage(sb.toString());
            } catch (Exception er) {
                er.printStackTrace();
            }
        } else if (command.equalsIgnoreCase("clear")) {
            while (channel.getHistory().retrieve(100) != null) {
                channel.deleteMessages(channel.getHistory().retrieve(100));
            }
        }

        /*if (channel.checkPermission(jda.getSelfInfo(), Permission.MESSAGE_MANAGE))
            channel.deleteMessageById(message.getId());*/

    }
}