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

@SuppressWarnings("unchecked")
@Named(value = "editCustomFeed")
@ManagedBean
@SessionScoped
public class EditCustomFeedBean implements Serializable {

	private static final long serialVersionUID = -5930629180984532531L;

	private static final FeedsComparator FEEDS_COMPARATOR = new FeedsComparator();

	@Inject
	private FeedService feedService;

	@Inject
	private FeedFetcherService feedFetcherService;

	@Inject
	private CustomFeedService customFeedService;

	private String filter = "";
	private Long id;
	private String newFeedUrl;
	private String username;
	private String category;
	private int numLastDay = 3;
	private transient CollectionDataModel feedsDataModel;
	private transient CollectionDataModel availableFeedsDataModel;
	private List<Feed> availableListFeeds;
	private CustomFeed currentCustomFeed;

	private static final Logger LOG = Logger.getLogger(EditCustomFeedBean.class.getName());

	public String edit() {
		currentCustomFeed = customFeedService.findByUsernameAndName(username, category);
		filter = currentCustomFeed.getCategory();
		reload();
		return "custom-feed/edit";
	}

	public String addFeed() {
		Feed feed = getAvailableFeeds().getRowData();
		return addFeed(feed);
	}

	private String addFeed(Feed feed) {
		currentCustomFeed = customFeedService.addFeed(currentCustomFeed, feed);
		return "custom-feed/edit";
	}

	public String removeFeed() {
		currentCustomFeed.getFeeds().remove(currentCustomFeed);
		currentCustomFeed = customFeedService.update(currentCustomFeed);
		reload();
		return "custom-feed/edit";
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
		currentCustomFeed.setUsername(username);
		currentCustomFeed.setCategory(category);
		currentCustomFeed.setNumLastDay(numLastDay);
		currentCustomFeed = customFeedService.update(currentCustomFeed);
		return "custom-feed/edit";
	}

	public String filter() {
		LOG.info("filter feeds with : " + filter);
		buildAvailableFeedsDataModel(filter);
		return "custom-feed/edit";
	}

	private void reload() {
		buildInformations();
		buildAvailableFeedsDataModel();
		buildFeedsDataModel();
	}

	private void buildInformations() {
		this.id = currentCustomFeed.getId();
		this.numLastDay = currentCustomFeed.getNumLastDay();
		this.username = currentCustomFeed.getUsername();
		this.category = currentCustomFeed.getCategory();
	}

	public DataModel<Feed> getFeeds() {
		if (feedsDataModel == null) {
			buildFeedsDataModel();
		}
		return feedsDataModel.getDataModel();
	}

	private void buildFeedsDataModel() {
		this.feedsDataModel = new CollectionDataModel(currentCustomFeed.getFeeds());
	}

	public DataModel<Feed> getAvailableFeeds() {
		if (availableFeedsDataModel == null) {
			buildAvailableFeedsDataModel();
		}
		return availableFeedsDataModel.getDataModel();
	}

	private void buildAvailableFeedsDataModel() {
		buildAvailableFeedsDataModel(filter);
	}

	private void buildAvailableFeedsDataModel(String filter) {
		List<Feed> feeds = feedService.findAvailableFeedsByCustomFeedWithFilter(currentCustomFeed, filter);
		Collections.sort(feeds, FEEDS_COMPARATOR);
		this.availableFeedsDataModel = new CollectionDataModel(feeds);
	}

	public Long getId() {
		return id;
	}

	public String getUrl() {
		return customFeedService.createUrl((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext()
				.getRequest(), currentCustomFeed);
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

	public void setCategory(String category) {
		this.category = category;
	}

	public String getCategory() {
		return category;
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
