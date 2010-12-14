package fr.fluxx.admin;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import fr.fluxx.core.service.FeedFetcherService;

public class Services {

	private static FeedFetcherService feedFetcherService;

	private static Object lookup(String service) {
        try {
            return new InitialContext().lookup("java:global/fluxx-admin/" + service);
        } catch (NamingException e) {
            try {
                return new InitialContext().lookup("java:global/classes/" + service);
            } catch (NamingException ex) {
                Logger.getLogger(Services.class.getName()).log(Level.SEVERE, null, ex);
                throw new RuntimeException(ex);
            }
        }
    }
	
	public static FeedFetcherService getFeedFetcherService() {
		if (feedFetcherService == null) {
			feedFetcherService = (FeedFetcherService) lookup(FeedFetcherService.class.getSimpleName());
        }
        return feedFetcherService;
	}

}
