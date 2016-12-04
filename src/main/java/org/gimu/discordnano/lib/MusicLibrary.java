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

import org.apache.commons.lang.math.NumberUtils;

import java.sql.*;
import java.util.*;

public class MusicLibrary {

    private LinkedHashMap<String, String> libraryMap = new LinkedHashMap<String, String>();

    public boolean add(String musicSource) {
        // Don't allow duplicate URLs
        /*boolean isDuplicate = false;
        for (Map.Entry<String, String> entry : libraryMap.entrySet()) {
            if (entry.getValue().equals(musicSource)) {
                isDuplicate = true;
            }
        }
        if (isDuplicate) return false;

        // Prepare to save to map and database
        AudioSource audioSource = new RemoteSource(musicSource, "");
        String musicTitle = audioSource.getInfo().getTitle();

        // Save to map
        libraryMap.put(musicTitle, musicSource);

        // Save to database
        Connection conn = NanoDatabase.getConnection();
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT id FROM MusicLibrary WHERE title = ?");
            ps.setString(1, musicTitle);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                ps = conn.prepareStatement("INSERT INTO MusicLibrary (title, source) VALUES(?, ?)");
                ps.setString(1, musicTitle);
                ps.setString(2, musicSource);
                ps.executeUpdate();
            }
            ps.close();
            conn.close();
        } catch (SQLException e) {
            NanoLogger.error(e.getMessage());
        }*/

        return true;
    }

    public String get(String query) {
        if (NumberUtils.isNumber(query)) {
            // Search based on index
            int iterator = 0;
            for (Map.Entry<String, String> entry : libraryMap.entrySet()) {
                if (iterator == Integer.parseInt(query)) {
                    return entry.getValue();
                }
                iterator++;
            }
        } else {
            // Search based on string
            for (String key : libraryMap.keySet()) {
                if (key.matches(".*(?i)"+query+".*")) {
                    return libraryMap.get(key);
                }
            }
        }
        return "-1";
    }

    public LinkedHashMap<String, String> getLibraryMap() {
        return libraryMap;
    }
}
