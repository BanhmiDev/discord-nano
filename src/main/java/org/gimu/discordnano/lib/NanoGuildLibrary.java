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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;

public class NanoGuildLibrary {

    private LinkedHashMap<String, NanoGuild> libraryMap = new LinkedHashMap<String, NanoGuild>();

    public void add(String guildID, String textchannel) {
        add(guildID, new NanoGuild(textchannel, ""));
    }

    public void add(String guildID) {
        add(guildID, new NanoGuild());
    }

    public void add(String guildID, NanoGuild nanoGuild) {
        if (libraryMap.get(guildID) == null) {
            libraryMap.put(guildID, nanoGuild);

            Connection conn = NanoDatabase.getConnection();
            try {
                Statement st = conn.createStatement();
                if (!st.executeQuery("SELECT id FROM NanoGuilds WHERE guild_id = " + guildID).next()) {
                    // Add to database if not already in it
                    PreparedStatement ps = conn.prepareStatement("INSERT INTO NanoGuilds (guild_id, textchannel, voicechannel) VALUES(?, ?, ?)");
                    ps.setString(1, guildID);
                    ps.setString(2, nanoGuild.getTextchannel());
                    ps.setString(3, nanoGuild.getVoicechannel());
                    ps.executeUpdate();
                    ps.close();
                }
                conn.close();
            } catch (SQLException ex) {
                // ..
            }
        }
    }

    public void remove(String guildID) {
        libraryMap.remove(guildID);
    }

    public NanoGuild get(String guildID) {
        return libraryMap.get(guildID);
    }

    public void setTextchannel(String guildID, String textchannel) {
        if (libraryMap.get(guildID) != null) {
            libraryMap.get(guildID).setTextchannel(textchannel);

            Connection conn = NanoDatabase.getConnection();
            try {
                PreparedStatement ps = conn.prepareStatement("UPDATE NanoGuilds SET textchannel = ? WHERE guild_id = ?");
                ps.setString(1, textchannel);
                ps.setString(2, guildID);
                ps.executeUpdate();
                ps.close();
                conn.close();
            } catch (SQLException ex) {
                // ..
            }
        }
    }

    public void setVoicechannel(String guildID, String voicechannel) {
        if (libraryMap.get(guildID) != null) {
            libraryMap.get(guildID).setVoicechannel(voicechannel);

            Connection conn = NanoDatabase.getConnection();
            try {
                PreparedStatement ps = conn.prepareStatement("UPDATE NanoGuilds SET voicechannel = ? WHERE guild_id = ?");
                ps.setString(1, voicechannel);
                ps.setString(2, guildID);
                ps.executeUpdate();
                ps.close();
                conn.close();
            } catch (SQLException ex) {
                // ...
            }
        }
    }

    public LinkedHashMap<String, NanoGuild> getLibraryMap() {
        return libraryMap;
    }
}
