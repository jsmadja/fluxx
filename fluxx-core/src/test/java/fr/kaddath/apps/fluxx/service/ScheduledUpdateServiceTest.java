package fr.kaddath.apps.fluxx.service;

import org.junit.Test;

import fr.kaddath.apps.fluxx.AbstractTest;

public class ScheduledUpdateServiceTest extends AbstractTest {

	@Test
	public void should_update_all_feeds() throws Exception {
		if (isIntegrationTest) {
			createFeeds();
			scheduledUpdateService.updateAll();
			scheduledUpdateService.updateAll();
		}
	}

}