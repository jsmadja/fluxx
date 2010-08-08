package fr.kaddath.apps.fluxx;

import fr.kaddath.apps.fluxx.service.FeedFetcherService;
import fr.kaddath.apps.fluxx.service.FeedService;
import fr.kaddath.apps.fluxx.service.ItemService;
import fr.kaddath.apps.fluxx.service.OpmlService;
import fr.kaddath.apps.fluxx.service.RssService;
import fr.kaddath.apps.fluxx.service.UserService;
import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import org.junit.Test;

public class AbstractTest {

    protected static EJBContainer container;
    protected static Context namingContext;

    protected static FeedService feedService;
    protected static UserService userService;
    protected static RssService rssService;
    protected static ItemService itemService;
    protected static OpmlService opmlService;
    protected static FeedFetcherService feedFetcherService;

    @Test
    public void test() {}

    static {
        try {
            container = EJBContainer.createEJBContainer();
            namingContext = container.getContext();
            feedService = (FeedService) namingContext.lookup("java:global/classes/FeedService");
            userService = (UserService) namingContext.lookup("java:global/classes/UserService");
            rssService = (RssService) namingContext.lookup("java:global/classes/RssService");
            itemService = (ItemService) namingContext.lookup("java:global/classes/ItemService");
            opmlService = (OpmlService) namingContext.lookup("java:global/classes/OpmlService");
            feedFetcherService = (FeedFetcherService) namingContext.lookup("java:global/classes/FeedFetcherService");

            rssService.setFeedEncoding("UTF-8");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}