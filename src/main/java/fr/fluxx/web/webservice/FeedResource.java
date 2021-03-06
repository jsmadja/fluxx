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
package fr.fluxx.web.webservice;

import fr.fluxx.core.Services;
import fr.fluxx.core.service.RssService;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

@Path("{username}/{category}")
public class FeedResource {

    @Context
    private HttpServletRequest request;

    private final String feedEncoding = "UTF-8";

    private RssService rssService;

    @GET
    @Produces("application/xml")
    public String getXml(@PathParam(value = "username") String username, @PathParam(value = "category") String category) {
        if (rssService == null) {
            rssService = Services.getRssService();
        }
        return rssService.getRssFeedByUsernameAndCategory(username, category, request, feedEncoding);
    }

    public void setRssService(RssService rssService) {
        this.rssService = rssService;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

}
