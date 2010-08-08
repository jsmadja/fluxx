package fr.kaddath.apps.fluxx.service;

import java.io.IOException;
import java.io.StringWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;
import fr.kaddath.apps.fluxx.cache.RssFeedCache;
import fr.kaddath.apps.fluxx.domain.AggregatedFeed;
import fr.kaddath.apps.fluxx.domain.Item;
import fr.kaddath.apps.fluxx.domain.Feed;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

@Stateful
public class RssService {

    private static final String FEED_TYPE = "rss_2.0";
    private static final String DESCRIPTION_TYPE = "text/plain";
    private SyndFeed syndFeed;

    @Inject
    private RssFeedCache feedCache;

    @Resource(lookup="fluxx/feed/encoding")
    private String feedEncoding;

    @EJB
    private AggregatedFeedService aggregatedFeedService;

    private String createRssFeed(AggregatedFeed aggregatedFeed, String host) throws ParseException, IOException, FeedException {
        syndFeed = new SyndFeedImpl();
        Feed feed = createFeed(aggregatedFeed, host);
        addFeedInformations(feed);
        addEntries(feed);
        return buildXmlContent();
    }

    private Feed createFeed(AggregatedFeed aggregatedFeed, String host) {
        String titre = aggregatedFeed.getName() + " - Flux aggrégés depuis les " + aggregatedFeed.getNumLastDay() + " derniers jours.";
        String description = "";
        for (Feed f : aggregatedFeed.getFeeds()) {
            description += f.getTitle() + " / ";
        }
        Feed feed = new Feed();
        List<Item> items = aggregatedFeedService.findItemsByAggregatedFeed(aggregatedFeed);
        feed.setItems(items);
        feed.setUrl(host);
        feed.setTitle(titre);
        feed.setDescription(description);
        return feed;
    }

    private String buildXmlContent() throws IOException, FeedException {
        StringWriter writer = new StringWriter();
        SyndFeedOutput output = new SyndFeedOutput();
        output.output(syndFeed, writer);
        writer.close();
        String xmlContent = writer.getBuffer().toString();
        return new String(xmlContent.getBytes(), feedEncoding);
    }

    private void addFeedInformations(Feed feed) {
        syndFeed.setFeedType(FEED_TYPE);
        syndFeed.setEncoding(feedEncoding);
        syndFeed.setTitle(feed.getTitle());
        syndFeed.setLink(feed.getUrl());
        syndFeed.setDescription(feed.getDescription());
    }

    private void addEntries(Feed feed) throws ParseException {
        List<SyndEntry> entries = new ArrayList<SyndEntry>();
        for (Item element : feed.getItems()) {
            entries.add(createEntry(element));
        }
        syndFeed.setEntries(entries);
    }

    private SyndEntry createEntry(Item item) throws ParseException {
        SyndEntry entry = new SyndEntryImpl();
        SyndContent description = new SyndContentImpl();
        entry.setTitle(item.getTitle());
        entry.setLink(item.getLink());
        entry.setPublishedDate(item.getPublishedDate());
        description.setType(DESCRIPTION_TYPE);
        description.setValue(item.getDescription());
        entry.setDescription(description);
        return entry;
    }

    public String getRssFeedById(String aggregatedFeedId, HttpServletRequest request) {
        AggregatedFeed feed = aggregatedFeedService.findByAggregatedFeedId(aggregatedFeedId);
        if (feed != null) {
            if (feedCache.contains(aggregatedFeedId)) {
                return feedCache.get(aggregatedFeedId);
            } else {
                String url = aggregatedFeedService.createUrl(request, feed);
                try {
                    feedCache.put(aggregatedFeedId, createRssFeed(feed, url));
                    return feedCache.get(aggregatedFeedId);
                } catch (Exception ex) {
                    Logger.getLogger(RssService.class.getName()).log(Level.SEVERE, null, ex);
                    return "";
                }
            }
        } else {
            return "";
        }
    }

    public String getFeedEncoding() {
        return feedEncoding;
    }

    public void setFeedEncoding(String feedEncoding) {
        this.feedEncoding = feedEncoding;
    }

}
