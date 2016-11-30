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

package org.gimu.discordnano.lib;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import org.gimu.discordnano.DiscordNano;

import java.awt.*;
import java.util.List;

public class MessageUtil {

    public static Message frameMessage(User author, String content, boolean inline) {
        return frameMessage(author, content, null, null, inline);
    }

    public static Message frameMessage(User author, String content, String imageUrl, boolean inline) {
        return frameMessage(author, content, null, imageUrl, inline);
    }

    public static Message frameMessage(User author, String content, List<Pair<String, String>> subContent, boolean inline) {
        return frameMessage(author, content, subContent, null, inline);
    }

    public static Message frameMessage(User author, String content, List<Pair<String, String>> subContent, String imageUrl, boolean inline) {
        User bot = DiscordNano.bot.getSelfUser();
        String avatar = bot.getAvatarUrl();
        if (avatar == null) {
            avatar = bot.getDefaultAvatarUrl();
        }
        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor(bot.getName(), null, avatar);
        eb.setDescription(content);
        eb.setColor(Color.ORANGE);
        eb.setUrl("https://www.gimu.org/discord-nano");
        /*String thumb = bot.getAvatarUrl();
        if (thumb != null)
            eb.setThumbnail(thumb);*/

        if (subContent != null) {
            for (Pair<String, String> pair : subContent) {
                eb.addField(pair.getKey(), pair.getValue(), inline);
            }
        }

        if (imageUrl != null) {
            if (!imageUrl.contains("https")) imageUrl = imageUrl.replaceAll("http", "https");
            eb.setImage(imageUrl);
        }

        // Requester information
        String authorAvatar = author.getAvatarUrl();
        if (authorAvatar == null) {
            authorAvatar = author.getDefaultAvatarUrl();
        }
        eb.setFooter("Requested by " + author.getName() + "#" + author.getDiscriminator(), authorAvatar);

        MessageBuilder mb = new MessageBuilder();
        mb.setEmbed(eb.build());
        return mb.build();
    }
}
