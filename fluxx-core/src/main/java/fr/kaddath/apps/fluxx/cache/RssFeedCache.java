package fr.kaddath.apps.fluxx.cache;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

@Singleton
public class RssFeedCache {

	Logger logger = Logger.getLogger(RssFeedCache.class.getName());

	private final String cacheName = "rss_feed_cache";
	private CacheManager cacheManager;
	private Cache cache;

	@PostConstruct
	public void initializeCache() {
		cacheManager = CacheManager.create();
		cache = cacheManager.getCache(cacheName);
		logger.log(Level.INFO, "{0} has been initialized", cacheName);
	}

	public void put(String key, String value) {
		cache.put(new Element(key, value));
		logger.log(Level.INFO, "put {0} aggregated feed in cache", key);
	}

	public void clear() {
		cache.removeAll();
		logger.log(Level.FINEST, "{0} has been cleared", cacheName);
	}

	public void remove(String key) {
		cache.remove(key);
		logger.log(Level.INFO, "remove {0} aggregated feed from cache", key);
	}

	public String get(String key) {
		Element element = cache.get(key);
		if (element == null) {
			return null;
		}
		return (String) element.getValue();
	}

	public boolean contains(String key) {
		return get(key) != null;
	}
}
