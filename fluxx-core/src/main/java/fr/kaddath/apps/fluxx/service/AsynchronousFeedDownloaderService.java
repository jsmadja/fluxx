package fr.kaddath.apps.fluxx.service;

import java.net.URL;

import javax.ejb.Stateless;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

import fr.kaddath.apps.fluxx.domain.Feed;
import fr.kaddath.apps.fluxx.exception.DownloadFeedException;

@Stateless
public class AsynchronousFeedDownloaderService {

	public Object[] downloadFeedContent(Feed feed) throws DownloadFeedException {
		try {
			URL feedUrl = new URL(feed.getUrl());
			SyndFeedInput syndFeedInput = new SyndFeedInput();
			SyndFeed syndFeed = syndFeedInput.build(new XmlReader(feedUrl));
			return new Object[] { feed, syndFeed };
		} catch (Exception ex) {
			throw new DownloadFeedException(ex.getMessage());
		}
	}
}
