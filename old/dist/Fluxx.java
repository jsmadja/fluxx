// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Fluxx.java

package fr.kaddath.apps.fluxx.webservice;

import fr.kaddath.apps.fluxx.exception.DownloadFeedException;
import fr.kaddath.apps.fluxx.service.FeedFetcherService;

public class Fluxx
{

    public Fluxx()
    {
    }

    public void addFeed(String feedUrl)
        throws DownloadFeedException
    {
        feedFetcherService.add(feedUrl);
    }

    private FeedFetcherService feedFetcherService;
}
