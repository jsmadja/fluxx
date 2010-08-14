package fr.kaddath.apps.fluxx.service;

import com.rosaloves.bitlyj.Bitly;
import com.rosaloves.bitlyj.Bitly.Provider;
import com.rosaloves.bitlyj.BitlyException;
import com.rosaloves.bitlyj.BitlyMethod;
import com.rosaloves.bitlyj.ShortenedUrl;
import com.rosaloves.bitlyj.Url;
import fr.kaddath.apps.fluxx.interceptor.BitlyCreateShortUrlInterceptor;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

/**
 * http://code.google.com/p/bitlyj/
 */
@Stateless
public class BitlyService {

    @Resource(lookup="fluxx/bitly/account")
    private String userAccount;

    @Resource(lookup="fluxx/bitly/api/key")
    private String apiKey;

    private Provider provider;

    @PostConstruct
    public void init() {
        provider = Bitly.as(userAccount, apiKey);
    }

    @Interceptors({BitlyCreateShortUrlInterceptor.class})
    public String createShortUrl(String longUrl) throws Exception {        
    try {
            BitlyMethod<ShortenedUrl> bitlyMethod = Bitly.shorten(longUrl);
            Url url = provider.call(bitlyMethod);
            String shortUrl = url.getShortUrl();
            return shortUrl;
        } catch (BitlyException e) {
            throw new Exception(e);
        }
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }
}
