package fr.kaddath.apps.fluxx.service;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class Services {

    private static UserService userService;
    private static FeedService feedService;
    private static ItemService itemService;

    public static UserService getUserService() {
        if (userService == null) {
            userService = (UserService) lookup(UserService.class.getSimpleName());
        }
        return userService;
    }

    public static FeedService getFeedService() {
        if (feedService == null) {
            feedService = (FeedService) lookup(FeedService.class.getSimpleName());
        }
        return feedService;
    }
    
    public static ItemService getItemService() {
        if (itemService == null) {
            itemService = (ItemService) lookup(ItemService.class.getSimpleName());
        }
        return itemService;
    }

    private static Object lookup(String service) {
        try {
            return new InitialContext().lookup("java:global/fluxx/" + service);
        } catch (NamingException ex) {
            Logger.getLogger(Services.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }
}
