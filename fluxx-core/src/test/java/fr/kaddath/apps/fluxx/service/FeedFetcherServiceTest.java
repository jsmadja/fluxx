package fr.kaddath.apps.fluxx.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.junit.Test;

import fr.kaddath.apps.fluxx.AbstractTest;

public class FeedFetcherServiceTest extends AbstractTest {

	@Test
	public void should_update() throws Exception {
		createFeeds();
		feedFetcherService.updateAll();
		feedFetcherService.updateAll();
	}

	private void createFeeds() throws FileNotFoundException {
		int maxFeeds = 20;
		Scanner sc = new Scanner(new File("src/test/resources/feeds.urls"));
		int i = 0;
		while (sc.hasNextLine() && i < maxFeeds) {
			String url = sc.nextLine();
			createFeed(url);
			i++;
		}

	}
}