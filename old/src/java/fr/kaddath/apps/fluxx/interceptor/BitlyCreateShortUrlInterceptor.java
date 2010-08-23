package fr.kaddath.apps.fluxx.interceptor;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

public class BitlyCreateShortUrlInterceptor {

    private static final Logger LOG = Logger.getLogger(BitlyCreateShortUrlInterceptor.class.getName());

    @AroundInvoke
    public Object intercept(InvocationContext ctx) throws java.lang.Exception {
        String longUrl = (String) ctx.getParameters()[0];
        LOG.log(Level.INFO, "Try to shorten {0}", new Object[]{longUrl});
        String shortUrl = (String) ctx.proceed();
        return shortUrl;
    }
}
