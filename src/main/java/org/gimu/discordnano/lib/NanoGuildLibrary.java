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

import net.dv8tion.jda.core.entities.Guild;

import java.sql.*;
import java.util.LinkedHashMap;

public class NanoGuildLibrary {

    private LinkedHashMap<String, NanoGuild> libraryMap = new LinkedHashMap<String, NanoGuild>();

    public void add(Guild guild) {
        NanoGuild nanoGuild = new NanoGuild();
        String guildID = guild.getId();

        // Add if not already present in map
        if (libraryMap.get(guildID) == null) {
            Connection conn = NanoDatabase.getConnection();
            try {
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery("SELECT id, textchannel, voicechannel FROM NanoGuilds WHERE guild_id = '" + guildID + "'");
                if (!rs.first()) {
                    // Add to database
                    // set first textchannel to be main one
                    PreparedStatement ps = conn.prepareStatement("INSERT INTO NanoGuilds (guild_id, textchannel, voicechannel) VALUES(?, ?, ?)");
                    ps.setString(1, guildID);
                    if (nanoGuild.getTextchannel().isEmpty()) {
                        // Bound to first text channel
                        nanoGuild.setTextchannel(guild.getTextChannels().get(0).getId());
                    }
                    ps.setString(2, nanoGuild.getTextchannel());
                    ps.setString(3, nanoGuild.getVoicechannel());
                    ps.executeUpdate();
                    ps.close();

                }

                // Already in database
                if (rs.first()) {
                    // Fetch data prior to adding to the map
                    nanoGuild.setTextchannel(rs.getString("textchannel"));
                    nanoGuild.setVoicechannel(rs.getString("voicechannel"));
                }

                // Finally add to the map
                libraryMap.put(guildID, nanoGuild);
                conn.close();
            } catch (SQLException e) {
                NanoLogger.error(e.getMessage());
            }
        }
    }

    public void remove(String guildID) {
        if (libraryMap.get(guildID) != null) {
            Connection conn = NanoDatabase.getConnection();
            try {
                // Delete from database
                conn.createStatement().executeQuery("DELETE FROM NanoGuilds WHERE guild_id = '" + guildID + "'");

                // Remove from map
                libraryMap.remove(guildID);
                conn.close();
            } catch (SQLException e) {
                NanoLogger.error(e.getMessage());
            }
        }
    }

    public NanoGuild get(String guildID) {
        return libraryMap.get(guildID);
    }

    public void setTextchannel(String guildID, String textchannel) {
        // Update in map and database
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
            } catch (SQLException e) {
                NanoLogger.error(e.getMessage());
            }
        }
    }

    public void setVoicechannel(String guildID, String voicechannel) {
        // Update in map and database
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
            } catch (SQLException e) {
                NanoLogger.error(e.getMessage());
            }
        }
    }

    public LinkedHashMap<String, NanoGuild> getLibraryMap() {
        return libraryMap;
    }
}
