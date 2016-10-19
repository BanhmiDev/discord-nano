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
package org.gimu.discordnano.util;

import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.entities.VoiceChannel;

import java.util.ArrayList;

public class SongInfo {

    public static final ArrayList<String> skips = new ArrayList<>();
    private final User author;
    private final Guild guild;

    public SongInfo(User author, Guild guild) {
        this.author = author;
        this.guild = guild;
    }

    public User getAuthor() {
        return author;
    }

    public VoiceChannel getVoiceChannel() {
        return guild.getVoiceStatusOfUser(author).getChannel();
    }

    public String getGuildId() {
        return guild.getId();
    }

    public int getVotes() {
        return skips.size();
    }

    public void voteSkip(User u) {
        skips.add(u.getId());
    }

    public boolean hasVoted(User u) {
        return skips.stream().anyMatch(uId -> u.getId().equals(uId));
    }
}