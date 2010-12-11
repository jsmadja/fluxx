package fr.kaddath.apps.fluxx.crawler;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.url.WebURL;
import fr.kaddath.apps.fluxx.exception.DownloadFeedException;
import fr.kaddath.apps.fluxx.service.CrawlerService;
import fr.kaddath.apps.fluxx.service.FeedFetcherService;
import fr.kaddath.apps.fluxx.service.Services;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.SimpleNodeIterator;

import java.net.URL;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class FeedCrawler extends WebCrawler {

    private static final Logger LOG = Logger.getLogger(CrawlerService.class.getName());

    public static int MAX_FEEDS_TO_ADD = 100;

    private static int NUM_FEEDS_ADDED = 0;

    private static final Pattern filters = Pattern
            .compile(".*(\\.(css|js|bmp|gif|jpe?g|png|tiff?|mid|mp2|mp3|mp4|wav|avi|mov|mpeg|ico|ram|m4v|pdf|rm|smil|wmv|swf|wma|zip|rar|gz))$");

    private final FeedFetcherService feedFetcherService;

    public FeedCrawler() {
        feedFetcherService = Services.getFeedFetcherService();
    }

    @Override
    public boolean shouldVisit(WebURL url) {
        if (!isCrawable()) {
            return false;
        }
        String href = url.getURL().toLowerCase();
        return (!filters.matcher(href).matches());
    }

    private boolean isCrawable() {
        boolean crawl = NUM_FEEDS_ADDED < MAX_FEEDS_TO_ADD;
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
            NUM_FEEDS_ADDED++;
            LOG.info("Crawled feeds : " + NUM_FEEDS_ADDED + "/" + MAX_FEEDS_TO_ADD);
        }
    }
}