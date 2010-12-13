package fr.fluxx.core;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import fr.fluxx.core.FeedUpdateData;
import fr.fluxx.core.domain.Feed;

public class FeedUpdateDataTest {

	@Test
	public void should_log_with_0_feed_added() {
		FeedUpdateData fud = new FeedUpdateData();
		assertEquals(0, fud.getDownloadSizeInBytes());
		assertEquals(0, fud.getNumFeeds());
	}

	@Test
	public void should_log_with_1_feed_add() {
		FeedUpdateData fud = new FeedUpdateData();
		Feed feed = new Feed();
		int feedSize = 1;
		feed.setSize(feedSize);
		fud.add(feed);
		assertEquals(1, fud.getNumFeeds());
		assertEquals(feedSize, fud.getDownloadSizeInBytes());
	}

	@Test
	public void should_not_fail_when_passing_null() {
		FeedUpdateData fud = new FeedUpdateData();
		fud.add(null);
		assertEquals(0, fud.getNumFeeds());
		assertEquals(0, fud.getDownloadSizeInBytes());
	}

	@Test
	public void should_not_add_negative_size() {
		FeedUpdateData fud = new FeedUpdateData();
		Feed feed = new Feed();
		feed.setSize(-1);
		fud.add(feed);
		assertEquals(0, fud.getNumFeeds());
		assertEquals(0, fud.getDownloadSizeInBytes());
	}
	
	@Test
	public void should_convert_in_MBytes() {
		FeedUpdateData fud = new FeedUpdateData();
		Feed feed = new Feed();
		feed.setSize(1024*1024);
		fud.add(feed);
		assertEquals("1", fud.getDownloadSizeInMBytes());
	}
}
