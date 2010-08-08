package fr.kaddath.apps.fluxx;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import fr.kaddath.apps.fluxx.cache.RssFeedCache;
import fr.kaddath.apps.fluxx.domain.Fluxxer;
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
import javax.persistence.EntityManager;
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

    protected static EntityManager em;

    @Test
    public void test() {}

    private static Object lookup(String key) throws NamingException {
        return namingContext.lookup("java:global/classes/" + key);
    }

    static {
        try {
            container = EJBContainer.createEJBContainer();
            namingContext = container.getContext();
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

            cache = (RssFeedCache) lookup("RssFeedCache");

            EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("fluxxPU");
            em = emFactory.createEntityManager();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    protected Fluxxer giveMeOneFluxxer() {
        Fluxxer user = userService.findAll().get(0);
        return user;
    }
}