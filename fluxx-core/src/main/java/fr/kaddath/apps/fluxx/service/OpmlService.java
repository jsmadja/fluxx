package fr.kaddath.apps.fluxx.service;

import fr.kaddath.apps.fluxx.domain.Feed;
import fr.kaddath.apps.fluxx.domain.Opml;
import fr.kaddath.apps.fluxx.interceptor.ChronoInterceptor;

import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Stateless
@Interceptors({ChronoInterceptor.class})
public class OpmlService {

    public List<Feed> loadOpml(String opmlFile) throws FileNotFoundException {
        File file = new File(opmlFile);
        Scanner sc = new Scanner(file);
        return loadOpml(sc);
    }

    public List<Feed> loadOpml(byte[] bytes) {
        Scanner sc = new Scanner(new String(bytes));
        return loadOpml(sc);
    }

    private List<Feed> loadOpml(Scanner sc) {
        List<Feed> feeds = new ArrayList<Feed>();
        List<String> listXmlUrl = new ArrayList<String>();
        List<String> listText = new ArrayList<String>();
        while (sc.hasNextLine()) {
            String line = sc.nextLine();

            if (line.contains("xmlUrl")) {
                String url = line.split("xmlUrl=\"")[1].split("\"")[0];
                url = url.replaceAll("&amp;", "&");

                listXmlUrl.add(url);
            }

            if (line.contains("text")) {
                listText.add(line.split("text=\"")[1].split("\"")[0]);
            }
        }

        for (int i = 0; i < listXmlUrl.size(); i++) {
            Feed feed = new Feed();
            feed.setTitle(listText.get(i));
            feed.setUrl(listXmlUrl.get(i));
            feed.setInError(false);
            feed.setComplete(false);
            feeds.add(feed);
        }
        return feeds;
    }

    public String createOpml(List<Feed> feeds) {

        return new Opml(feeds).build();
    }
}
