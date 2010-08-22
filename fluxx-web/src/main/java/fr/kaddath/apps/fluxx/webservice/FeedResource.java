package fr.kaddath.apps.fluxx.webservice;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import fr.kaddath.apps.fluxx.domain.AggregatedFeed;
import fr.kaddath.apps.fluxx.domain.Fluxxer;
import fr.kaddath.apps.fluxx.service.Services;

@Path("rss/{username}/{aggregatedfeed}")
public class FeedResource {
    @Context
    private HttpServletRequest request;

    //@Resource(lookup = "fluxx/feed/encoding")
    private String feedEncoding = "UTF-8";

    @GET
    @Produces("application/xml")
    public String getXml(@PathParam(value="username") String username, @PathParam(value="aggregatedfeed") String aggregatedFeed) {
        Fluxxer fluxxer = Services.getUserService().findByUsername(username);
        if (fluxxer != null) {
            List<AggregatedFeed> aggregatedFeeds = fluxxer.getAggregatedFeeds();
            for (AggregatedFeed feed:aggregatedFeeds) {
                if (feed.getName().equals(aggregatedFeed)) {
                    return Services.getRssService().getRssFeedById(feed.getAggregatedFeedId(), request, feedEncoding);
                }
            }
        }
        return "";
    }
}
