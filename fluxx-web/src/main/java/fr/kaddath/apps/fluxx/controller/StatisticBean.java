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