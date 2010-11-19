package fr.kaddath.apps.fluxx.service;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang.StringUtils;

import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndPerson;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

import fr.kaddath.apps.fluxx.cache.RssFeedCache;
import fr.kaddath.apps.fluxx.cache.RssItemCache;
import fr.kaddath.apps.fluxx.domain.Feed;
import fr.kaddath.apps.fluxx.domain.Item;
import fr.kaddath.apps.fluxx.exception.DownloadFeedException;
import fr.kaddath.apps.fluxx.interceptor.ChronoInterceptor;

@Stateless
public class FeedFetcherService {

	private static final Logger LOG = Logger.getLogger(FeedFetcherService.class.getName());

	@EJB
	AsynchronousFeedDownloaderService downloaderService;

	@EJB
	ItemService itemService;

	@EJB
	FeedService feedService;

	@EJB
	ItemBuilderService itemBuilderService;

	@PersistenceContext
	EntityManager em;

	@Inject
	RssFeedCache feedCache;

	@Inject
	RssItemCache lastItemCache;

	public Feed add(String feedUrl) throws DownloadFeedException {
		Feed feed = new Feed();
		feed.setUrl(feedUrl);
		fetch(feed);
		feed = em.merge(feed);
		em.flush();
		return feed;
	}

	public void fetch(Feed feed) throws DownloadFeedException {
		try {
			URL feedUrl = new URL(feed.getUrl());
			SyndFeedInput syndFeedInput = new SyndFeedInput();
			SyndFeed syndFeed = syndFeedInput.build(new XmlReader(feedUrl));
			updateWithFeedContent(feed, syndFeed);
		} catch (Exception e) {
			throw new DownloadFeedException(e.getMessage());
		}
	}

	public void update(Feed feed, SyndFeed syndFeed) throws DownloadFeedException {
		updateFeedInformations(feed, syndFeed);
		if (feed.getId() == null) {
			em.persist(feed);
		} else {
			feed = em.merge(feed);
		}
		em.flush();
		updateFeedItems(feed, syndFeed);
	}

	private List<Future<Object[]>> downloadFeeds() {
		List<Future<Object[]>> couples = new ArrayList<Future<Object[]>>();
		List<Feed> feeds = feedService.findAllFeedsNotInError();
		for (int i = 0; i < feeds.size(); i++) {
			Future<Object[]> couple = downloaderService.downloadFeedContent(feeds.get(i));
			try {
				if (couple != null && couple.get() != null) {
					couples.add(couple);
				}
			} catch (Exception e) {
				LOG.severe(e.getMessage());
			}
		}
		return couples;
	}

	private void updateFeeds(List<Future<Object[]>> couples) {
		for (int i = 0; i < couples.size();) {
			Future<Object[]> couple = couples.get(i);
			if (couple.isDone()) {
				try {
					Feed feed = (Feed) couple.get()[0];
					SyndFeed syndFeed = (SyndFeed) couple.get()[1];
					if (mustBeUpdated(syndFeed)) {
						update(feed, syndFeed);
						lastItemCache.put(findLastEntry(syndFeed));
						LOG.log(Level.INFO, "[{0}%] Update feed : {1} ... updated successfully", new Object[] {
								getInPercent(i + 1, couples.size()), feed.getTitle() });
					}
				} catch (Exception ex) {
					Logger.getLogger(FeedService.class.getName()).log(Level.SEVERE, null, ex);
				}
				i++;
			} else {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException ex) {
					Logger.getLogger(FeedService.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	private void updateWithFeedContent(Feed feed, SyndFeed syndFeed) {
		try {
			feed.setInError(false);
			update(feed, syndFeed);
			em.merge(feed);
		} catch (Throwable e) {
			LOG.warning(e.getMessage());
			feed.setInError(true);
			em.merge(feed);
		}
	}

	@Interceptors({ ChronoInterceptor.class })
	@Schedule(minute = "*/15", hour = "*")
	public void updateAllAsynchronously() {
		feedCache.clear();
		List<Future<Object[]>> couples = downloadFeeds();
		updateFeeds(couples);
	}

	private int getInPercent(int counter, float numFeeds) {
		return (int) ((counter / numFeeds) * 100);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void update(Feed feed) {
		try {
			feed.setInError(false);
			fetch(feed);
			em.merge(feed);
		} catch (Throwable e) {
			LOG.warning(e.getMessage());
			feed.setInError(true);
			em.merge(feed);
		}
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
				Item feedItem = itemService.findItemByLink(link, feed);
				if (feedItem == null) {
					LOG.log(Level.INFO, "New item found : {0} with date : {1}",
							new Object[] { link, syndEntryImpl.getPublishedDate() });
					Item item = itemBuilderService.createItemFromSyndEntry(syndEntryImpl);
					item.setFeed(feed);
					em.persist(item);
					feed.setLastUpdate(Calendar.getInstance().getTime());
					feed = em.merge(feed);
					em.flush();
				}
			}
		}
	}

	private boolean mustBeUpdated(SyndFeed syndFeed) {
		SyndEntryImpl syndEntryImpl = findLastEntry(syndFeed);
		return !lastItemCache.contains(syndEntryImpl.getLink());
	}

	private SyndEntryImpl findLastEntry(SyndFeed syndFeed) {
		return (SyndEntryImpl) syndFeed.getEntries().get(0);
	}

}
