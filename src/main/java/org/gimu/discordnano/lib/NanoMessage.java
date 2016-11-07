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

import net.dv8tion.jda.JDA;
import net.dv8tion.jda.entities.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.function.Consumer;

public class NanoMessage implements Message
{
    private final Message message;
    private final Guild guild;

    public NanoMessage(Message message, Guild guild) {
        this.message = message;
        this.guild = guild;
    }

    public Message reply(String str) {
        return message.getChannel().sendMessage(str);
    }

    public Message replyFramed(String title, String str) {
        return message.getChannel().sendMessage("```Haskell\n" + title + "```\n```Haskell\n" + str + "```");
    }

    public Message replyFramed(String str) {
        return message.getChannel().sendMessage("```Haskell\n" + str + "```");
    }

    @Override
    public String getId() {
        return message.getId();
    }

    @Override
    public List<User> getMentionedUsers() {
        return message.getMentionedUsers();
    }

    @Override
    public boolean isMentioned(User user) {
        return message.isMentioned(user);
    }

    @Override
    public List<TextChannel> getMentionedChannels() {
        return message.getMentionedChannels();
    }

    @Override
    public List<Role> getMentionedRoles() {
        return message.getMentionedRoles();
    }

    @Override
    public boolean mentionsEveryone() {
        return message.mentionsEveryone();
    }

    @Override
    public OffsetDateTime getTime() {
        return message.getTime();
    }

    @Override
    public boolean isEdited() {
        return message.isEdited();
    }

    @Override
    public OffsetDateTime getEditedTimestamp() {
        return message.getEditedTimestamp();
    }

    @Override
    public User getAuthor() {
        return message.getAuthor();
    }

    @Override
    public String getContent() {
        return message.getContent();
    }

    @Override
    public String getRawContent() {
        return message.getRawContent();
    }

    @Override
    public String getStrippedContent() {
        return message.getStrippedContent();
    }

    @Override
    public boolean isPrivate() {
        return message.isPrivate();
    }

    @Override
    public String getChannelId() {
        return message.getChannelId();
    }

    @Override
    public MessageChannel getChannel() {
        return message.getChannel();
    }

    @Override
    public List<Attachment> getAttachments() {
        return message.getAttachments();
    }

    @Override
    public List<MessageEmbed> getEmbeds() {
        return message.getEmbeds();
    }

    @Override
    public boolean isTTS() {
        return message.isTTS();
    }

    @Override
    public Message updateMessage(String s) {
        return message.updateMessage(s);
    }

    @Override
    public void updateMessageAsync(String s, Consumer<Message> consumer) {
        message.updateMessageAsync(s, consumer);
    }

    @Override
    public void deleteMessage() {
        message.deleteMessage();
    }

    @Override
    public JDA getJDA() {
        return message.getJDA();
    }

    @Override
    public boolean isPinned() {
        return message.isPinned();
    }

    @Override
    public boolean pin() {
        return message.pin();
    }

    @Override
    public boolean unpin() {
        return message.unpin();
    }


    public Guild getGuild() {
        return guild;
    }
}
