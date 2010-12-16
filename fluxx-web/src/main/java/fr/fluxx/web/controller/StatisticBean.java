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

package fr.fluxx.web.controller;

import java.util.Calendar;
import java.util.Date;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import fr.fluxx.core.domain.Feed;
import fr.fluxx.core.service.CustomFeedService;
import fr.fluxx.core.service.FeedService;
import fr.fluxx.core.service.ItemService;

@Named(value = "statisticView")
@ApplicationScoped
public class StatisticBean {

	@EJB
	FeedService feedService;

	@EJB
	ItemService itemService;

	@EJB
	CustomFeedService customFeedService;

	public long getNumFeeds() {
		return feedService.getNumFeeds();
	}

	public long getNumItems() {
		return itemService.getNumItems();
	}

	public long getNumAggregatedFeeds() {
		return customFeedService.getNumCustomFeeds();
	}

	public Date getLastUpdate() {
		Feed feed = feedService.findLastUpdatedFeed();
		if (feed != null) {
			return feed.getLastUpdate();
		}
		return Calendar.getInstance().getTime();
	}

}