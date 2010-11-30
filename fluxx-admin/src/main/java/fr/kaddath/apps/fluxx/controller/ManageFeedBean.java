package fr.kaddath.apps.fluxx.controller;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.faces.model.DataModel;
import javax.inject.Inject;
import javax.inject.Named;

import fr.kaddath.apps.fluxx.comparator.FeedsComparator;
import fr.kaddath.apps.fluxx.domain.Feed;
import fr.kaddath.apps.fluxx.model.CollectionDataModel;
import fr.kaddath.apps.fluxx.service.FeedFetcherService;
import fr.kaddath.apps.fluxx.service.FeedService;

@Named(value = "manageFeedBean")
@SessionScoped
public class ManageFeedBean implements Serializable {

	private static final FeedsComparator FEEDS_COMPARATOR = new FeedsComparator();

	private static final String MANAGE_FEEDS = "manage-feeds";

	private transient DataModel<Feed> feedsDataModel;
	private transient DataModel<Feed> feedsInErrorDataModel;

	@Inject
	private FeedService feedService;
	@Inject
	private FeedFetcherService feedFetcherService;

	public String updateAll() {
		feedFetcherService.updateAll();
		reload();
		return MANAGE_FEEDS;
	}

	public DataModel<Feed> getFeeds() {
		feedsDataModel = new CollectionDataModel(getAllFeeds()).getDataModel();
		return feedsDataModel;
	}

	public DataModel<Feed> getFeedsInError() {
		if (feedsInErrorDataModel == null) {
			feedsInErrorDataModel = new CollectionDataModel(getAllFeedsInError()).getDataModel();
		}
		return feedsInErrorDataModel;
	}

	public String update() {
		Feed feed = feedsDataModel.getRowData();
		feedFetcherService.updateExistingFeed(feed);
		reload();
		return MANAGE_FEEDS;
	}

	public String delete() {
		Feed feed = feedsDataModel.getRowData();
		feedService.delete(feed);
		feedsDataModel = new CollectionDataModel(getAllFeeds()).getDataModel();
		reload();
		return MANAGE_FEEDS;
	}

	public String updateInError() {
		Feed feed = feedsInErrorDataModel.getRowData();
		feedFetcherService.updateExistingFeed(feed);
		reload();
		return MANAGE_FEEDS;
	}

	public String deleteInError() {
		Feed feed = feedsInErrorDataModel.getRowData();
		feedService.delete(feed);
		feedsDataModel = new CollectionDataModel(getAllFeeds()).getDataModel();
		return MANAGE_FEEDS;
	}

	private List<Feed> getAllFeeds() {
		List<Feed> feeds = feedService.findAllFeeds();
		Collections.sort(feeds, FEEDS_COMPARATOR);
		return feeds;
	}

	private List<Feed> getAllFeedsInError() {
		List<Feed> feeds = feedService.findAllFeedsInError();
		Collections.sort(feeds, FEEDS_COMPARATOR);
		return feeds;
	}

	private void reload() {
		feedsDataModel = new CollectionDataModel(getAllFeeds()).getDataModel();
		feedsInErrorDataModel = new CollectionDataModel(getAllFeedsInError()).getDataModel();
	}
}
