package fr.kaddath.apps.fluxx.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

import fr.kaddath.apps.fluxx.AbstractTest;
import fr.kaddath.apps.fluxx.domain.AggregatedFeed;
import fr.kaddath.apps.fluxx.domain.Feed;
import fr.kaddath.apps.fluxx.domain.Item;

public class AggregatedFeedServiceTest extends AbstractTest {

	private static final Logger log = Logger.getLogger(AggregatedFeedServiceTest.class.getName());

	@Test
	public void addAggregatedFeed() {
		Long old = aggregatedFeedService.getNumAggregatedFeeds();
		log.log(Level.INFO, "{0} aggregatedfeeds before adding", old);
		aggregatedFeedService.addAggregatedFeed("fluxxer", createRandomString(), 7);
		log.log(Level.INFO, "{0} aggregatedfeeds after adding", aggregatedFeedService.getNumAggregatedFeeds());
		assertTrue(aggregatedFeedService.getNumAggregatedFeeds() > old);
	}

	@Test
	public void testCreateUrl() throws Exception {
		AggregatedFeed feed = addRandomAggregatedFeed("fluxxer");
		String expResult = "http://" + serverName + ":" + serverPort + contextPath + "/rss?id=" + feed.getId();
		String result = aggregatedFeedService.createUrl(request, feed);
		log.info(result);
		assertEquals(expResult, result);
	}

	@Test
	public void findItemsByAggregatedFeed() {
		AggregatedFeed aggregatedFeed = addRandomAggregatedFeed("fluxxer");
		List<Feed> feeds = new ArrayList<Feed>();
		feeds.add(createFeed());
		aggregatedFeed.setFeeds(feeds);
		aggregatedFeed = aggregatedFeedService.merge(aggregatedFeed);
		List<Item> items = aggregatedFeedService.findItemsByAggregatedFeed(aggregatedFeed);
		assertTrue(items.size() > 0);
	}

	@Test
	public void findByAggregatedFeedId() {
		AggregatedFeed aggregatedFeed = aggregatedFeedService.addAggregatedFeed("fluxxer", createRandomString(), 7);
		AggregatedFeed af = aggregatedFeedService.findById(aggregatedFeed.getId());
		assertEquals(aggregatedFeed.getId(), af.getId());
	}

	@Test
	public void delete() {
		AggregatedFeed af1 = addRandomAggregatedFeed("fluxxer");
		AggregatedFeed af2 = addRandomAggregatedFeed("fluxxer");
		Long old = aggregatedFeedService.getNumAggregatedFeeds();
		log.log(Level.INFO, "{0} aggregatedfeeds before deleting", old);
		aggregatedFeedService.delete(af1);
		aggregatedFeedService.delete(af2);
		log.log(Level.INFO, "{0} aggregatedfeeds after deleting", aggregatedFeedService.getNumAggregatedFeeds());
		assertTrue(aggregatedFeedService.getNumAggregatedFeeds() < old);
	}
}