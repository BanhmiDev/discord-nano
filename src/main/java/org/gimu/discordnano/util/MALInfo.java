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
package org.gimu.discordnano.util;

public class MALInfo {

    public String id, title, english, episodes, score, type, status, synopsis;

    public MALInfo(String id, String title, String english, String episodes, String score, String type, String status, String synopsis) {
        this.id = id;
        this.title = title;
        this.english = english;
        this.episodes = episodes;
        this.score = score;
        this.type = type;
        this.status = status;
        this.synopsis = synopsis;
    }
}
