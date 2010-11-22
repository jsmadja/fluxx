package fr.kaddath.apps.fluxx.webservice;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

import javax.ejb.EJB;
import javax.jws.WebMethod;
import javax.jws.WebService;

import fr.kaddath.apps.fluxx.exception.DownloadFeedException;
import fr.kaddath.apps.fluxx.service.FeedFetcherService;

@WebService
public class Crawler {

	@EJB
	FeedFetcherService feedFetcherService;

	private static final int MAX_URLS_TO_CRAWL = 100;

	private static final Set<String> rss = new TreeSet<String>();

	private static final Set<String> crawledUrls = new TreeSet<String>();

	private static final LinkedList<String> urlsToCrawl = new LinkedList<String>();

	@WebMethod
	public void crawlFrom(String rootUrl) {
		urlsToCrawl.add(rootUrl);

		while (!urlsToCrawl.isEmpty()) {
			String url = urlsToCrawl.pop();
			if (!crawledUrls.contains(url)) {
				crawl(url);
			}
			crawledUrls.add(url);

			System.err.println(urlsToCrawl.size() + " urls to crawl, " + crawledUrls.size() + " already crawled, "
					+ rss.size() + " rss found");
		}
	}

	private void crawl(String url) {
		try {
			String html = download(url);
			addRss(html);
			addUrlsToCrawl(html, url);
		} catch (MalformedURLException e) {
		} catch (IOException e) {
			String msg = e.getMessage();
			if (!msg.startsWith("Server returned HTTP response code: 403")) {
				e.printStackTrace();
			}
		} catch (NullPointerException e) {
			System.err.println("NPE : " + url);
		}
	}

	private void addRss(String html) {
		String rssFeed = extractRss(html);
		if (rssFeed != null && !rss.contains(rssFeed)) {
			System.err.println("found rss : " + rssFeed);
			rss.add(rssFeed);
			try {
				feedFetcherService.add(rssFeed);
			} catch (DownloadFeedException e) {
				e.printStackTrace();
			}
		}
	}

	private void addUrlsToCrawl(String html, String siteUrl) {
		List<String> urls = extractUrls(html, siteUrl);
		for (String url : urls) {
			if (!crawledUrls.contains(url) && !url.startsWith(siteUrl) && urlsToCrawl.size() < MAX_URLS_TO_CRAWL) {
				urlsToCrawl.addAll(urls.subList(0,
						urls.size() > MAX_URLS_TO_CRAWL ? MAX_URLS_TO_CRAWL - urlsToCrawl.size() : urls.size()));
			}
		}
	}

	/**
	 * <link rel="alternate" title="PC INpact News RSS" href="http://www.pcinpact.com/include/news.xml"
	 * type="application/rss+xml" />
	 * 
	 * @param html
	 * @return
	 */
	private String extractRss(String html) {
		try {
			if (html.contains("rss+xml")) {
				int rssXmlIdx = html.indexOf("rss+xml");
				int closeMarkup = html.indexOf(">", rssXmlIdx) + 1;
				int beginMarkup = new StringBuilder(html.substring(0, closeMarkup)).reverse().toString().indexOf("<");
				beginMarkup = closeMarkup - beginMarkup - 1;
				String linkMarkup = html.substring(beginMarkup, closeMarkup);
				String href = linkMarkup.split("href=")[1].split("\"")[1];
				return href.trim();
			}
		} catch (Throwable t) {
			System.err.println("extractRss(" + html.trim() + ")");
		}
		return null;
	}

	private List<String> extractUrls(String html, String siteUrl) {
		List<String> list = new ArrayList<String>();
		Scanner sc = new Scanner(html);
		while (sc.hasNextLine()) {
			String line = sc.nextLine();
			if (line.contains("href")) {
				String href = extractHref(line);
				if (href != null && href.length() > 1 && !href.startsWith(siteUrl)) {
					// System.err.println("add link : " + href);
					list.add(href);
				}
			}
		}
		return list;
	}

	private String extractHref(String html) {
		try {
			// System.err.println("extractHref(" + html + ")");
			int hrefdx = html.indexOf("href");
			int closeMarkup = html.indexOf(">", hrefdx) + 1;
			int beginMarkup = new StringBuilder(html.substring(0, closeMarkup)).reverse().toString().indexOf("<");
			beginMarkup = closeMarkup - beginMarkup - 1;
			String aMarkup = html.substring(beginMarkup, closeMarkup);
			// System.err.println("aMarkup : " + aMarkup);
			if (aMarkup.trim().length() > 0) {
				aMarkup = aMarkup.replaceAll("'", "\"");
				aMarkup = aMarkup.replaceAll(" = ", "=");

				String href = aMarkup.split("href=")[1].split("\"")[1];
				return href.trim();
			}
		} catch (Throwable t) {
			System.err.println("extractHref(" + html.trim() + ")");
		}
		return null;
	}

	private String download(String strUrl) throws IOException {
		System.err.println("crawl " + strUrl);
		URL url = new URL(strUrl);
		URLConnection connection = url.openConnection();
		Scanner sc = new Scanner(connection.getInputStream());
		StringBuilder builder = new StringBuilder();
		while (sc.hasNextLine()) {
			builder.append(sc.nextLine());
			builder.append("\n");
		}
		return builder.toString();
	}
}
