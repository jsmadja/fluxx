package fr.kaddath.apps.fluxx.service;

import fr.kaddath.apps.fluxx.Services;
import junit.framework.TestCase;
import org.junit.Test;

public class TwitterServiceTest {

    private String url = "http://google.fr";

    @Test
    public void updateStatus() throws Exception {
        Services.twitterService.updateStatus("tweet");
    }

    @Test
    public void createTweet() throws Exception {
        String title = "Very long message that we have to shorten as fast as we can";
        String tweet = Services.twitterService.createPrivateTweetWithUrl(title, url);
        TestCase.assertTrue(tweet.length() < TwitterService.TWEET_MAX_LENGTH);
    }

    @Test
    public void tweetWithAccents() throws Exception {
        String title = "Les numériques joue avec le portable GT660 signé MSI";
        Services.twitterService.updateStatus(title);
    }
}