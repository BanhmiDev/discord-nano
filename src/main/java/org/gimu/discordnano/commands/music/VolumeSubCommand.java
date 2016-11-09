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
import org.gimu.discordnano.util.MusicUtil;

public class VolumeCommand {

    public static String setVolume(MusicStreamer player, User user, String input) {
        if (MusicUtil.isDJ(player, user)) {
            return MusicCommand.NO_DJ_REPLY;
        }

        if (input.length() == 0) {
            return "Current volume: " + player.getVolume();
        } else {
            if (!NumberUtils.isNumber(input)) {
                return "Please enter a valid value!";
            }

            float newVolume = Float.parseFloat(input);
            if (newVolume <= 1f) {
                player.setVolume(newVolume);
                return "VolumeCommand set to " + newVolume;
            } else {
                return "That's too loud（ノT＿T)ノ ＾┻━┻";
            }
        }
    }
}
