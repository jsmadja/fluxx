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

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

@Named(value = "manageFeedBean")
@SessionScoped
public class ManageFeedBean implements Serializable {

	// private static final FeedsComparator FEEDS_COMPARATOR = new FeedsComparator();
	//
	// private static final String MANAGE_FEEDS = "manage-feeds";
	//
	// private transient DataModel<Feed> feedsDataModel;
	// private transient DataModel<Feed> feedsInErrorDataModel;
	//
	// @Inject
	// private FeedService feedService;
	// @Inject
	// private FeedFetcherService feedFetcherService;
	//
	// public String updateAll() {
	// feedFetcherService.updateAll();
	// reload();
	// return MANAGE_FEEDS;
	// }
	//
	// public DataModel<Feed> getFeeds() {
	// feedsDataModel = new CollectionDataModel(getAllFeeds()).getDataModel();
	// return feedsDataModel;
	// }
	//
	// public DataModel<Feed> getFeedsInError() {
	// if (feedsInErrorDataModel == null) {
	// feedsInErrorDataModel = new CollectionDataModel(getAllFeedsInError()).getDataModel();
	// }
	// return feedsInErrorDataModel;
	// }
	//
	// public String update() {
	// Feed feed = feedsDataModel.getRowData();
	// feedFetcherService.updateExistingFeed(feed);
	// reload();
	// return MANAGE_FEEDS;
	// }
	//
	// public String delete() {
	// Feed feed = feedsDataModel.getRowData();
	// feedService.delete(feed);
	// feedsDataModel = new CollectionDataModel(getAllFeeds()).getDataModel();
	// reload();
	// return MANAGE_FEEDS;
	// }
	//
	// public String updateInError() {
	// Feed feed = feedsInErrorDataModel.getRowData();
	// feedFetcherService.updateExistingFeed(feed);
	// reload();
	// return MANAGE_FEEDS;
	// }
	//
	// public String deleteInError() {
	// Feed feed = feedsInErrorDataModel.getRowData();
	// feedService.delete(feed);
	// feedsDataModel = new CollectionDataModel(getAllFeeds()).getDataModel();
	// return MANAGE_FEEDS;
	// }
	//
	// private List<Feed> getAllFeeds() {
	// List<Feed> feeds = feedService.findAllFeeds();
	// Collections.sort(feeds, FEEDS_COMPARATOR);
	// return feeds;
	// }
	//
	// private List<Feed> getAllFeedsInError() {
	// List<Feed> feeds = feedService.findAllFeedsInError();
	// Collections.sort(feeds, FEEDS_COMPARATOR);
	// return feeds;
	// }
	//
	// private void reload() {
	// feedsDataModel = new CollectionDataModel(getAllFeeds()).getDataModel();
	// feedsInErrorDataModel = new CollectionDataModel(getAllFeedsInError()).getDataModel();
	// }
}
