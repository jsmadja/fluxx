package fr.kaddath.apps.fluxx.webservice;

import javax.ejb.EJB;
import javax.jws.WebMethod;
import javax.jws.WebService;

import fr.kaddath.apps.fluxx.exception.DownloadFeedException;
import fr.kaddath.apps.fluxx.service.FeedFetcherService;

@WebService
public class Fluxx {

    @EJB
    private FeedFetcherService feedFetcherService;

    @WebMethod
    public void addFeed(String feedUrl) throws DownloadFeedException {
        feedFetcherService.add(feedUrl);
    }

}