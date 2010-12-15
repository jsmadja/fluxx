package fr.fluxx.core.domain;

import java.util.Date;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;


public class FeedTest {

	@Test
	public void should_calculate_next_update() {
		
		Feed feed = new Feed();
		feed.setPublicationRatio(0.1);
		feed.calculateNextUpdate();
		
		Date actual = feed.getNextUpdate();
		
		DateTime expected = new DateTime();
		expected = expected.plusHours(12);
		
		System.err.println(expected+" == "+actual);
		
		Assert.assertEquals(expected.getMillis(), actual.getTime());
	}
	
}
