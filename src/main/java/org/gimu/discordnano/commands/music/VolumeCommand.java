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
import org.apache.commons.lang3.math.NumberUtils;
import org.gimu.discordnano.util.CustomMusicPlayer;
import org.gimu.discordnano.util.MusicUtil;
import org.gimu.discordnano.util.NanoMessage;

public class VolumeCommand {

    public static void setVolume(NanoMessage message, CustomMusicPlayer player, User user, String input) {
        if (MusicUtil.isDJ(player, user)) {
            message.reply(MusicExecutor.NO_DJ_REPLY);
            return;
        }

        if (input.length() == 0) {
            message.reply("Current volume: " + player.getVolume());
        } else {
            if (!NumberUtils.isNumber(input)) {
                message.reply("Please enter a valid value!");
                return;
            }

            float newVolume = Float.parseFloat(input);
            if (newVolume <= 1f) {
                player.setVolume(newVolume);
                message.reply("VolumeCommand set to " + newVolume);
            } else {
                message.reply("That's too loud（ノT＿T)ノ ＾┻━┻");
            }
        }
    }
}
