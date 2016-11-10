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
package org.gimu.discordnano.commands.music;

import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.player.source.AudioInfo;
import net.dv8tion.jda.player.source.AudioSource;
import net.dv8tion.jda.player.source.AudioTimestamp;
import org.gimu.discordnano.DiscordNano;
import org.gimu.discordnano.commands.AbstractSubCommand;
import org.gimu.discordnano.commands.SubCommand;
import org.gimu.discordnano.lib.MusicStreamer;
import org.gimu.discordnano.util.HastebinUtil;
import org.gimu.discordnano.util.MusicUtil;
import org.gimu.discordnano.lib.NanoMessage;

import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

@SubCommand(
        mainCommandAlias = "music",
        alias = {"now", "queue"},
        description = "",
        usage = ""
)
public class NowSubCommand extends AbstractSubCommand {

    public Optional execute(NanoMessage message, String[] args) throws IllegalArgumentException {
        MusicStreamer player = MusicCommand.musicStreamer;
        StringJoiner response = new StringJoiner("\n");
        if (!player.isPlaying()) {
            response.add("I'm not playing anything.");
        } else {
            // Current song information
            AudioTimestamp currentTime = player.getCurrentTimestamp();
            AudioSource currentSource = player.getCurrentAudioSource();
            AudioInfo info = currentSource.getInfo();
            User currentDJ = player.getAuthor();

            // Current song information string
            response.add("**Song**: " + info.getTitle());
            response.add("**Source**: " + info.getOrigin());
            response.add((info.getError() != null ? "" : "**Time**: [" + currentTime.getTimestamp() + " / " + info.getDuration().getTimestamp() + "]"));
            if (!player.getIdle()) {
                response.add("**DJ**: " + currentDJ.getUsername().replace("~~", "\\~\\~").replace("_", "\\_").replace("*", "\\*").replace("`", "\\`") + "#" + currentDJ.getDiscriminator());
            }

            // Queue information
            List<AudioSource> queue = player.getAudioQueue();

            if (player.getIdle()) {
                response.add("\nCurrently in **IDLE** mode and playing songs from the library.");
            } else if (queue.isEmpty()) {
                if (DiscordNano.RANDOM_MUSIC) {
                    response.add("\nNo more songs in the queue. Seems like I have to play from the library soon!");
                } else {
                    response.add("\nNo more songs in the queue. Seems like I have to stop soon!");
                }
            } else {
                StringBuilder queueString = new StringBuilder("\n\n**Queue Status** (Entries: " + queue.size() + ")\n");
                queueString.append("**Shuffle**: " + player.isShuffle() + "\n\n");
                if (queue.size() <= 5) {
                    for (int i = 0; i < queue.size(); i++) {
                        queueString.append(MusicUtil.buildQueue(queue.get(i)));
                    }
                } else {
                    message.getChannel().sendTyping();
                    StringBuilder body = new StringBuilder();
                    queue.stream().map(MusicUtil::buildQueue).forEach(body::append);
                    queueString.append(HastebinUtil.post(body.deleteCharAt(body.length() - 1).toString()));
                }

                int totalSeconds = 0;
                for (AudioSource source : queue) {
                    totalSeconds += source.getInfo().getDuration().getTotalSeconds();
                }
                queueString.append("\nTotal Queue Duration: ").append(AudioTimestamp.fromSeconds(totalSeconds).getTimestamp()).append(" minutes.");

                response.add(queueString);
            }
        }

        return Optional.of(response.toString());
    }
}
