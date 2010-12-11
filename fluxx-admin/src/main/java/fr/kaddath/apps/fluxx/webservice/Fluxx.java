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

package fr.kaddath.apps.fluxx.webservice;

import javax.ejb.EJB;
import javax.jws.WebMethod;
import javax.jws.WebService;

import fr.kaddath.apps.fluxx.exception.DownloadFeedException;
import fr.kaddath.apps.fluxx.service.CrawlerService;
import fr.kaddath.apps.fluxx.service.FeedFetcherService;
import fr.kaddath.apps.fluxx.service.ScheduledUpdateService;

@WebService
public class Fluxx {

	@EJB
	private FeedFetcherService feedFetcherService;

	@EJB
	private ScheduledUpdateService scheduledUpdateService;

	@EJB
	private CrawlerService crawlerService;

	@WebMethod
	public void addFeed(String feedUrl) throws DownloadFeedException {
		feedFetcherService.addNewFeed(feedUrl);
	}

	@WebMethod
	public void crawl(String url, Integer maxFeedsToAdd) throws Exception {
		crawlerService.crawl(url, maxFeedsToAdd);
	}

	@WebMethod
	public void updateAll() {
		scheduledUpdateService.updateAll();
	}

	@WebMethod
	public void updateTopPriorityFeeds() {
		scheduledUpdateService.updateTopPriorityFeeds();
	}
}
