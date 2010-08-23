package fr.kaddath.apps.fluxx.service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import net.sf.json.JSONException;
import net.unto.twitter.Api;

/**
 * http://code.google.com/p/java-twitter/
 */
@Stateless
public class TwitterService {

    @Resource(lookup="fluxx/twitter/account")
    private String usernameAccount;

    @Resource(lookup="fluxx/twitter/password")
    private String passwordAccount;

    private Api api;
    
    public final static int TWEET_MAX_LENGTH = 140;

    @EJB
    private BitlyService bitlyService;

    @PostConstruct
    public void init() {
        api = Api.builder().username(usernameAccount).password(passwordAccount).build();
    }

    public void sendDirectMessage(String username, String tweet) throws Exception {
        try {
            api.newDirectMessage(username, tweet).build().post();
        } catch (JSONException e) {
            throw new Exception(e);
        }
    }

    public void updateStatus(String tweet) throws Exception {
        try {
            api.updateStatus(tweet).build().post();
        } catch (RuntimeException e) {
            throw new Exception(e);
        }
    }

    public String createPrivateTweetWithUrl(String title, String url) throws Exception {
        String shortUrl = bitlyService.createShortUrl(url);
        final int messageMaxLength = TWEET_MAX_LENGTH - shortUrl.length() - 1;
        String tweet = title.substring(0, Math.min(title.length(), messageMaxLength));
        tweet += " "+shortUrl;
        return tweet;
    }

    public String createPublicTweetWithUrl(String title, String url, String username) throws Exception {
        String shortUrl = bitlyService.createShortUrl(url);
        String toUsername = "@"+username+" ";
        final int messageMaxLength = TWEET_MAX_LENGTH - shortUrl.length() - 1 - toUsername.length();
        String tweet = toUsername;
        tweet += title.substring(0, Math.min(title.length(), messageMaxLength));
        tweet += " "+shortUrl;
        return tweet;
    }

    public String getPasswordAccount() {
        return passwordAccount;
    }

    public void setPasswordAccount(String passwordAccount) {
        this.passwordAccount = passwordAccount;
    }

    public String getUsernameAccount() {
        return usernameAccount;
    }

    public void setUsernameAccount(String usernameAccount) {
        this.usernameAccount = usernameAccount;
    }

    public BitlyService getBitlyService() {
        return bitlyService;
    }

    public void setBitlyService(BitlyService bitlyService) {
        this.bitlyService = bitlyService;
    }
}
