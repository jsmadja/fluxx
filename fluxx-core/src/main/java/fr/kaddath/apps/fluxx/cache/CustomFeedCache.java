package fr.kaddath.apps.fluxx.cache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class CustomFeedCache {

    Logger logger = Logger.getLogger(CustomFeedCache.class.getName());

    private final String cacheName = "custom_feed_cache";
    private Cache cache;

    @PostConstruct
    public void initializeCache() {
        CacheManager cacheManager = CacheManager.create();
        cache = cacheManager.getCache(cacheName);
        logger.log(Level.INFO, "{0} has been initialized", cacheName);
    }

    public void put(Long key, String value) {
        cache.put(new Element(key, value));
        logger.log(Level.INFO, "put {0} custom feed in cache", key);
    }

    public void clear() {
        cache.removeAll();
        logger.log(Level.FINEST, "{0} has been cleared", cacheName);
    }

    public void remove(Long key) {
        cache.remove(key);
        logger.log(Level.INFO, "remove {0} custom feed from cache", key);
    }

    public String get(Long key) {
        Element element = cache.get(key);
        if (element == null) {
            return null;
        }
        return (String) element.getValue();
    }

    public boolean contains(Long key) {
        return get(key) != null;
    }
}
