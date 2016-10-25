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
import net.dv8tion.jda.entities.VoiceChannel;
import net.dv8tion.jda.managers.AudioManager;
import net.dv8tion.jda.player.MusicPlayer;
import net.dv8tion.jda.player.source.AudioInfo;
import net.dv8tion.jda.player.source.AudioSource;
import net.dv8tion.jda.player.source.RemoteSource;
import org.gimu.discordnano.DiscordNano;
import org.gimu.discordnano.commands.music.MusicExecutor;
import org.gimu.discordnano.lib.NanoMessage;

public class CustomMusicPlayer extends MusicPlayer {

    private boolean idle = false;
    private AudioManager am;
    private VoiceChannel vc;
    private NanoMessage message;
    private User author;

    public CustomMusicPlayer(AudioManager am, VoiceChannel vc, NanoMessage message) {
        super();
        this.am = am;
        this.vc = vc;
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
        MusicExecutor.musicQueue.remove(super.getPreviousAudioSource());
        DiscordNano.jda.getAccountManager().setGame(DiscordNano.DEFAULT_STATUS);
        super.stop();
        am.closeAudioConnection();
    }

    @Override
    public void playNext(boolean b) {
        super.playNext(b);
        SongInfo.skips.clear();
        MusicExecutor.musicQueue.remove(super.getPreviousAudioSource());
        AudioSource src = super.getCurrentAudioSource();
        if (src == null) {
            if (DiscordNano.RANDOM_MUSIC && vc.getUsers().size() > 1) { // Random music whenever someone is listening
                setIdle(true);
                src = new RemoteSource(MusicExecutor.getSrcFromLibrary(String.valueOf((int) (Math.random() * MusicExecutor.musicLibraryMap.size()))));
                AudioInfo srcInfo = src.getInfo();
                if (srcInfo.getError() == null) {

                    this.getAudioQueue().add(src);
                    MusicExecutor.musicQueue.put(src, new SongInfo(null, null));

                    message.reply("**Now playing**: `" + srcInfo.getTitle() + "` \\(ﾉ´ヮ´)ﾉ*:･ﾟ");
                    DiscordNano.jda.getAccountManager().setGame(src.getInfo().getTitle());

                    if (!this.isPlaying())
                        this.play();
                } else {
                    String err = srcInfo.getError();
                    System.err.println(err);
                }
            } else {
                setIdle(false);
                DiscordNano.jda.getAccountManager().setGame(DiscordNano.DEFAULT_STATUS);
                am.closeAudioConnection();
            }
        } else {
            message.reply("**Now playing**: `" + src.getInfo().getTitle() + "` \\(ﾉ´ヮ´)ﾉ*:･ﾟ");
            DiscordNano.jda.getAccountManager().setGame(src.getInfo().getTitle());
        }
    }

    @Override
    public void play() {
        super.play();
        super.setVolume(DiscordNano.DEFAULT_VOLUME);

        if (am.isConnected()) {
            am.moveAudioConnection(vc);
        } else {
            am.openAudioConnection(vc);
        }

    }
}
