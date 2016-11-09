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

import net.dv8tion.jda.JDA;
import net.dv8tion.jda.entities.*;
import net.dv8tion.jda.managers.AudioManager;
import net.dv8tion.jda.player.MusicPlayer;
import net.dv8tion.jda.player.source.AudioSource;
import net.dv8tion.jda.player.source.RemoteSource;
import org.gimu.discordnano.DiscordNano;
import org.gimu.discordnano.commands.MainCommand;
import org.gimu.discordnano.commands.CommandExecutor;
import org.gimu.discordnano.util.MusicUtil;
import org.gimu.discordnano.lib.NanoMessage;
import org.gimu.discordnano.util.SongInfo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//TODO: FINISH
public class MusicExecutor  {

    // TODO: each guild one streamer, up to one library for all?

    public String[] triggers = {"music"};
    public String description = "Plays music";
    public String usage = "";

    public MusicStreamer musicStreamer;

    public static MusicLibrary musicLibrary;
    public static final ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);

    protected static final String NO_DJ_REPLY = "You are not the DJ (ノдヽ)";

    public MusicExecutor() {
        musicLibrary = new MusicLibrary();

        // Register subcommands
    }

    private static boolean isIdle(MusicPlayer musicStreamer, NanoMessage message) {
        if (!musicStreamer.isPlaying()) {
            message.reply("I'm not playing music right now.");
            return true;
        }
        return false;
    }

    public void respond(NanoMessage message, String[] args) throws IllegalArgumentException {
        if (args.length == 0) {
            throw new IllegalArgumentException();
        }

        JDA jda = message.getJDA();
        User author = message.getAuthor();
        AudioManager am = message.getGuild().getAudioManager();
        VoiceChannel vc = jda.getVoiceChannelById(DiscordNano.VOICECHANNEL_ID);

        if (am.getSendingHandler() == null || musicStreamer == null) {
            musicStreamer = new MusicStreamer(message);
            am.setSendingHandler(musicStreamer);
        }

        String inputArgs = args.length >= 2 ? args[1] : "";
        AudioSource currentSource = musicStreamer.getCurrentAudioSource();

        // Commands
        switch (args[0].toLowerCase()) {
            case "join":
                am.openAudioConnection(vc);
                break;
            case "leave":
                musicStreamer.stop();
                break;
            case "play":
                PlayCommand.respond(musicStreamer, author, inputArgs);
                break;
            case "library":
            case "list":
                message.reply(ListSubCommand.respond(musicLibrary));
                break;
            case "add":
                if (inputArgs.length() == 0) return;

                musicLibrary.add(musicStreamer, author, new RemoteSource(inputArgs), true);
                threadPool.submit(() -> {
                    // Delete request message
                    message.deleteMessage();
                });
                break;
            case "dj":
                if (currentSource != null) {
                    User currentDJ = musicStreamer.musicQueue.get(currentSource).getAuthor();
                    message.reply("**Current DJ**: " + currentDJ.getAsMention());
                } else {
                    message.reply("Current DJ: (╯°□°）╯︵ ┻━┻");
                }
                break;
            case "volume":
                VolumeCommand.setVolume(musicStreamer, author, inputArgs);
                break;
            case "now":
            case "queue":
            case "status":
            case "current":
                NowCommand.respond(message, musicStreamer);
                break;
            case "skip":
                if (isIdle(musicStreamer, message))
                    return;

                SongInfo s = musicStreamer.musicQueue.get(musicStreamer.getCurrentAudioSource());
                if (MusicUtil.isDJ(musicStreamer, author)) {
                    message.reply("DJ skipped the song!");
                    musicStreamer.skipToNext();
                } else {
                    if (s.hasVoted(author)) {
                        message.reply("You have already voted to skip the song!");
                        return;
                    }

                    s.voteSkip(author);
                    int voteCount = s.getVotes();
                    int votesRequired = Math.round(message.getGuild().getAudioManager().getConnectedChannel().getUsers().size() / 2);
                    if (voteCount >= votesRequired) {
                        musicStreamer.skipToNext();
                        message.reply("Skipping to the next song.");
                    } else
                        message.reply(author.getUsername().replace("`", "\\`") + " has voted to skip the song! " + voteCount + "/" + votesRequired + "");
                }
                break;
            case "stop":
            case "clear":
            case "reset":
                if (!MusicUtil.isDJ(musicStreamer, author)) {
                    message.reply("I don't think so, " + author.getUsername().replace("`", "\\`"));
                    return;
                }

                musicStreamer.stop();
                message.reply("(ノಠ益ಠ)ノ彡┻━┻");
                break;
            case "shuffle":
                if (!MusicUtil.isDJ(musicStreamer, author)) {
                    message.reply(NO_DJ_REPLY);
                    return;
                }

                boolean shuffle = musicStreamer.isShuffle();
                if (inputArgs.equals("")) {
                    musicStreamer.setShuffle(!shuffle);
                    if (shuffle)
                        message.reply("Disabled shuffling.");
                    else
                        message.reply("Enabled shuffling.");
                }
                break;
            case "pause":
                if (isIdle(musicStreamer, message))
                    return;

                if (!MusicUtil.isDJ(musicStreamer, author)) {
                    message.reply(NO_DJ_REPLY);
                    return;
                }

                musicStreamer.setIdle(false);
                musicStreamer.pause();
                message.reply("I paused the music stream.");
                DiscordNano.jda.getAccountManager().setGame(DiscordNano.DEFAULT_STATUS);
                break;
            case "resume":
                if (!musicStreamer.isPlaying()) musicStreamer.play();
                break;
            case "playlist":
                PlaylistCommand.respond(musicStreamer, message, author, inputArgs);
                break;
        }
    }

}
