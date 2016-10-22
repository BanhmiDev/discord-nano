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

package org.gimu.discordnano.commands.single;

import org.gimu.discordnano.DiscordNano;
import org.gimu.discordnano.commands.NanoExecutor;
import org.gimu.discordnano.util.NanoMessage;

public class HelpCommand extends NanoExecutor {

    public String[] triggers = {"help"};
    public String description = "Display the help page";
    public String usage = "";

    public void respond(NanoMessage message, String[] args) {
        // TODO: automated processing
        /*message.getAuthor().getPrivateChannel().sendMessage("__General Commands__\n"
                + "```\n"
                + DiscordNano.prefix + "mal <anime|manga> <searchquery> - Search for specific anime or manga\n"
                + DiscordNano.prefix + "mal <anime|manga> view <index> - View an entry from the recent search\n"
                + DiscordNano.prefix + "mal <anime|manga> view - List recent query\n"
                + DiscordNano.prefix + "xkcd [number|latest] - Display xkcd comic\n"
                + DiscordNano.prefix + "choose <option1 | option2 ...> - Let Nano choose from a minimum of 2 options\n"
                + DiscordNano.prefix + "rps <rock|paper|scissors> - Play rock-paper-scissors with Nano\n"
                + DiscordNano.prefix + "roll [faces] - Roll the dice\n"
                + DiscordNano.prefix + "flip - Heads or tails\n"
                + DiscordNano.prefix + "leet <text> - Translate into 1337speak\n"
                + DiscordNano.prefix + "math <expression> - Evaluate baby-tier calculations\n"
                + DiscordNano.prefix + "game <query> - Game lookup\n"
                + "```\n"
                + "__Lewd Commands__\n"
                + "```\n"
                + DiscordNano.prefix + "ehentai - Display e-hentai entry (WIP)\n"
                + DiscordNano.prefix + "exhentai - Display ex-hentai entry (WIP)\n"
                + DiscordNano.prefix + "rule34 <tags> - Hot stuff from your tags (whitespace separated)\n"
                + DiscordNano.prefix + "poop <text> - Translate into poop\n"
                + "```\n"
                + "__Music Commands__\n"
                + "```\n"
                + DiscordNano.prefix + "music play <url>|<index>|<searchquery> - Play song directly from URL or from the library \n"
                + DiscordNano.prefix + "music playlist <playlist url> - Add playlist to the queue\n\n"
                + "Library commands:\n"
                + DiscordNano.prefix + "music add <url> - Add music to library \n"
                + DiscordNano.prefix + "music list - List music library \n\n"
                + "General music commands:\n"
                + DiscordNano.prefix + "music join - Tell Nano to join the voice channel\n"
                + DiscordNano.prefix + "music leave - Tell Nano to leave the voice channel\n"
                + DiscordNano.prefix + "music skip - Vote skip current song\n"
                + DiscordNano.prefix + "music <now|queue|status|current> - Display current queue information\n"
                + DiscordNano.prefix + "music dj - Display current DJ\n\n"
                + "Commands available to the DJ:\n"
                + DiscordNano.prefix + "music volume [0.0 - 1.0] - Set or get volume\n"
                + DiscordNano.prefix + "music shuffle - Enable/disable shuffling\n"
                + DiscordNano.prefix + "music pause - Pause the playback\n"
                + DiscordNano.prefix + "music stop - Stop the playback\n"
                + DiscordNano.prefix + "music clear/reset - Clear queue\n"
                + "```\n"
                + "__Bot Specific Commands__\n"
                + "```\n"
                + DiscordNano.prefix + "about - Let me introduce myself\n"
                + DiscordNano.prefix + "invite - Get the authentication link to let Nano join another server\n"
                + DiscordNano.prefix + "uptime - Display uptime\n\n```"
        );*/
        message.reply("Working on the help page (ﾉ´ヮ´)ﾉ*:･ﾟ✧");
    }
}
