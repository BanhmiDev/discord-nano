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

import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.player.source.AudioInfo;
import net.dv8tion.jda.player.source.AudioSource;
import net.dv8tion.jda.player.source.AudioTimestamp;
import org.gimu.discordnano.DiscordNano;
import org.gimu.discordnano.lib.MusicStreamer;

public class MusicUtil {

    public static boolean isDJ(MusicStreamer player, User user) {
        return user.getId().equals(DiscordNano.AUTHOR_ID) || (user == player.getAuthor());
    }

    public static String buildQueue(AudioSource src) {
        AudioInfo info = src.getInfo();
        if (info != null) {
            AudioTimestamp dur = info.getDuration();
            return "`[" + (dur == null ? "N/A" : dur.getTimestamp()) + "]` " + info.getTitle() + "\n";
        }
        return null;
    }
}
