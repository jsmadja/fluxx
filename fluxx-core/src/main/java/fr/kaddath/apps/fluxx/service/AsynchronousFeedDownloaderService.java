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

import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.Scanner;

import javax.ejb.Stateless;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;

import fr.kaddath.apps.fluxx.collection.Pair;
import fr.kaddath.apps.fluxx.domain.Feed;
import fr.kaddath.apps.fluxx.exception.DownloadFeedException;

@Stateless
public class AsynchronousFeedDownloaderService {

	private static final SyndFeedInput SYND_FEED_INPUT = new SyndFeedInput();

	public Pair<Feed, SyndFeed> downloadFeedContent(Feed feed) throws DownloadFeedException {
		try {
			String content = downloadContent(feed.getUrl());
			SyndFeed syndFeed = createSyndFeed(content);
			int size = content.length();
			feed.setSize(size);

			return new Pair<Feed, SyndFeed>(feed, syndFeed);
		} catch (Exception ex) {
			throw new DownloadFeedException(ex.getMessage());
		}
	}

	private SyndFeed createSyndFeed(String text) throws FeedException {
		return SYND_FEED_INPUT.build(new StringReader(text));
	}

	private String downloadContent(String url) throws IOException {
		Scanner sc = new Scanner(new URL(url).openConnection().getInputStream());
		StringBuilder builder = new StringBuilder();
		while (sc.hasNextLine()) {
			builder.append(sc.nextLine());
		}
		return builder.toString();
	}

}
