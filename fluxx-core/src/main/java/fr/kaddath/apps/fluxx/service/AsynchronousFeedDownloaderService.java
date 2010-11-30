package fr.kaddath.apps.fluxx.service;

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;

import fr.kaddath.apps.fluxx.domain.Feed;
import fr.kaddath.apps.fluxx.exception.DownloadFeedException;
import fr.kaddath.apps.fluxx.interceptor.ChronoInterceptor;

@Stateless
public class AsynchronousFeedDownloaderService {

	private static final SyndFeedInput syndFeedInput = new SyndFeedInput();

	public Object[] downloadFeedContent(Feed feed) throws DownloadFeedException {
		try {
			String content = downloadContent(feed.getUrl());
			SyndFeed syndFeed = createSyndFeed(content);
			int size = computeSize(content);
			feed.setSize(size);

			return new Object[] { feed, syndFeed };
		} catch (Exception ex) {
			throw new DownloadFeedException(ex.getMessage());
		}
	}

	@Interceptors({ ChronoInterceptor.class })
	private SyndFeed createSyndFeed(String text) throws IllegalArgumentException, FeedException {
		return syndFeedInput.build(new StringReader(text));
	}

	private String downloadContent(String url) throws MalformedURLException, IOException {
		Scanner sc = new Scanner(new URL(url).openConnection().getInputStream());
		StringBuilder builder = new StringBuilder();
		while (sc.hasNextLine()) {
			builder.append(sc.nextLine());
		}
		return builder.toString();
	}

	private int computeSize(String text) throws IOException {
		return text.getBytes().length;
	}
}
