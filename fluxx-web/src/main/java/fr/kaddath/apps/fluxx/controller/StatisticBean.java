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

package fr.kaddath.apps.fluxx.controller;

import java.util.Calendar;
import java.util.Date;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import fr.kaddath.apps.fluxx.domain.Feed;
import fr.kaddath.apps.fluxx.service.CustomFeedService;
import fr.kaddath.apps.fluxx.service.FeedService;
import fr.kaddath.apps.fluxx.service.ItemService;

@Named(value = "statisticView")
@ApplicationScoped
public class StatisticBean {

	@Inject
	FeedService feedService;

	@Inject
	ItemService itemService;

	@Inject
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