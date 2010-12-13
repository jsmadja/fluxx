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

import javax.ejb.Stateless;

import edu.uci.ics.crawler4j.crawler.CrawlController;
import fr.kaddath.apps.fluxx.crawler.FeedCrawler;

@Stateless
public class CrawlerService {

	private static final int NUMBER_OF_CRAWLERS = 1;

	public void crawl(String url, int maxFeedsToAdd) throws Exception {
		CrawlController controller = new CrawlController("/tmp/fluxx/crawl");
		controller.addSeed(url);
		FeedCrawler.setMaxFeedsToAdd(maxFeedsToAdd);
		controller.start(FeedCrawler.class, NUMBER_OF_CRAWLERS);
	}

}
