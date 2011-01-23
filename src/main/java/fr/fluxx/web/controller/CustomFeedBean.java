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

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import fr.fluxx.core.domain.CustomFeed;
import fr.fluxx.core.domain.Feed;
import fr.fluxx.core.service.CustomFeedService;
import fr.fluxx.core.service.FeedFetcherService;
import fr.fluxx.web.model.CollectionDataModel;
import javax.faces.bean.SessionScoped;
import javax.faces.model.DataModel;

@ManagedBean
@SessionScoped
public class CustomFeedBean {

    @EJB
    private FeedFetcherService feedFetcherService;

    @EJB
    private CustomFeedService customFeedService;

    private String newFeedUrl;
    private String username = "utilisateur";
    private String category = "categorie";

    private CustomFeed customFeed;

    private transient CollectionDataModel feedsDataModel;

    private static final int DEFAULT_MAX_NUM_DAYS = 7;

    public String add() {
        customFeed = customFeedService.findByUsernameAndName(username, category);
        if (customFeed == null) {
            customFeed = customFeedService.addCustomFeed(username, category, DEFAULT_MAX_NUM_DAYS);
        }
        return "custom-feed/edit";
    }

    public String addNewFeed() {
        try {
            Feed feed = feedFetcherService.addNewFeed(newFeedUrl);
            customFeed = customFeedService.addFeed(customFeed, feed);
            newFeedUrl = "";
            return "custom-feed/edit";
        } catch (Exception ex) {
            return "custom-feed/error";
        }
    }

    public DataModel<Feed> getFeeds() {
        buildFeedsDataModel();
        return feedsDataModel.getDataModel();
    }

    private void buildFeedsDataModel() {
        this.feedsDataModel = new CollectionDataModel(customFeed.getFeeds());
    }

    public String getUrl() {
        return customFeedService.createUrl((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest(), customFeed);
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

    public void setCustomFeed(CustomFeed customFeed) {
        this.customFeed = customFeed;
    }

    public CustomFeed getCustomFeed() {
        return customFeed;
    }

}
