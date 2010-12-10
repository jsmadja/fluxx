package fr.kaddath.apps.fluxx.service;

import org.junit.Test;

import fr.kaddath.apps.fluxx.AbstractIntegrationTest;

public class ScheduledUpdateServiceTest extends AbstractIntegrationTest {

	@Test
	public void should_update_all_feeds() throws Exception {
		scheduledUpdateService.updateAll();
	}

}