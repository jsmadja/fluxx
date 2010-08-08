package fr.kaddath.apps.fluxx;

import fr.kaddath.apps.fluxx.domain.Feed;
import fr.kaddath.apps.fluxx.cache.RssFeedCache;
import fr.kaddath.apps.fluxx.domain.Fluxxer;
import fr.kaddath.apps.fluxx.exception.DownloadFeedException;
import fr.kaddath.apps.fluxx.service.AggregatedFeedService;
import fr.kaddath.apps.fluxx.service.FeedCategoryService;
import fr.kaddath.apps.fluxx.service.FeedFetcherService;
import fr.kaddath.apps.fluxx.service.FeedService;
import fr.kaddath.apps.fluxx.service.ItemService;
import fr.kaddath.apps.fluxx.service.OpmlService;
import fr.kaddath.apps.fluxx.service.RssService;
import fr.kaddath.apps.fluxx.service.UserService;
import java.util.UUID;
import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import org.junit.Test;
import static org.mockito.Mockito.*;

public class AbstractTest {

    protected static EJBContainer container;
    protected static Context namingContext;

    protected static FeedService feedService;
    protected static UserService userService;
    protected static RssService rssService;
    protected static ItemService itemService;
    protected static OpmlService opmlService;
    protected static FeedFetcherService feedFetcherService;
    protected static RssFeedCache cache;
    protected static AggregatedFeedService aggregatedFeedService;
    protected static FeedCategoryService feedCategoryService;

    protected static String uuid = UUID.randomUUID().toString();

    protected static HttpServletRequest request;

    protected static int serverPort = 8080;
    protected static String serverName = "fluxx.fr.cr";
    protected static String contextPath = "/fluxx";

    private static Object lookup(String key) throws NamingException {
        return namingContext.lookup("java:global/classes/" + key);
    }

    static {
        try {
            container = EJBContainer.createEJBContainer();
            namingContext = container.getContext();

            cache = (RssFeedCache) lookup("RssFeedCache");

            feedService = (FeedService) lookup("FeedService");
            userService = (UserService) lookup("UserService");
            rssService = (RssService) lookup("RssService");
            itemService = (ItemService) lookup("ItemService");
            opmlService = (OpmlService) lookup("OpmlService");
            feedFetcherService = (FeedFetcherService) lookup("FeedFetcherService");
            aggregatedFeedService = (AggregatedFeedService) lookup("AggregatedFeedService");
            feedCategoryService = (FeedCategoryService) lookup("FeedCategoryService");

            rssService.setFeedEncoding("UTF-8");

            request = mock(HttpServletRequest.class);
            when(request.getServerPort()).thenReturn(serverPort);
            when(request.getServerName()).thenReturn(serverName);
            when(request.getContextPath()).thenReturn(contextPath);

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test() {}
    
    protected static Fluxxer createFluxxer() {
        Fluxxer user = new Fluxxer();
        user.setUsername(createRandomString());
        user.setPassword(createRandomString());
        user.setEmail(user.getUsername()+"@gmail.com");
        userService.persist(user);
        return user;
    }

    protected static Feed createFeed() {
        try {
            String url = "http://fluxx:8080/fluxx/rss?id=-444c6b0fbfc589b30185fa159f3dfac6";
            Feed feed = feedService.findFeedByUrl(url);
            if (feed != null) {
                feedService.delete(feed);
            }
            feed = feedFetcherService.add(url);
            return feed;
        } catch (DownloadFeedException ex) {
            throw new RuntimeException(ex);
        }
    }

    protected static String createRandomString() {
        return UUID.randomUUID().toString().substring(0, 8);
    }


    protected static Fluxxer addRandomAggregatedFeed(Fluxxer fluxxer) {
        return aggregatedFeedService.addAggregatedFeed(fluxxer, createRandomString(), 7);
    }
}