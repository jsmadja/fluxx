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

package fr.fluxx.core.crawler;

import java.net.URL;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.SimpleNodeIterator;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.url.WebURL;
import fr.fluxx.core.Services;
import fr.fluxx.core.exception.DownloadFeedException;
import fr.fluxx.core.service.CrawlerService;
import fr.fluxx.core.service.FeedFetcherService;

public class FeedCrawler extends WebCrawler {

	private static final int DEFAULT_MAX_FEEDS_TO_ADD = 100;

	private static final Logger LOG = Logger.getLogger(CrawlerService.class.getName());

	private static int maxFeedsToAdd = DEFAULT_MAX_FEEDS_TO_ADD;

	private static int numFeedsAdded = 0;

	private static final Pattern EXTENSION_FILTERS = Pattern
			.compile(".*(\\.(css|js|bmp|gif|jpe?g|png|tiff?|mid|mp2|mp3|mp4|wav|avi|mov|mpeg|ico|ram|m4v|pdf|rm|smil|wmv|swf|wma|zip|rar|gz))$");

	private final FeedFetcherService feedFetcherService;

	public FeedCrawler() {
		feedFetcherService = Services.getFeedFetcherService();
	}

	public FeedCrawler(FeedFetcherService feedFetcherService) {
		this.feedFetcherService = feedFetcherService;
	}

	@Override
	public boolean shouldVisit(WebURL url) {
		if (!isCrawable()) {
			return false;
		}
		String href = url.getURL().toLowerCase();
		return (!EXTENSION_FILTERS.matcher(href).matches());
	}

	private boolean isCrawable() {
		boolean crawl = numFeedsAdded < maxFeedsToAdd;
		if (!crawl) {
			getMyController().stop();
		}
		return crawl;
	}

	@Override
	public void visit(Page page) {
		if (!isCrawable()) {
			return;
		}
		try {
			addAllFeedsFoundInPage(page);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addAllFeedsFoundInPage(Page page) {
		try {
			Parser parser = new Parser(new URL(page.getWebURL().getURL()).openConnection());
			NodeFilter nodeFilter = new HasAttributeFilter("type", "application/rss+xml");
			NodeList list = parser.parse(nodeFilter);
			SimpleNodeIterator nodes = list.elements();
			while (isCrawable() && nodes.hasMoreNodes()) {
				try {
					addFeed(nodes.nextNode());
				} catch (DownloadFeedException e) {
					LOG.info(e.getMessage());
				}
			}
		} catch (Exception e) {
			String msg = e.getMessage();
			if (isLoggable(msg)) {
				LOG.info(e.getMessage());
				e.printStackTrace();
			}
		}
	}

	private boolean isLoggable(String msg) {
		boolean notLoggable = isHTTPError(msg);
		notLoggable |= isCharacterMismatchError(msg);
		return !notLoggable;
	}

	private boolean isCharacterMismatchError(String msg) {
		return msg.contains("character mismatch");
	}

	private boolean isHTTPError(String msg) {
		return msg.contains("Server returned HTTP response code");
	}

	private void addFeed(Node node) throws DownloadFeedException {
		String link = node.getText();
		LOG.info("Feed crawler thinks you could add : " + link);
		link = link.replaceAll("\'", "\"");
		String rssLink = link.split("href=\"")[1].split("\"")[0];
		if (rssLink.startsWith("http")) {
			add(rssLink);
		}
	}

	private void add(String feedUrl) throws DownloadFeedException {
		if (!feedFetcherService.exists(feedUrl)) {
			feedFetcherService.addNewFeed(feedUrl);
			numFeedsAdded++;
			LOG.info("Crawled feeds : " + numFeedsAdded + "/" + maxFeedsToAdd);
		}
	}

	public static void setMaxFeedsToAdd(int maxFeedsToAdd) {
		FeedCrawler.maxFeedsToAdd = maxFeedsToAdd;
	}
}