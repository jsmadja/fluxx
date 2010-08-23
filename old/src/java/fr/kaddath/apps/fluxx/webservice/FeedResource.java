package fr.kaddath.apps.fluxx.webservice;

import fr.kaddath.apps.fluxx.domain.AggregatedFeed;
import fr.kaddath.apps.fluxx.domain.Fluxxer;
import fr.kaddath.apps.fluxx.service.RssService;
import fr.kaddath.apps.fluxx.service.Services;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.PathParam;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;

@Path("rss/{username}/{aggregatedfeed}")
public class FeedResource {
    @Context
    private HttpServletRequest request;

    @GET
    @Produces("application/xml")
    public String getXml(@PathParam(value="username") String username, @PathParam(value="aggregatedfeed") String aggregatedFeed) {
        Fluxxer fluxxer = Services.getUserService().findByUsername(username);
        if (fluxxer != null) {
            List<AggregatedFeed> aggregatedFeeds = fluxxer.getAggregatedFeeds();
            for (AggregatedFeed feed:aggregatedFeeds) {
                if (feed.getName().equals(aggregatedFeed)) {
                    return Services.getRssService().getRssFeedById(feed.getAggregatedFeedId(), request);
                }
            }
        }
        return "";
    }
}
