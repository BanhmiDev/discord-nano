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

import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.entities.VoiceChannel;
import net.dv8tion.jda.managers.AudioManager;
import net.dv8tion.jda.player.MusicPlayer;
import net.dv8tion.jda.player.source.AudioInfo;
import net.dv8tion.jda.player.source.AudioSource;
import net.dv8tion.jda.player.source.RemoteSource;
import org.gimu.discordnano.DiscordNano;

import java.util.HashMap;
import java.util.Map;

public class MusicStreamer extends MusicPlayer {

    public static Map<AudioSource, MusicInfo> musicQueue = new HashMap<>();
    private boolean idle = false;
    private NanoMessage message;
    private AudioManager am;
    private VoiceChannel vc;
    private User author;

    public MusicStreamer(NanoMessage message) {
        super();
        this.am = message.getGuild().getAudioManager();
        this.vc = message.getGuild().getJDA().getVoiceChannelById(DiscordNano.VOICECHANNEL_ID); // TODO
        this.message = message;
        this.author = message.getAuthor();
        setVolume(DiscordNano.DEFAULT_VOLUME);
    }

    public void setIdle(boolean value) {
        idle = value;
    }

    public boolean getIdle() {
        return idle;
    }

    public User getAuthor() {
        return author;
    }

    @Override
    public void stop() {
        if (am != null) {
            musicQueue.remove(super.getPreviousAudioSource());
            DiscordNano.JDA.getAccountManager().setGame(DiscordNano.DEFAULT_STATUS);
            super.stop();
            am.closeAudioConnection();
        }
    }

    @Override
    public void playNext(boolean b) {
        super.playNext(b);
        MusicInfo.skips.clear();
        musicQueue.remove(super.getPreviousAudioSource());
        AudioSource src = super.getCurrentAudioSource();
        if (src == null) {
            if (DiscordNano.RANDOM_MUSIC && vc.getUsers().size() > 1) { // Random music whenever someone is listening
                setIdle(true);
                src = new RemoteSource(DiscordNano.musicLibrary.get(String.valueOf((int) (Math.random() * DiscordNano.musicLibrary.size()))));
                AudioInfo srcInfo = src.getInfo();
                if (srcInfo.getError() == null) {

                    this.getAudioQueue().add(src);
                    musicQueue.put(src, new MusicInfo(null));

                    updateStatus();

                    if (!this.isPlaying())
                        this.play();
                } else {
                    String err = srcInfo.getError();
                    System.err.println(err);
                }
            } else {
                setIdle(false);
                DiscordNano.JDA.getAccountManager().setGame(DiscordNano.DEFAULT_STATUS);
                am.closeAudioConnection();
            }
        } else {
            message.reply("**Now playing**: `" + src.getInfo().getTitle() + "` \\(ﾉ´ヮ´)ﾉ*:･ﾟ");
            DiscordNano.JDA.getAccountManager().setGame(src.getInfo().getTitle());
        }
    }

    @Override
    public void play() {
        super.play();
        super.setVolume(DiscordNano.DEFAULT_VOLUME);

        updateStatus();

        if (am.isConnected()) {
            am.moveAudioConnection(vc);
        } else {
            am.openAudioConnection(vc);
        }

    }

    public void add(AudioSource audioSource, MusicInfo musicInfo) {
        this.getAudioQueue().add(audioSource);
        musicQueue.put(audioSource, musicInfo);
    }

    private void updateStatus() {
        AudioSource audioSource = super.getCurrentAudioSource();
        AudioInfo audioInfo = audioSource.getInfo();
        message.reply("**Now playing**: `" + audioInfo.getTitle() + "`");
        DiscordNano.JDA.getAccountManager().setGame(audioInfo.getTitle());
    }
}
