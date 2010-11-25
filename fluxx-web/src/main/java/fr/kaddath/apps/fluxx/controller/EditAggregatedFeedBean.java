package fr.kaddath.apps.fluxx.controller;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import fr.kaddath.apps.fluxx.comparator.FeedsComparator;
import fr.kaddath.apps.fluxx.domain.CustomFeed;
import fr.kaddath.apps.fluxx.domain.Feed;
import fr.kaddath.apps.fluxx.model.CollectionDataModel;
import fr.kaddath.apps.fluxx.service.CustomFeedService;
import fr.kaddath.apps.fluxx.service.FeedFetcherService;
import fr.kaddath.apps.fluxx.service.FeedService;

@Named(value = "editAggregatedFeed")
@ManagedBean
@SessionScoped
public class EditAggregatedFeedBean implements Serializable {

	private static final FeedsComparator FEEDS_COMPARATOR = new FeedsComparator();

	@Inject
	private FeedService feedService;

	@Inject
	private FeedFetcherService feedFetcherService;

	@Inject
	private CustomFeedService aggregatedFeedService;

	private static final String EDIT_AGGREGATEDFEED = "edit-aggregatedfeed";

	private String filter;
	private Long id;
	private String newFeedUrl;
	private String username;
	private String theme;
	private int numLastDay = 3;
	private transient CollectionDataModel feedsDataModel;
	private transient CollectionDataModel availableFeedsDataModel;
	private transient CollectionDataModel aggregatedFeedsDataModel;
	private List<Feed> availableListFeeds;
	private CustomFeed currentAggregatedFeed;

	private static final Logger LOG = Logger.getLogger(EditAggregatedFeedBean.class.getName());

	public String edit() {
		currentAggregatedFeed = aggregatedFeedService.findByUsernameAndName(username, theme);
		filter = currentAggregatedFeed.getTheme();
		reload();
		return EDIT_AGGREGATEDFEED;
	}

	public String addFeed() {
		Feed feed = getAvailableFeeds().getRowData();
		return addFeed(feed);
	}

	private String addFeed(Feed feed) {
		currentAggregatedFeed = aggregatedFeedService.addFeed(currentAggregatedFeed, feed);
		return EDIT_AGGREGATEDFEED;
	}

	public String removeFeed() {
		currentAggregatedFeed.getFeeds().remove(getAggregatedFeeds().getRowData());
		currentAggregatedFeed = aggregatedFeedService.update(currentAggregatedFeed);
		reload();
		return EDIT_AGGREGATEDFEED;
	}

	public String addNewFeed() {
		try {
			Feed newFeed = feedFetcherService.addNewFeed(newFeedUrl);
			return addFeed(newFeed);
		} catch (Exception ex) {
			return "add-feed-failure";
		}
	}

	public String update() {
		currentAggregatedFeed.setUsername(username);
		currentAggregatedFeed.setTheme(theme);
		currentAggregatedFeed.setNumLastDay(numLastDay);
		currentAggregatedFeed = aggregatedFeedService.update(currentAggregatedFeed);
		return EDIT_AGGREGATEDFEED;
	}

	public String filter() {
		LOG.info("filter feeds with : " + filter);
		buildAvailableFeedsDataModel(filter);
		return EDIT_AGGREGATEDFEED;
	}

	private void reload() {
		buildInformations();
		buildAvailableFeedsDataModel();
		buildFeedsDataModel();
		buildAggregatedFeedsDataModel();
	}

	private void buildInformations() {
		this.id = currentAggregatedFeed.getId();
		this.numLastDay = currentAggregatedFeed.getNumLastDay();
		this.username = currentAggregatedFeed.getUsername();
		this.theme = currentAggregatedFeed.getTheme();
	}

	public DataModel<Feed> getFeeds() {
		if (feedsDataModel == null) {
			buildFeedsDataModel();
		}
		return feedsDataModel.getDataModel();
	}

	private void buildFeedsDataModel() {
		this.feedsDataModel = new CollectionDataModel(currentAggregatedFeed.getFeeds());
	}

	public DataModel<Feed> getAvailableFeeds() {
		if (availableFeedsDataModel == null) {
			buildAvailableFeedsDataModel();
		}
		return availableFeedsDataModel.getDataModel();
	}

	private void buildAvailableFeedsDataModel() {
		LOG.info("filter:" + filter);
		buildAvailableFeedsDataModel(filter);
	}

	private void buildAvailableFeedsDataModel(String filter) {
		List<Feed> feeds = feedService.findAvailableFeedsByCustomFeedWithFilter(currentAggregatedFeed, filter);
		Collections.sort(feeds, FEEDS_COMPARATOR);
		this.availableFeedsDataModel = new CollectionDataModel(feeds);
	}

	public DataModel<Feed> getAggregatedFeeds() {
		if (aggregatedFeedsDataModel == null) {
			buildAggregatedFeedsDataModel();
		}
		return aggregatedFeedsDataModel.getDataModel();
	}

	private void buildAggregatedFeedsDataModel() {
		this.aggregatedFeedsDataModel = new CollectionDataModel(currentAggregatedFeed.getFeeds());
	}

	public Long getId() {
		return id;
	}

	public String getUrl() {
		return aggregatedFeedService.createUrl((HttpServletRequest) FacesContext.getCurrentInstance()
				.getExternalContext().getRequest(), currentAggregatedFeed);
	}

	public List<Feed> getAvailableListFeeds() {
		return availableListFeeds;
	}

	public String getNewFeedUrl() {
		return newFeedUrl;
	}

	public void setNewFeedUrl(String newFeedUrl) {
		this.newFeedUrl = newFeedUrl;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	public String getTheme() {
		return theme;
	}

	public int getNumLastDay() {
		return numLastDay;
	}

	public void setNumLastDay(int numLastDay) {
		this.numLastDay = numLastDay;
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

}
