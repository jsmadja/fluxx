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

package fr.fluxx.web.service;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import fr.fluxx.core.service.FeedService;
import fr.fluxx.core.service.ItemService;
import fr.fluxx.core.service.RssService;

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
		} catch (NamingException e) {
			try {
				return new InitialContext().lookup("java:global/fluxx/" + service);
			} catch (NamingException ex) {
				Logger.getLogger(Services.class.getName()).log(Level.SEVERE, null, ex);
			}
			throw new RuntimeException(e);
		}
	}
}