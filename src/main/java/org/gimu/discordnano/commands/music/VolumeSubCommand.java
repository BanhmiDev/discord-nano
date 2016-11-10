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

import org.apache.commons.lang3.math.NumberUtils;
import org.gimu.discordnano.commands.AbstractSubCommand;
import org.gimu.discordnano.commands.SubCommand;
import org.gimu.discordnano.lib.MusicStreamer;
import org.gimu.discordnano.lib.NanoMessage;
import org.gimu.discordnano.util.MusicUtil;

import java.util.Optional;

@SubCommand(
        mainCommandAlias = "music",
        alias = {"volume"},
        description = "",
        usage = ""
)
public class VolumeSubCommand extends AbstractSubCommand {

    public Optional execute(NanoMessage message, String[] args) throws IllegalArgumentException {
        MusicStreamer streamer = MusicCommand.musicStreamer;
        String response = "";
        if (args.length == 0) {
            response = "Current volume: " + streamer.getVolume();
        } else if (!MusicUtil.isDJ(streamer, message.getAuthor())) {
            response = MusicCommand.NO_DJ_REPLY;
        } else {
            if (!NumberUtils.isNumber(args[0])) {
                response =  "Please enter a valid value!";
            }

            float newVolume = Float.parseFloat(args[0]);
            if (newVolume <= 1f) {
                streamer.setVolume(newVolume);
                response =  "Volume set to " + newVolume;
            } else {
                response = "That's too loud（ノT＿T)ノ ＾┻━┻";
            }
        }

        return Optional.of(response);
    }
}
