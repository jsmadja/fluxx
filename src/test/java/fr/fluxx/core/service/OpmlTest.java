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

package fr.fluxx.core.service;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import fr.fluxx.core.domain.Feed;
import fr.fluxx.core.domain.Opml;

public class OpmlTest {

	@Test
	public void should_create_a_valid_opml() {
		List<Feed> feeds = new ArrayList<Feed>();
		Feed feed = new Feed();
		feed.setTitle("titre");
		feed.setUrl("http://www.google.fr");
		feeds.add(feed);
		String xmlOpml = new Opml(feeds).build();
		assertTrue(StringUtils.isNotEmpty(xmlOpml));
	}

}