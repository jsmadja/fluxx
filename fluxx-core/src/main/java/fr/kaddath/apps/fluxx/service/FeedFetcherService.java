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

import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndPerson;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import fr.kaddath.apps.fluxx.domain.Feed;
import fr.kaddath.apps.fluxx.domain.Item;
import fr.kaddath.apps.fluxx.exception.DownloadFeedException;
import fr.kaddath.apps.fluxx.exception.InvalidItemException;
import fr.kaddath.apps.fluxx.interceptor.ChronoInterceptor;
import org.apache.commons.lang.StringUtils;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
@Interceptors({ChronoInterceptor.class})
public class FeedFetcherService {

    private static final Logger LOG = Logger.getLogger(FeedFetcherService.class.getName());

    @EJB
    ItemService itemService;

    @EJB
    FeedService feedService;

    @EJB
    ItemBuilderService itemBuilderService;

    @PersistenceContext
    EntityManager em;

    public Feed addNewFeed(String feedUrl) throws DownloadFeedException {
        feedUrl = clean(feedUrl);

        Feed feed = feedService.findFeedByUrl(feedUrl);
        if (feed == null) {
            feed = fetch(new Feed(feedUrl));
        }
        return feed;
    }

    private String clean(String url) {
        try {
            url = StringUtils.trimToEmpty(url);
            url = org.apache.commons.lang.StringEscapeUtils.unescapeHtml(url);
            url = URLDecoder.decode(url, "UTF-8");
            url = url.trim();
        } catch (UnsupportedEncodingException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }
        return url;
    }

    private Feed fetch(Feed feed) throws DownloadFeedException {
        try {
            URL feedUrl = new URL(feed.getUrl());
            SyndFeedInput syndFeedInput = new SyndFeedInput();
            SyndFeed syndFeed = syndFeedInput.build(new XmlReader(feedUrl));
            return updateWithFeedContent(feed, syndFeed);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
            throw new DownloadFeedException(e.getMessage());
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Feed updateExistingFeed(Feed feed) {
        try {
            feed.setInError(false);
            fetch(feed);
        } catch (Exception e) {
            LOG.warning(e.getMessage());
            feed.setInError(true);
            feed = feedService.store(feed);
        }
        return feed;
    }

    public Feed createFromSyndFeed(Feed feed, SyndFeed syndFeed) {
        updateFeedInformations(feed, syndFeed);
        updateFeedItems(feed, syndFeed);
        return feedService.store(feed);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    private Feed updateWithFeedContent(Feed feed, SyndFeed syndFeed) {
        try {
            feed.setInError(false);
            feed = createFromSyndFeed(feed, syndFeed);
        } catch (Exception e) {
            LOG.throwing("FeedFetcherService", "createFromSyndFeed", e);
            feed.setInError(true);
            feed = feedService.store(feed);
        }
        return feed;
    }

    private void updateFeedInformations(Feed feed, SyndFeed syndFeed) {
        feed.setAuthor(syndFeed.getAuthor());
        if (StringUtils.isEmpty(feed.getAuthor()) && syndFeed.getAuthors() != null && !syndFeed.getAuthors().isEmpty()) {
            SyndPerson person = (SyndPerson) syndFeed.getAuthors().get(0);
            feed.setAuthor(person.getName());
        }
        if (StringUtils.isBlank(feed.getAuthor())) {
            feed.setAuthor(syndFeed.getCopyright());
        }
        feed.setDescription(syndFeed.getDescription());
        feed.setEncoding(syndFeed.getEncoding());
        feed.setFeedType(syndFeed.getFeedType());
        feed.setPublishedDate(syndFeed.getPublishedDate());
        feed.setTitle(syndFeed.getTitle());
    }

    private void updateFeedItems(Feed feed, SyndFeed syndFeed) {
        for (Object o : syndFeed.getEntries()) {
            SyndEntryImpl syndEntryImpl = (SyndEntryImpl) o;
            String link = syndEntryImpl.getLink();
            if (link != null) {
                Item item = itemService.findItemByLink(link);
                if (item == null) {
                    try {
                        item = itemBuilderService.createItemFromSyndEntry(syndEntryImpl);
                        item.setFeed(feed);
                        feed.getItems().add(item);
                        feed.setLastUpdate(Calendar.getInstance().getTime());
                    } catch (InvalidItemException e) {
                        LOG.info(e.getMessage());
                    } catch (Exception e) {
                        LOG.severe(e.getMessage());
                    }
                }
            }
        }
    }

    public boolean exists(String feedUrl) {
        return feedService.findFeedByUrl(feedUrl) != null;
    }
}
