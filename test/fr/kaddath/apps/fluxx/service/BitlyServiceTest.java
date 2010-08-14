package fr.kaddath.apps.fluxx.service;

import fr.kaddath.apps.fluxx.Services;
import org.junit.Test;
import static org.junit.Assert.*;

public class BitlyServiceTest {

    @Test
    public void createShortUrl() throws Exception {
        String longUrl = "http://www.frandroid.com/4628/rom-alternative-avec-android-donut-en-beta/";
        String shortUrl = Services.bitlyService.createShortUrl(longUrl);
        assertNotNull(shortUrl);
        assertNotSame(shortUrl, longUrl);
        System.err.println(shortUrl+"["+shortUrl.length()+"]");
    }    
}