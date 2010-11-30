package fr.kaddath.apps.fluxx.service;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;

public class Services {

	private static FeedService feedService;
	private static ItemService itemService;
	private static RssService rssService;

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

	public static RssService getRssService() {
		if (rssService == null) {
			rssService = (RssService) lookup(RssService.class.getSimpleName());
		}
		return rssService;
	}

	private static Object lookup(String service) {
		try {
			return new InitialContext().lookup("java:global/fluxx-web/" + service);
		} catch (NamingException ex) {
			Logger.getLogger(Services.class.getName()).log(Level.SEVERE, null, ex);
			throw new RuntimeException(ex);
		}
	}
}
