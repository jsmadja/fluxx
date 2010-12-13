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

package fr.fluxx.admin.controller;

import java.io.Serializable;
import java.util.Date;

import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.inject.Inject;
import javax.inject.Named;

import fr.fluxx.admin.model.CollectionDataModel;
import fr.fluxx.core.domain.Feed;
import fr.fluxx.core.domain.Item;
import fr.fluxx.core.service.FeedFetcherService;
import fr.fluxx.core.service.FeedService;
import fr.fluxx.core.service.ItemService;

@Named(value = "editFeed")
@ManagedBean
@SessionScoped
public class EditFeedBean implements Serializable {

	private static final long serialVersionUID = -5993382815103965143L;

	@Inject
	private FeedService feedService;

	@Inject
	private FeedFetcherService feedFetcherService;

	@Inject
	private ItemService itemService;

	private Feed currentFeed;

	private Long id;
	private String url;
	private String author;
	private String description;
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
		author = currentFeed.getAuthor();
		description = currentFeed.getDescription();
		feedType = currentFeed.getFeedType();
		inError = currentFeed.getInError();
		title = currentFeed.getTitle();
		lastUpdate = currentFeed.getLastUpdate();
		publishedDate = currentFeed.getPublishedDate();
	}

	@SuppressWarnings("unchecked")
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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