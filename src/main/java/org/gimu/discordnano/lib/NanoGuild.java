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

public class NanoGuild {

    private String voicechannel; // Main voice channel to join/leave
    private String textchannel; // Main channel to listen for

    public NanoGuild(String textchannel, String voicechannel) {
        this.textchannel = textchannel;
        this.voicechannel = voicechannel;
    }

    public String getVoicechannel() {
        return voicechannel;
    }

    public void setVoicechannel(String voicechannel) {
        this.voicechannel = voicechannel;
    }

    public String getTextchannel() {
        return textchannel;
    }

    public void setTextchannel(String textchannel) {
        this.textchannel = textchannel;
    }
}
