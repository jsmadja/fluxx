package fr.kaddath.apps.fluxx.controller;

import java.io.Serializable;
import java.util.Date;

import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.inject.Inject;
import javax.inject.Named;

import fr.kaddath.apps.fluxx.domain.Feed;
import fr.kaddath.apps.fluxx.domain.Item;
import fr.kaddath.apps.fluxx.model.CollectionDataModel;
import fr.kaddath.apps.fluxx.service.FeedFetcherService;
import fr.kaddath.apps.fluxx.service.FeedService;
import fr.kaddath.apps.fluxx.service.ItemService;

@Named(value = "editFeed")
@ManagedBean
@SessionScoped
public class EditFeedBean implements Serializable {

	@Inject
	private FeedService feedService;

	@Inject
	private FeedFetcherService feedFetcherService;

	@Inject
	private ItemService itemService;

	private Feed currentFeed;

	private Long id;
	private String url;
	private Boolean complete;
	private String author;
	private String description;
	private String encoding;
	private String feedType;
	private Boolean inError;
	private String title;
	private Date lastUpdate;
	private Date publishedDate;

	private CollectionDataModel itemsDataModel;

	private static final String EDIT_FEED = "edit-feed";

	public String edit() {
		// currentFeed = manageFeedBean.getFeeds().getRowData();
		buildInformations(currentFeed);
		return EDIT_FEED;
	}

	public String update() {
		Feed feed = feedService.findFeedById(id);
		feed.setUrl(url);
		feedFetcherService.updateExistingFeed(feed);
		currentFeed = (Feed) FacesContext.getCurrentInstance().getAttributes().put("feed", feed);
		return EDIT_FEED;
	}

	public void buildInformations(Feed currentFeed) {
		id = currentFeed.getId();
		url = currentFeed.getUrl();
		complete = currentFeed.getComplete();
		author = currentFeed.getAuthor();
		description = currentFeed.getDescription();
		encoding = currentFeed.getEncoding();
		feedType = currentFeed.getFeedType();
		inError = currentFeed.getInError();
		title = currentFeed.getTitle();
		lastUpdate = currentFeed.getLastUpdate();
		publishedDate = currentFeed.getPublishedDate();
	}

	public DataModel<Item> getItems() {
		if (itemsDataModel == null) {
			buildItemsDataModel();
		}
		return itemsDataModel.getDataModel();
	}

	private void buildItemsDataModel() {
		this.itemsDataModel = new CollectionDataModel(itemService.findItemsByFeed(currentFeed));
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public Boolean getComplete() {
		return complete;
	}

	public void setComplete(Boolean complete) {
		this.complete = complete;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getFeedType() {
		return feedType;
	}

	public void setFeedType(String feedType) {
		this.feedType = feedType;
	}

	public Boolean getInError() {
		return inError;
	}

	public void setInError(Boolean inError) {
		this.inError = inError;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public Date getPublishedDate() {
		return publishedDate;
	}

	public void setPublishedDate(Date publishedDate) {
		this.publishedDate = publishedDate;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
