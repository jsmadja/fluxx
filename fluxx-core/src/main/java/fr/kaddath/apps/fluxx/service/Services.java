package fr.kaddath.apps.fluxx.service;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Services {

    private static FeedService feedService;
    private static ItemService itemService;
    private static RssService rssService;
    private static FeedFetcherService feedFetcherService;

    public static FeedService getFeedService() {
        if (feedService == null) {
            feedService = (FeedService) lookup(FeedService.class.getSimpleName());
        }
        return feedService;
    }

    public static FeedFetcherService getFeedFetcherService() {
        if (feedFetcherService == null) {
            feedFetcherService = (FeedFetcherService) lookup(FeedFetcherService.class.getSimpleName());
        }
        return feedFetcherService;
    }

    public static ItemService getItemService() {
        if (itemService == null) {
            itemService = (ItemService) lookup(ItemService.class.getSimpleName());
        }
        return itemService;
    }

    public static RssService getRssService() {
        if (rssService == null) {
            rssService = (RssService) lookup(RssService.class.getSimpleName());
        }
        return rssService;
    }

    private static Object lookup(String service) {
        try {
            return new InitialContext().lookup("java:global/fluxx/" + service);
        } catch (NamingException e) {
            try {
                return new InitialContext().lookup("java:global/classes/" + service);
            } catch (NamingException ex) {
                Logger.getLogger(Services.class.getName()).log(Level.SEVERE, null, ex);
                throw new RuntimeException(ex);
            }
        }
    }
}
