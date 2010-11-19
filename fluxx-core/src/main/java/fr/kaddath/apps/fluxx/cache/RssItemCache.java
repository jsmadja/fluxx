package fr.kaddath.apps.fluxx.cache;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import com.sun.syndication.feed.synd.SyndEntryImpl;

@Singleton
public class RssItemCache {

	Logger logger = Logger.getLogger(RssItemCache.class.getName());

	private final String cacheName = "rss_item_cache";
	private CacheManager cacheManager;
	private Cache cache;

	@PostConstruct
	public void initializeCache() {
		cacheManager = CacheManager.create();
		cache = cacheManager.getCache(cacheName);
		logger.log(Level.INFO, "{0} has been initialized", cacheName);
	}

	public boolean contains(String itemLink) {
		return cache.isKeyInCache(itemLink);
	}

	public void put(SyndEntryImpl entry) {
		String link = entry.getLink();
		cache.put(new Element(link, entry));
		logger.log(Level.INFO, "put {0} item link in cache", link);
	}
}
