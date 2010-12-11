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

package fr.kaddath.apps.fluxx.service;

import fr.kaddath.apps.fluxx.domain.Feed;
import fr.kaddath.apps.fluxx.domain.Opml;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class OpmlTest {

    @Test
    public void opml() {
        List<Feed> feeds = new ArrayList<Feed>();
        Feed feed = new Feed();
        feed.setTitle("titre");
        feed.setUrl("http://www.google.fr");
        feeds.add(feed);

        String xmlOpml = new Opml(feeds).build();
        System.err.println(xmlOpml);
        assertTrue(StringUtils.isNotEmpty(xmlOpml));
    }

}