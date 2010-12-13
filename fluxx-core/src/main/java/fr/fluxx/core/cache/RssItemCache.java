/**
 * Copyright (C) 2010 Julien SMADJA <julien.smadja@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.fluxx.core.cache;

import com.sun.syndication.feed.synd.SyndEntryImpl;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class RssItemCache {

    Logger logger = Logger.getLogger(RssItemCache.class.getName());

    private final String cacheName = "rss_item_cache";
    private Cache cache;

    @PostConstruct
    public void initializeCache() {
        CacheManager cacheManager = CacheManager.create();
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
