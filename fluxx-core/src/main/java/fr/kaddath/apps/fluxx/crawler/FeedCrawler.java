package fr.kaddath.apps.fluxx.crawler;

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
import fr.kaddath.apps.fluxx.exception.DownloadFeedException;
import fr.kaddath.apps.fluxx.service.CrawlerService;
import fr.kaddath.apps.fluxx.service.Services;

public class FeedCrawler extends WebCrawler {

	private static final Logger LOG = Logger.getLogger(CrawlerService.class.getName());

	public static int MAX_FEEDS_TO_ADD = 100;

	private static int NUM_FEEDS_ADDED = 0;

	private static final Pattern filters = Pattern
			.compile(".*(\\.(css|js|bmp|gif|jpe?g|png|tiff?|mid|mp2|mp3|mp4|wav|avi|mov|mpeg|ico|ram|m4v|pdf|rm|smil|wmv|swf|wma|zip|rar|gz))$");

	@Override
	public boolean shouldVisit(WebURL url) {
		if (NUM_FEEDS_ADDED >= MAX_FEEDS_TO_ADD) {
			return false;
		}

		String href = url.getURL().toLowerCase();
		return (!filters.matcher(href).matches());
	}

	@Override
	public void visit(Page page) {
		if (NUM_FEEDS_ADDED >= MAX_FEEDS_TO_ADD) {
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
			while (nodes.hasMoreNodes()) {
				try {
					addFeed(nodes.nextNode());
				} catch (DownloadFeedException e) {
					LOG.info(e.getMessage());
				}
			}
		} catch (Exception e) {
			LOG.info(e.getMessage());
		}
	}

	private void addFeed(Node node) throws DownloadFeedException {
		String rssLink = node.getText().split("href=\"")[1].split("\"")[0];
		if (rssLink.startsWith("http")) {
			add(rssLink);
		}
	}

	private void add(String feedUrl) throws DownloadFeedException {
		Services.getFeedFetcherService().addNewFeed(feedUrl);
		NUM_FEEDS_ADDED++;
	}
}