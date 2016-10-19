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
package org.gimu.discordnano.commands.media;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.gimu.discordnano.commands.AbstractCommand;
import org.gimu.discordnano.util.NanoMessage;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class EHentaiCommand extends AbstractCommand {

    private String[] triggers = {"ehentai"};
    private String description = "Display e-hentai entry";
    private String usage = "";

    @Override
    public void respond(NanoMessage message, String[] args) {
        // GENERATE RANDOM PAGE
        String galleryID = "";
        String galleryToken = "";

        message.getChannel().sendTyping();
        try {
            int random = (int)(Math.random() * ((18250) + 1));
            Document document = Jsoup.connect("http://g.e-hentai.org/?page=" + random).ignoreContentType(true).get();

            while (document.select(".it5 a").size() == 0) {
                random = (int)(Math.random() * ((18250) + 1));
                document = Jsoup.connect("http://g.e-hentai.org/?page=" + random).ignoreContentType(true).get();
            }

            int randomInner = (int)(Math.random() * (document.select(".it5 a").size()));
            Element randomElement = document.select(".it5 a").get(randomInner);
            String url = randomElement.attr("href");
            String[] split = url.split("/g/"); // URL split
            String[] split2 = split[1].split("/"); // ID split
            galleryID = split2[0];
            galleryToken = split2[1];

            String jsonString = "{\"method\": \"gdata\", \"gidlist\": [[" + galleryID + ", \"" + galleryToken + "\"]], \"namespace\": 1}".trim();
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost postRequest = new HttpPost("http://g.e-hentai.org/api.php");
            postRequest.setHeader("Content-type", "application/json");
            StringEntity entity = new StringEntity(jsonString);

            postRequest.setEntity(entity);

            HttpResponse response = httpClient.execute(postRequest);

            InputStream is = response.getEntity().getContent();
            Reader reader = new InputStreamReader(is);
            BufferedReader bufferedReader = new BufferedReader(reader);
            StringBuilder builder = new StringBuilder();
            while (true) {
                try {
                    String line = bufferedReader.readLine();
                    if (line != null) {
                        builder.append(line);
                    } else {
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            JSONObject jsonObj = new JSONObject(builder.toString());
            JSONArray array = jsonObj.getJSONArray("gmetadata");
            JSONObject first = array.getJSONObject(0);

            StringBuilder sb = new StringBuilder();
            sb.append("**English**: " + first.getString("title"));
            if (first.getString("title_jpn").length() > 0) sb.append("\n**Japanese**: " + first.getString("title_jpn"));
            sb.append("\n**Category**: " + first.getString("category") + " | **Files**: " + first.getString("filecount") + " | **Rating**: " + first.getString("rating"));

            JSONArray tags = first.getJSONArray("tags");
            if (tags.length() >= 2) {
                sb.append("\nTags: [");
                for (int i = 0; i < tags.length() - 1; i++) {
                    sb.append(tags.get(i) + ", ");
                }
                sb.append(tags.get(tags.length() - 1) + "]");
            }
            sb.append("\n\n**<http://g.e-hentai.org/g/" + first.getInt("gid") + "/" + first.getString("token") + ">**");
            sb.append("\n\n" + first.getString("thumb"));
            message.reply(sb.toString());
            //System.out.println("****************");
        } catch (Exception ex) {
            ex.printStackTrace();
            message.reply("I fucked up! Try again.");
        }
    }
}
