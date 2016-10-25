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
import net.dv8tion.jda.Permission;
import net.dv8tion.jda.entities.*;
import net.dv8tion.jda.managers.AudioManager;
import net.dv8tion.jda.player.MusicPlayer;
import net.dv8tion.jda.player.source.AudioInfo;
import net.dv8tion.jda.player.source.AudioSource;
import net.dv8tion.jda.player.source.RemoteSource;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.gimu.discordnano.DiscordNano;
import org.gimu.discordnano.lib.NanoExecutor;
import org.gimu.discordnano.listeners.CommandListener;
import org.gimu.discordnano.util.CustomMusicPlayer;
import org.gimu.discordnano.util.MusicUtil;
import org.gimu.discordnano.lib.NanoMessage;
import org.gimu.discordnano.util.SongInfo;
import org.json.JSONObject;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MusicExecutor extends NanoExecutor {

    public String[] triggers = {"music"};
    public String description = "Plays music";
    public String usage = "";

    protected static CustomMusicPlayer player;
    protected static final String NO_DJ_REPLY = "You are not the DJ (ノдヽ)";

    protected static final Set<String> playlistLoader = new HashSet<>();
    protected static final ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);

    protected static JSONObject musicLibrary;
    public static Map<AudioSource, SongInfo> musicQueue = new HashMap<>();
    public static LinkedHashMap<String, JSONObject> musicLibraryMap = new LinkedHashMap<>();

    public MusicExecutor() {
        initMusicLibrary();
    }

    public static void addSingleSource(AudioSource src, CustomMusicPlayer player, NanoMessage message) {
        TextChannel channel = (TextChannel) message.getChannel();
        Guild guild = channel.getGuild();
        User author = message.getAuthor();
        AudioInfo srcInfo = src.getInfo();
        if (srcInfo.getError() == null) {
            if (channel.checkPermission(channel.getJDA().getSelfInfo(), Permission.MESSAGE_MANAGE)) {
                // Delete request message
                message.deleteMessage();
            }

            player.getAudioQueue().add(src);
            musicQueue.put(src, new SongInfo(author, guild));

            if (player.getIdle()) {
                player.skipToNext();
                player.setIdle(false);
            } else if (!player.isPlaying()) {
                player.play();
                message.reply("**Now playing**: `" + srcInfo.getTitle() + "`");
                DiscordNano.jda.getAccountManager().setGame(srcInfo.getTitle());
            }
        } else {
            String err = srcInfo.getError();
            message.reply("I fucked up");
            System.err.println(err);
        }
    }

    private static boolean isIdle(MusicPlayer player, NanoMessage message) {
        if (!player.isPlaying()) {
            message.reply("I'm not playing music right now.");
            return true;
        }
        return false;
    }

    private static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public void initMusicLibrary() {
        // Init file
        try {
            URL oracle = new URL(DiscordNano.MUSIC_LIBRARY_URL);
            HttpURLConnection httpcon = (HttpURLConnection) oracle.openConnection();
            httpcon.addRequestProperty("User-Agent", "Mozilla/4.0");
            InputStream is = httpcon.getInputStream();
            String jsonTxt = convertStreamToString(is);
            musicLibrary = new JSONObject(jsonTxt);
        } catch (Exception e) {
            System.out.println("Fucked up initializing music library");
            System.out.println(e.getMessage());
        }

        // Create list
        Iterator<?> keys = musicLibrary.keys();
        while (keys.hasNext()) {
            String key = (String)keys.next();
            if (musicLibrary.get(key) instanceof JSONObject) {
                musicLibraryMap.put(key, (JSONObject) musicLibrary.get(key));
            }
        }
    }

    public static String getSrcFromLibrary(String query) {
        if (NumberUtils.isNumber(query)) {
            // Search based on index
            int iterator = 0;
            for (Map.Entry<String, JSONObject> entry : musicLibraryMap.entrySet()) {
                if (iterator == Integer.parseInt(query)) {
                    return entry.getValue().getString("url");
                }
                iterator++;
            }
        } else {
            // Search based on string
            String found = "";
            for (String key : musicLibraryMap.keySet()) {
                if (key.matches(".*(?i)"+query+".*")) {
                    found = key;
                    break;
                }
            }
            if (!found.equals("")) {
                return musicLibraryMap.get(found).getString("url");
            }
        }
        return "-1";
    }

    private void addToLibrary(AudioSource src) {
        AudioInfo info = src.getInfo();
        JSONObject json = new JSONObject();
        json.put("url", info.getOrigin());
        musicLibraryMap.put(info.getTitle(), json);
        saveToJSON();
    }

    private void saveToJSON() {
        JSONObject json = new JSONObject();
        for (Map.Entry<String, JSONObject> entry : musicLibraryMap.entrySet()) {
            json.put(entry.getKey(), entry.getValue());
        }
        try {
            HttpClient httpclient = HttpClients.createDefault();
            HttpPost httppost = new HttpPost(DiscordNano.MUSIC_LIBRARY_SAVE);

            // Request parameters and other properties.
            List<NameValuePair> params = new ArrayList<NameValuePair>(2);
            params.add(new BasicNameValuePair("json", json.toString()));
            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

            // Execute and get the response.
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            if (entity == null) System.out.println("Fucked up saving data.");
        } catch (Exception e) {
            System.out.println("Fucked up saving data.");
        }
    }

    public void respond(NanoMessage message, String[] args) throws IllegalArgumentException {
        if (args.length == 0) {
            throw new IllegalArgumentException();
        }

        JDA jda = message.getJDA();
        Guild guild = CommandListener.guild;
        User author = message.getAuthor();

        AudioManager am = guild.getAudioManager();
        VoiceChannel vc = jda.getVoiceChannelById(DiscordNano.VOICECHANNEL_ID);

        if (am.getSendingHandler() == null) {
            player = new CustomMusicPlayer(am, vc, message);
            am.setSendingHandler(player);
        } else {
            player = (CustomMusicPlayer) am.getSendingHandler();
        }

        String inputArgs = args.length >= 2 ? args[1] : "";
        AudioSource currentSource = null;
        User currentDJ = null;

        StringBuilder sb;
        AudioSource src;

        // Commands
        switch (args[0].toLowerCase()) {
            case "join":
                if (am != null) am.openAudioConnection(vc);
                break;
            case "leave":
                if (am != null && am.isConnected()) {
                    player.stop();
                    am.closeAudioConnection();
                }
                break;
            case "play":
                PlayCommand.respond(message, inputArgs, player);
                break;
            case "library":
            case "list":
                ListCommand.respond(message, musicLibraryMap);
                break;
            case "add":
                if (inputArgs.length() == 0) return;

                message.getChannel().sendTyping();
                src = new RemoteSource(inputArgs);
                if (src.getInfo().getError() != null) {
                    String err = src.getInfo().getError();
                    System.err.println(err);
                    message.reply("Invalid URL, you fucked up.");
                } else if (src.getInfo().isLive()) {
                    message.reply("I don't play livestreams.");
                } else {
                    // Add to library
                    threadPool.submit(() -> {
                        addToLibrary(src);
                        addSingleSource(src, player, message);
                    });
                }
                break;
            case "dj":
                currentSource = player.getCurrentAudioSource();
                if (currentSource != null) {
                    currentDJ = musicQueue.get(currentSource).getAuthor();
                    message.reply("**Current DJ**: " + currentDJ.getAsMention());
                } else {
                    message.reply("Current DJ: (╯°□°）╯︵ ┻━┻");
                }
                break;
            case "volume":
                VolumeCommand.setVolume(message, player, author, inputArgs);
                break;

            case "now":
            case "queue":
            case "status":
            case "current":
                NowCommand.respond(message, player);
                break;

            case "skip":
                if (isIdle(player, message))
                    return;

                SongInfo s = musicQueue.get(player.getCurrentAudioSource());
                if (MusicUtil.isDJ(player, author)) {
                    message.reply("DJ skipped the song!");
                    player.skipToNext();
                } else {
                    if (s.hasVoted(author)) {
                        message.reply("You have already voted to skip the song!");
                        return;
                    }

                    s.voteSkip(author);
                    int voteCount = s.getVotes();
                    int votesRequired = Math.round(guild.getAudioManager().getConnectedChannel().getUsers().size() / 2);
                    if (voteCount >= votesRequired) {
                        player.skipToNext();
                        message.reply("Skipping to the next song.");
                    } else
                        message.reply(author.getUsername().replace("`", "\\`") + " has voted to skip the song! " + voteCount + "/" + votesRequired + "");
                }
                break;

            case "clear":
            case "reset":
                if (!MusicUtil.isDJ(player, author)) {
                    message.reply("I don't think so, " + author.getUsername().replace("`", "\\`") + " (ノಠ益ಠ)ノ");
                    return;
                }

                player.stop();
                playlistLoader.remove(guild.getId());
                musicQueue.clear();
                message.reply("(ノಠ益ಠ)ノ彡┻━┻");
                break;

            case "shuffle":
                if (!MusicUtil.isDJ(player, author)) {
                    message.reply(NO_DJ_REPLY);
                    return;
                }

                boolean shuffle = player.isShuffle();
                if (inputArgs.equals("")) {
                    player.setShuffle(!shuffle);
                    if (shuffle)
                        message.reply("Disabled shuffling.");
                    else
                        message.reply("Enabled shuffling.");
                }
                break;

            case "pause":
                if (isIdle(player, message))
                    return;

                if (!MusicUtil.isDJ(player, author)) {
                    message.reply(NO_DJ_REPLY);
                    return;
                }

                player.setIdle(false);
                player.pause();
                message.reply("Paused the player.");
                DiscordNano.jda.getAccountManager().setGame(DiscordNano.DEFAULT_STATUS);
                break;

            case "stop":
                if (isIdle(player, message))
                    return;

                if (!MusicUtil.isDJ(player, author)) {
                    message.reply(NO_DJ_REPLY);
                    return;
                }

                player.stop();
                message.reply("Stopped the player.");
                break;

            case "playlist":
                PlaylistCommand.respond(message, author, inputArgs);
                break;

            default:
                System.out.println("WRONG INPUT");
                break;
        }
    }

}
