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

import org.apache.commons.lang3.StringUtils;
import org.gimu.discordnano.lib.NanoExecutor;
import org.gimu.discordnano.lib.NanoMessage;

import java.util.HashMap;
import java.util.Map;

public class LeetExecutor extends NanoExecutor {

    public String[] triggers = {"leet"};
    public String description = "Translates text into 1337speak";
    public String usage = "<text>";

    private static final Map<String, String> dictionary = new HashMap<String, String>() {{
        put("a", "@");
        put("e", "3");
        put("f", "ph");
        put("g", "9");
        put("i", "1");
        put("o", "0");
        put("s", "z");
        put("t", "7");
    }};

    @Override
    public void respond(NanoMessage message, String[] args) throws IllegalArgumentException {
        if (args.length == 0) {
            throw new IllegalArgumentException();
        }

        String response = StringUtils.join(args, " ");

        for (Map.Entry<String, String> entry : dictionary.entrySet()) {
            response = response.replaceAll(entry.getKey(), entry.getValue());
        }
        message.reply("`" + response + "`");
    }
}
