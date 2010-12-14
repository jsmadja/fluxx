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

package edu.uci.ics.crawler4j.crawler;

import java.io.File;
import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;

import edu.uci.ics.crawler4j.frontier.DocIDServer;
import edu.uci.ics.crawler4j.frontier.Frontier;
import edu.uci.ics.crawler4j.url.URLCanonicalizer;
import edu.uci.ics.crawler4j.url.WebURL;
import edu.uci.ics.crawler4j.util.IO;

/**
 * Copyright (C) 2010
 * 
 * @author Yasser Ganjisaffar <yganjisa at uci dot edu>
 */

public final class CrawlController {

	private static final int MAX_POLITENESS_DELAY = 10000;

	private static final int ONE_SECOND_IN_MS = 1000;

	private static final int SLEEP_TIME = 30;

	private static final Logger LOG = Logger.getLogger(CrawlController.class.getName());

	private final List<Object> crawlersLocalData = new ArrayList<Object>();

	public List<Object> getCrawlersLocalData() {
		return crawlersLocalData;
	}

	private boolean crawl = true;

	private List<Thread> threads;

	public CrawlController(String storageFolder) {
		File folder = new File(storageFolder);
		if (!folder.exists()) {
			boolean result = folder.mkdirs();
			if (!result) {
				throw new RuntimeException("Crawler can't start");
			}
		}

		EnvironmentConfig envConfig = new EnvironmentConfig();
		envConfig.setAllowCreate(true);
		envConfig.setTransactional(false);
		envConfig.setLocking(false);

		File envHome = new File(storageFolder + "/frontier");
		if (!envHome.exists()) {
			boolean result = envHome.mkdir();
			if (!result) {
				throw new RuntimeException("Crawler can't start");
			}
		}
		IO.deleteFolderContents(envHome);

		Environment env = new Environment(envHome, envConfig);
		Frontier.init(env);
		DocIDServer.init(env);

		PageFetcher.startConnectionMonitorThread();
	}

	public void stop() {
		crawl = false;
	}

	public <T extends WebCrawler> void start(Class<T> clazz, int numberOfCrawlers) {
		try {
			crawlersLocalData.clear();
			threads = new ArrayList<Thread>();
			List<T> crawlers = new ArrayList<T>();
			for (int i = 1; i <= numberOfCrawlers; i++) {
				startThread(clazz, crawlers, i);
			}
			while (crawl) {
				sleep(SLEEP_TIME);
				crawl(clazz, crawlers);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private <T extends WebCrawler> void crawl(Class<T> clazz, List<T> crawlers) throws InstantiationException,
			IllegalAccessException {
		boolean someoneIsWorking = false;
		for (int i = 0; i < threads.size(); i++) {
			Thread thread = threads.get(i);
			someoneIsWorking = crawl(clazz, crawlers, someoneIsWorking, i, thread);
		}
		if (!someoneIsWorking) {
			// Make sure again that none of the threads are alive.
			sleep(SLEEP_TIME);

			if (!isAnyThreadWorking()) {
				long queueLength = Frontier.getQueueLength();
				if (queueLength > 0) {
					return;
				}
				sleep(SLEEP_TIME);
				queueLength = Frontier.getQueueLength();
				if (queueLength > 0) {
					return;
				}
				Frontier.close();
				LOG.info("All of the crawlers are stopped. Finishing the process.");
				for (T crawler : crawlers) {
					crawler.onBeforeExit();
					crawlersLocalData.add(crawler.getMyLocalData());
				}

				// At this step, frontier notifies the threads that were waiting for new URLs and they should
				// stop
				// We will wait a few seconds for them and then return.
				Frontier.finish();
				sleep(SLEEP_TIME);
				return;
			}
		}
	}

	private <T extends WebCrawler> boolean crawl(Class<T> clazz, List<T> crawlers, boolean someoneIsWorking, int i,
			Thread thread) throws InstantiationException, IllegalAccessException {
		if (!thread.isAlive()) {
			LOG.info("Thread " + i + " was dead, I'll recreate it.");
			T crawler = clazz.newInstance();
			thread = new Thread(crawler, "Crawler " + (i + 1));
			threads.remove(i);
			threads.add(i, thread);
			crawler.setThread(thread);
			crawler.setMyId(i + 1);
			crawler.setMyController(this);
			thread.start();
			crawlers.remove(i);
			crawlers.add(i, crawler);
		} else if (thread.getState() == State.RUNNABLE) {
			someoneIsWorking = true;
		}
		return someoneIsWorking;
	}

	private <T extends WebCrawler> void startThread(Class<T> clazz, List<T> crawlers, int i)
			throws InstantiationException, IllegalAccessException {
		T crawler = clazz.newInstance();
		Thread thread = new Thread(crawler, "Crawler " + i);
		crawler.setThread(thread);
		crawler.setMyId(i);
		crawler.setMyController(this);
		thread.start();
		crawlers.add(crawler);
		threads.add(thread);
		LOG.info("Crawler " + i + " started.");
	}

	private void sleep(int seconds) {
		try {
			Thread.sleep(seconds * ONE_SECOND_IN_MS);
		} catch (Exception e) {
		}
	}

	private boolean isAnyThreadWorking() {
		boolean someoneIsWorking = false;
		for (Thread thread : threads) {
			if (thread.isAlive() && thread.getState() == State.RUNNABLE) {
				someoneIsWorking = true;
			}
		}
		return someoneIsWorking;
	}

	public void addSeed(String pageUrl) {
		String canonicalUrl = URLCanonicalizer.getCanonicalURL(pageUrl);
		if (canonicalUrl == null) {
			LOG.error("Invalid seed URL: " + pageUrl);
			return;
		}
		int docid = DocIDServer.getDocID(canonicalUrl);
		if (docid > 0) {
			// This URL is already seen.
			return;
		}
		WebURL url = new WebURL(canonicalUrl, -docid);
		Frontier.schedule(url);
	}

	public void setPolitenessDelay(int milliseconds) {
		if (milliseconds < 0) {
			return;
		}
		if (milliseconds > MAX_POLITENESS_DELAY) {
			milliseconds = MAX_POLITENESS_DELAY;
		}
		PageFetcher.setPolitenessDelay(milliseconds);
	}

	public void setProxy(String proxyHost, int proxyPort) {
		PageFetcher.setProxy(proxyHost, proxyPort);
	}

	public static void setProxy(String proxyHost, int proxyPort, String username, String password) {
		PageFetcher.setProxy(proxyHost, proxyPort, username, password);
	}
}
