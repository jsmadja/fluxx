/**
 * Copyright (C) 2010 Julien SMADJA <julien.smadja@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.kaddath.apps.fluxx.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.ejb.Stateless;

import fr.kaddath.apps.fluxx.domain.Feed;
import fr.kaddath.apps.fluxx.domain.Opml;

@Stateless
public class OpmlService {

	public List<Feed> loadOpml(String opmlFile) throws FileNotFoundException {
		File file = new File(opmlFile);
		Scanner sc = new Scanner(file);
		return loadOpml(sc);
	}

	public List<Feed> loadOpml(byte[] bytes) {
		Scanner sc = new Scanner(new String(bytes));
		return loadOpml(sc);
	}

	private List<Feed> loadOpml(Scanner sc) {
		List<Feed> feeds = new ArrayList<Feed>();
		List<String> listXmlUrl = new ArrayList<String>();
		List<String> listText = new ArrayList<String>();
		while (sc.hasNextLine()) {
			String line = sc.nextLine();

			if (line.contains("xmlUrl")) {
				String url = line.split("xmlUrl=\"")[1].split("\"")[0];
				url = url.replaceAll("&amp;", "&");

				listXmlUrl.add(url);
			}

			if (line.contains("text")) {
				listText.add(line.split("text=\"")[1].split("\"")[0]);
			}
		}

		for (int i = 0; i < listXmlUrl.size(); i++) {
			Feed feed = new Feed();
			feed.setTitle(listText.get(i));
			feed.setUrl(listXmlUrl.get(i));
			feed.setInError(false);
			feed.setComplete(false);
			feeds.add(feed);
		}
		return feeds;
	}

	public String createOpml(List<Feed> feeds) {

		return new Opml(feeds).build();
	}
}
