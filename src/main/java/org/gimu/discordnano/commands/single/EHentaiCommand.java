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

import net.dv8tion.jda.core.entities.Message;

import net.dv8tion.jda.core.entities.User;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import org.gimu.discordnano.commands.AbstractCommand;
import org.gimu.discordnano.commands.MainCommand;
import org.gimu.discordnano.lib.EmbedFieldListBuilder;
import org.gimu.discordnano.lib.MessageUtil;
import org.gimu.discordnano.lib.NanoLogger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Optional;

@MainCommand(
        alias = "ehentai",
        description = "Display random e-hentai entry",
        usage = "ehentai"
)
public class EHentaiCommand extends AbstractCommand {

    public EHentaiCommand(String description, String usage, String alias) {
        super(description, usage, alias);
    }

    public Optional execute(User author, Message message, String[] args) throws IllegalArgumentException {
        // GENERATE RANDOM PAGE
        String content = "";
        String galleryID = "";
        String galleryToken = "";
        EmbedFieldListBuilder builder = new EmbedFieldListBuilder();

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
            StringBuilder sb = new StringBuilder();
            while (true) {
                try {
                    String line = bufferedReader.readLine();
                    if (line != null) {
                        sb.append(line);
                    } else {
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            JSONObject jsonObj = new JSONObject(sb.toString());
            JSONArray array = jsonObj.getJSONArray("gmetadata");
            JSONObject first = array.getJSONObject(0);

            content = first.getString("title");
            if (first.getString("title_jpn").length() > 0) content += "\n" + first.getString("title_jpn");
            builder.append("Category", first.getString("category"));
            builder.append("Files", first.getString("filecount"));
            builder.append("Rating", first.getString("rating"));

            JSONArray tags = first.getJSONArray("tags");
            sb = new StringBuilder();
            if (tags.length() >= 2) {
                for (int i = 0; i < tags.length() - 1; i++) {
                    sb.append(cleanTag(tags.get(i).toString()) + ", ");
                }
                sb.append(cleanTag(tags.get(tags.length() - 1).toString()) + "");
                builder.append("Tags", sb.toString());
            }
            builder.append("Link", "http://g.e-hentai.org/g/" + first.getInt("gid") + "/" + first.getString("token") + "");

            message.getChannel().sendMessage(first.getString("thumb")).queue();
        } catch (Exception e) {
            NanoLogger.error(e.getMessage());
        }

        Message response = MessageUtil.frameMessage(author, content, builder.build(), false);
        return Optional.of(response);
    }

    private String cleanTag(String tag) {
        return tag.substring(tag.lastIndexOf(':') + 1);
    }
}
