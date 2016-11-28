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

            System.out.println("Created a configuration file, edit it as you need!");
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
                        .put("prefix", "!")
                        .put("token", "EDIT THIS")
                        .put("debug", false)
                        .put("default_volume", "0.25")
                        .put("random_music", true)
                        .put("db_user", "EDIT THIS")
                        .put("db_pass", "EDIT THIS")
                        .put("mal_user", "EDIT THIS")
                        .put("mal_pass", "EDIT THIS")
                        .put("x_mashape_key", "EDIT THIS")
                        .put("osu_api_key", "EDIT THIS")
                        .toString(11).getBytes());
    }

    private static JSONObject load() throws IOException {
        JSONObject object = new JSONObject(new String(Files.readAllBytes(Paths.get(config.getPath())), "UTF-8"));

        //if (object.has("token") && object.has("token") && object.has("prefix")) TODO
        return object;

        /*Configurator.create();
        System.err.println("The config file was missing a value! Regenerating...");
        return null;*/
    }
}