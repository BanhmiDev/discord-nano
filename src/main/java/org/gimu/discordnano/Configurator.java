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
package org.gimu.discordnano;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

class Configurator {

    private static final File config = new File("config.json");

    public static JSONObject getConfig() {
        if (!config.exists()) {
            try {
                Configurator.create();

            } catch (IOException ex) {
                ex.printStackTrace();
            }

            System.out.println("Created a configuration file. Please fill the login credentials!");
            System.exit(0);
        }

        JSONObject object = null;
        try {
            object = Configurator.load();

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return object;
    }

    private static void create() throws IOException {
        Files.write(Paths.get(config.getPath()),
                new JSONObject()
                        .put("appId", "")
                        .put("token", "")
                        .put("prefix", "-")
                        .toString(4).getBytes());
    }

    private static JSONObject load() throws IOException {
        JSONObject object = new JSONObject(new String(Files.readAllBytes(Paths.get(config.getPath())), "UTF-8"));

        if (object.has("appId") && object.has("token") && object.has("prefix"))
            return object;

        Configurator.create();
        System.err.println("The config file was missing a value! Regenerating...");
        System.exit(1);

        return null;
    }
}