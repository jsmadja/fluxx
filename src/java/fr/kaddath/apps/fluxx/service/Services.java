package fr.kaddath.apps.fluxx.service;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class Services {

    public static UserService userService;
    public static FeedService feedService;
    public static ItemService itemService;

    static {
        try {
            InitialContext context = new InitialContext();
            userService = (UserService) context.lookup("java:global/fluxx/UserService");
            feedService = (FeedService) context.lookup("java:global/fluxx/FeedService");
            itemService = (ItemService) context.lookup("java:global/fluxx/ItemService");
        } catch (NamingException ex) {
            Logger.getLogger(Services.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }        
    }
}
