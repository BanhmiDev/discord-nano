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

import org.gimu.discordnano.DiscordNano;

public class NanoLogger {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";

    public static void debug(String message) {
        if (DiscordNano.DEBUG) {
            System.out.println(ANSI_GREEN + "[DEBUG] " + message + ANSI_RESET);
        }
    }

    public static void warning(String message) {
        if (DiscordNano.DEBUG) {
            System.out.println(ANSI_YELLOW + "[WARNING] " + message + ANSI_RESET);
        }
    }

    public static void error(String message) {
        if (DiscordNano.DEBUG) {
            System.out.println(ANSI_RED + "[ERROR] " + message + ANSI_RESET);
        }
    }
}
