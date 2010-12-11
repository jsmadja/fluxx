package fr.kaddath.apps.fluxx.service;

import com.sun.syndication.feed.synd.*;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;
import fr.kaddath.apps.fluxx.cache.CustomFeedCache;
import fr.kaddath.apps.fluxx.domain.*;
import fr.kaddath.apps.fluxx.interceptor.ChronoInterceptor;
import fr.kaddath.apps.fluxx.resource.FluxxMessage;

import javax.ejb.EJB;
import javax.ejb.Stateful;
import javax.interceptor.Interceptors;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.StringWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateful
@Interceptors({ChronoInterceptor.class})
public class RssService {

    private static final String FEED_TYPE = "rss_2.0";
    private static final String DESCRIPTION_TYPE = "text/plain";
    private SyndFeed syndFeed;

    @EJB
    private CustomFeedCache feedCache;

    @EJB
    private CustomFeedService customFeedService;

    private String createRssFeed(CustomFeed customFeed, String host, String encoding) throws ParseException,
            IOException, FeedException {
        syndFeed = new SyndFeedImpl();
        Feed feed = createFeed(customFeed, host);
        addFeedInformations(feed);
        addEntries(feed);
        return buildXmlContent(encoding);
    }

    private Feed createFeed(CustomFeed customFeed, String host) {
        String titre = FluxxMessage.m("feed_title", customFeed.getCategory(), customFeed.getNumLastDay());
        String description = "";
        for (Feed f : customFeed.getFeeds()) {
            description += f.getTitle() + " / ";
        }
        Feed feed = new Feed();
        List<Item> items = customFeedService.findItemsByCustomFeed(customFeed);
        feed.setItems(items);
        feed.setUrl(host);
        feed.setTitle(titre);
        feed.setDescription(description);
        return feed;
    }

    private String buildXmlContent(String encoding) throws IOException, FeedException {
        StringWriter writer = new StringWriter();
        SyndFeedOutput output = new SyndFeedOutput();
        output.output(syndFeed, writer);
        writer.close();
        String xmlContent = writer.getBuffer().toString();
        return new String(xmlContent.getBytes(), encoding);
    }

    private void addFeedInformations(Feed feed) {
        syndFeed.setFeedType(FEED_TYPE);
        syndFeed.setEncoding(feed.getEncoding());
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

    private SyndEntry createEntry(Item item) {
        SyndEntry entry = new SyndEntryImpl();
        addEntryInformations(entry, item);
        addEnclosures(item, entry);
        addCategories(item, entry);
        return entry;
    }

    private void addCategories(Item item, SyndEntry entry) {
        if (!item.getCategories().isEmpty()) {
            createCategories(item, entry);
        }
    }

    private void addEnclosures(Item item, SyndEntry entry) {
        if (!item.getDownloadableItems().isEmpty()) {
            createEnclosures(item, entry);
        }
    }

    private void addEntryInformations(SyndEntry entry, Item item) {
        SyndContent description = new SyndContentImpl();
        entry.setTitle(item.getTitle());
        entry.setLink(item.getLink());
        entry.setPublishedDate(item.getPublishedDate());
        description.setType(DESCRIPTION_TYPE);
        description.setValue(item.getDescription());
        entry.setDescription(description);
    }

    private void createEnclosures(Item item, SyndEntry entry) {
        Set<DownloadableItem> downloadableItems = item.getDownloadableItems();
        List<SyndEnclosure> enclosures = new ArrayList<SyndEnclosure>();
        for (DownloadableItem di : downloadableItems) {
            SyndEnclosure enclosure = new SyndEnclosureImpl();
            enclosure.setLength(di.getFileLength());
            enclosure.setType(di.getType());
            enclosure.setUrl(di.getUrl());
            enclosures.add(enclosure);
        }
        entry.setEnclosures(enclosures);
    }

    private void createCategories(Item item, SyndEntry entry) {
        Set<Category> feedCategories = item.getCategories();
        List<SyndCategory> categories = new ArrayList<SyndCategory>();
        for (Category fc : feedCategories) {
            SyndCategory category = new SyndCategoryImpl();
            category.setName(fc.getName());
            categories.add(category);
        }
        entry.setCategories(categories);
    }

    public String getRssFeedById(Long id, HttpServletRequest request, String encoding) {
        CustomFeed customFeed = customFeedService.findById(id);
        return getRssFeed(customFeed, request, encoding);
    }

    public String getRssFeedByUsernameAndCategory(String username, String category, HttpServletRequest request,
                                                  String encoding) {
        CustomFeed customFeed = customFeedService.findByUsernameAndName(username, category);
        return getRssFeed(customFeed, request, encoding);
    }

    private String getRssFeed(CustomFeed customFeed, HttpServletRequest request, String encoding) {
        if (customFeed != null) {
            Long id = customFeed.getId();
            if (feedCache.contains(id)) {
                return feedCache.get(id);
            } else {
                String url = customFeedService.createUrl(request, customFeed);
                try {
                    feedCache.put(id, createRssFeed(customFeed, url, encoding));
                    return feedCache.get(id);
                } catch (Exception ex) {
                    Logger.getLogger(RssService.class.getName()).log(Level.SEVERE, null, ex);
                    return "";
                }
            }
        } else {
            return "";
        }
    }

}
