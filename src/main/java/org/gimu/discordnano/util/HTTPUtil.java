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

import org.apache.commons.codec.binary.Base64;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;

public class HTTPUtil {

	private static final String USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36";

	public static InputStream sendAuthGet(String target, String parameters, String username, String password) throws IOException {
		URL url = new URL(target + "?" + parameters);
		URLConnection uc = url.openConnection();
		uc.setRequestProperty("User-Agent", USER_AGENT);
		String preAuth = username + ":" + password;
		String basicAuth = "Basic " + new String(new Base64().encode(preAuth.getBytes()));
		uc.setRequestProperty("Authorization", basicAuth);

		return uc.getInputStream();
	}

	public static InputStream sendGet(String url) throws IOException {
		return sendGet(url, "");
	}

	public static InputStream sendGet(String url, String parameters) throws IOException {
		URL object = new URL(url + "?" + parameters);
		HttpURLConnection connection = (HttpURLConnection)object.openConnection();
		connection.setRequestMethod("GET");
		connection.setRequestProperty("User-Agent", USER_AGENT);
		return connection.getInputStream();
	}

	public static InputStream sendPost(String url) throws IOException {
		return sendPost(url, "");
	}

	public static InputStream sendPost(String url, String parameters) throws IOException {
		URL object = new URL(url);
		HttpURLConnection connection = (HttpURLConnection)object.openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("User-Agent", USER_AGENT);
		connection.setDoOutput(true); // We need to send a request body

		DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
		dataOutputStream.writeBytes(parameters);
		dataOutputStream.flush();
		dataOutputStream.close();

		return connection.getInputStream();
	}
}
