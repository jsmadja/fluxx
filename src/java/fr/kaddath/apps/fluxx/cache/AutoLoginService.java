package fr.kaddath.apps.fluxx.cache;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

public class AutoLoginService {

    private final static Logger logger = Logger.getLogger(AutoLoginService.class.getName());


    public boolean autoLog(String login, String password, String url) {

        int result = 0;

        try {
            // Get HTTP client instance
            HttpClient httpClient = new HttpClient();

            // Create HTTP GET method and execute it
            GetMethod getMethod = null;
            getMethod = new GetMethod(url);
            getMethod.setFollowRedirects(true);
            result = httpClient.executeMethod(getMethod);
            String content = getMethod.getResponseBodyAsString();
            logger.info(content);
            logger.info("result:"+result);
            getMethod.releaseConnection();

            PostMethod postMethod = null;
            postMethod = new PostMethod(url);
            postMethod.addParameter("j_username", login);
            postMethod.addParameter("j_password", password);
            result = httpClient.executeMethod(postMethod);
            logger.info("result:"+result);
            content = postMethod.getResponseBodyAsString();
            postMethod.releaseConnection();

            postMethod = new PostMethod(url);
            postMethod.addParameter("Password", password);
            result = httpClient.executeMethod(postMethod);
            content = postMethod.getResponseBodyAsString();
            logger.info("result:"+result);
            logger.info(""+content.contains(login));
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        } 
        return result == 200;
    }
}
