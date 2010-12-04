package fr.kaddath.apps.fluxx.service;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;

public class Services {

	private static FeedFetcherService feedFetcherService;

	public static FeedFetcherService getFeedFetcherService() {
		if (feedFetcherService == null) {
			feedFetcherService = (FeedFetcherService) lookup(FeedFetcherService.class.getSimpleName());
		}
		return feedFetcherService;
	}

	private static Object lookup(String service) {
		try {
			return new InitialContext().lookup("java:global/fluxx-admin/" + service);
		} catch (NamingException e) {
			Logger.getLogger(Services.class.getName()).log(Level.SEVERE, null, e);
			throw new RuntimeException(e);
		}
	}
}
