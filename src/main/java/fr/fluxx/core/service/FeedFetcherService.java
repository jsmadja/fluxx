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

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.ExcludeClassInterceptors;
import javax.interceptor.Interceptors;

import org.apache.commons.lang.StringUtils;

import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndPerson;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

import fr.fluxx.core.domain.Feed;
import fr.fluxx.core.domain.Item;
import fr.fluxx.core.exception.DownloadFeedException;
import fr.fluxx.core.exception.InvalidItemException;
import fr.fluxx.core.interceptor.ChronoInterceptor;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
@Interceptors({ChronoInterceptor.class})
public class FeedFetcherService {

	private static final Logger LOG = Logger.getLogger(FeedFetcherService.class.getName());

	@EJB
	private ItemService itemService;

	@EJB
	private FeedService feedService;

	@EJB
	private ItemBuilderService itemBuilderService;

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Feed addNewFeed(String feedUrl) throws DownloadFeedException {
		String cleanedFeedUrl = clean(feedUrl);

		Feed feed = feedService.findFeedByUrl(cleanedFeedUrl);
		if (feed == null) {
			feed = fetch(new Feed(cleanedFeedUrl));
		}
		return feed;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Feed updateExistingFeed(Feed feed) {
		Feed updatedFeed;
		try {
			feed.setInError(false);
			updatedFeed = fetch(feed);
		} catch (Exception e) {
			LOG.warning(e.getMessage());
			feed.setInError(true);
			updatedFeed = feedService.store(feed);
		}
		return updatedFeed;
	}

	@ExcludeClassInterceptors
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Feed createFromSyndFeed(Feed feed, SyndFeed syndFeed) {
		updateFeedInformations(feed, syndFeed);
		updateFeedItems(feed, syndFeed);
		return feedService.store(feed);
	}

	private Feed updateWithFeedContent(final Feed feed, final SyndFeed syndFeed) {
		Feed updatedFeed;
		try {
			feed.setInError(false);
			updatedFeed = createFromSyndFeed(feed, syndFeed);
		} catch (Exception e) {
			LOG.throwing("FeedFetcherService", "createFromSyndFeed", e);
			feed.setInError(true);
			updatedFeed = feedService.store(feed);
		}
		return updatedFeed;
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
		if (syndFeed.getPublishedDate() == null) {
			feed.setPublishedDate(new Date());
		} else {
			feed.setPublishedDate(syndFeed.getPublishedDate());
		}
		if (feed.getTitle() == null) {
			feed.setTitle(feed.getUrl());
		} else {
			feed.setTitle(syndFeed.getTitle());
		}
		feed.setDescription(syndFeed.getDescription());
		feed.setFeedType(syndFeed.getFeedType());
	}

	private void updateFeedItems(Feed feed, SyndFeed syndFeed) {
		for (Object o : syndFeed.getEntries()) {
			SyndEntryImpl syndEntryImpl = (SyndEntryImpl) o;
			String link = syndEntryImpl.getLink();
			if (link != null) {
				try {
					Item item = itemService.findItemByLink(link);
					if (item == null) {
						item = itemBuilderService.createItemFromSyndEntry(syndEntryImpl);
						item.setFeed(feed);
					}
					if (!feed.getItems().contains(item)) {
						feed.getItems().add(item);
					}
					feed.setLastUpdate(Calendar.getInstance().getTime());
				} catch (InvalidItemException e) {
					LOG.info(e.getMessage());
				} catch (Exception e) {
					LOG.severe(e.getMessage());
				}
			}
		}
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

	public boolean exists(String feedUrl) {
		return feedService.findFeedByUrl(feedUrl) != null;
	}

	public void setFeedService(FeedService feedService) {
		this.feedService = feedService;
	}
}
