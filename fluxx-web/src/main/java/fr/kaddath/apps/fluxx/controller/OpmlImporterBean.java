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

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import fr.kaddath.apps.fluxx.domain.Feed;
import fr.kaddath.apps.fluxx.exception.DownloadFeedException;
import fr.kaddath.apps.fluxx.service.FeedFetcherService;
import fr.kaddath.apps.fluxx.service.FeedService;
import fr.kaddath.apps.fluxx.service.OpmlService;

@Named(value = "opmlImporter")
@ApplicationScoped
public class OpmlImporterBean {

	private String content;
	@Inject
	private OpmlService opmlService;
	@Inject
	private FeedService feedService;
	@Inject
	private FeedFetcherService feedFetcherService;

	private static final Logger LOG = Logger.getLogger(OpmlImporterBean.class.getName());

	public String getImportOpml() {
		List<Feed> feeds = opmlService.loadOpml(getContent().getBytes());
		for (Feed feed : feeds) {
			if (null == feedService.findFeedByUrl(feed.getUrl())) {
				try {
					feedFetcherService.addNewFeed(feed.getUrl());
				} catch (DownloadFeedException ex) {
					java.util.logging.Logger.getLogger(OpmlImporterBean.class.getName()).log(Level.SEVERE, null, ex);
				}
			} else {
				LOG.info(feed.getUrl() + " already exists in database");
			}
		}
		return feeds.size() + " feeds found!";
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
