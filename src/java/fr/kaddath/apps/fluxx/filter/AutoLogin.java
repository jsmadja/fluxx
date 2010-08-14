package fr.kaddath.apps.fluxx.filter;

import fr.kaddath.apps.fluxx.cache.AutoLoginService;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

public class AutoLogin implements Filter {

    private static final Logger log = Logger.getLogger(AutoLogin.class.getName());
    private FilterConfig filterConfig;

    private static AutoLoginService autoLoginService = new AutoLoginService();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) {

        HttpServletRequest realrequest = (HttpServletRequest) request;

        autoLoginService.autoLog("julien", "julien", "http://fluxx.fr.cr:8080/fluxx/j_security_check");

        HttpServletResponse realresponse = (HttpServletResponse) response;
        try {
            realresponse.sendRedirect(realrequest.getContextPath());
        } catch (IOException ex) {
            Logger.getLogger(AutoLogin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public FilterConfig getFilterConfig() {
        return this.filterConfig;
    }

    public void setFilterConfig(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }

    @Override
    public void destroy() {
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
    }
}
