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

package fr.fluxx.core.domain;

import fr.fluxx.core.resource.FluxxMessage;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.util.List;

public class Opml {

    private final List<Feed> feeds;
    private final Element root = new Element("opml");
    private final XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());

    public Opml(List<Feed> feeds) {
        this.feeds = feeds;
    }

    public String build() {
        insertRoot();
        insertHead();
        insertBody();
        return generateXmlString();
    }

    private void insertRoot() {
        root.setAttribute("version", "1.0");
    }

    private void insertHead() {
        Element head = new Element("head");
        Element title = new Element("title");
        title.setText(FluxxMessage.m("opml_title"));
        head.addContent(title);
        root.addContent(head);
    }

    private String generateXmlString() {
        Document document = new Document(root);
        return outputter.outputString(document);
    }

    private void insertBody() {
        Element body = new Element("body");
        root.addContent(body);
        for (Feed feed : feeds) {
            insertOutline(feed, body);
        }
    }

    private void insertOutline(Feed feed, Element body) {
        Element outline = new Element("outline");
        String title = feed.getTitle();
        outline.setAttribute("text", title);
        outline.setAttribute("title", title);
        outline.setAttribute("type", "rss");
        outline.setAttribute("xmlUrl", feed.getUrl());
        outline.setAttribute("htmlUrl", feed.getUrl());
        body.addContent(outline);
    }
}
