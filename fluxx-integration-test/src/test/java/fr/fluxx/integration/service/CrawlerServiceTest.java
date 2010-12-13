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

package fr.fluxx.integration.service;

import org.junit.Ignore;
import org.junit.Test;

import fr.fluxx.integration.AbstractIntegrationTest;

@Ignore
public class CrawlerServiceTest extends AbstractIntegrationTest {

	private static final int MAX_FEEDS_TO_ADD = 10;

	@Test
	public void should_crawl() throws Exception {
		crawlerService.crawl("http://www.lemonde.fr", MAX_FEEDS_TO_ADD);
	}

}