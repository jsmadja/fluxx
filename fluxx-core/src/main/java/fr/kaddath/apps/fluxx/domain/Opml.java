package fr.kaddath.apps.fluxx.domain;

import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import fr.kaddath.apps.fluxx.resource.FluxxMessage;

public class Opml {

	private final List<Feed> feeds;
	private Document document;
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
		document = new Document(root);
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
