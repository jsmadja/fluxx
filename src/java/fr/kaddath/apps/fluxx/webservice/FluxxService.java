package fr.kaddath.apps.fluxx.webservice;

import fr.kaddath.apps.fluxx.exception.DownloadFeedException;
import fr.kaddath.apps.fluxx.service.FeedFetcherService;
import javax.ejb.EJB;
import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService
public class FluxxService {

    @EJB
    private FeedFetcherService feedFetcherService;

    @WebMethod
    public void addFeed(String feedUrl) throws DownloadFeedException {
        feedFetcherService.add(feedUrl);
    }

}
