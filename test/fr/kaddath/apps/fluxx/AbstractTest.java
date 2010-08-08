package fr.kaddath.apps.fluxx;

import fr.kaddath.apps.fluxx.service.FeedFetcherService;
import fr.kaddath.apps.fluxx.service.FeedService;
import fr.kaddath.apps.fluxx.service.ItemService;
import fr.kaddath.apps.fluxx.service.OpmlService;
import fr.kaddath.apps.fluxx.service.RssService;
import fr.kaddath.apps.fluxx.service.UserService;
import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.NamingException;
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

            rssService.setFeedEncoding("UTF-8");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}