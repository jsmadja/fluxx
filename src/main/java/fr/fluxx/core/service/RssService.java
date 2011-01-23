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
package fr.fluxx.core.service;

import java.io.IOException;
import java.io.StringWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.servlet.http.HttpServletRequest;

import com.sun.syndication.feed.synd.SyndCategory;
import com.sun.syndication.feed.synd.SyndCategoryImpl;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEnclosure;
import com.sun.syndication.feed.synd.SyndEnclosureImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;

import fr.fluxx.core.cache.CustomFeedCache;
import fr.fluxx.core.domain.Category;
import fr.fluxx.core.domain.CustomFeed;
import fr.fluxx.core.domain.DownloadableItem;
import fr.fluxx.core.domain.Feed;
import fr.fluxx.core.domain.Item;
import fr.fluxx.core.interceptor.ChronoInterceptor;
import fr.fluxx.core.resource.FluxxMessage;

@Stateful
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
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
        StringBuilder description = new StringBuilder();
        for (Feed f : customFeed.getFeeds()) {
            description.append(f.getTitle()).append(" ");
        }
        Feed feed = new Feed();
        List<Item> items = customFeedService.findItemsOfLastDaysByCustomFeed(customFeed);
        feed.setItems(items);
        feed.setUrl(host);
        feed.setTitle(titre);
        feed.setDescription(description.toString());
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
