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

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

@Singleton
public class CustomFeedCache {

	private final static String CACHE_NAME = "custom_feed_cache";
	private Cache cache;

	@PostConstruct
	public void initializeCache() {
		CacheManager cacheManager = CacheManager.create();
		cache = cacheManager.getCache(CACHE_NAME);
	}

	public void put(Long key, String value) {
		cache.put(new Element(key, value));
	}

	public void clear() {
		cache.removeAll();
	}

	public void remove(Long key) {
		cache.remove(key);
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
