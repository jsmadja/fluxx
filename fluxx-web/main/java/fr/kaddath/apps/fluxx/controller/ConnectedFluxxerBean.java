package fr.kaddath.apps.fluxx.controller;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import fr.kaddath.apps.fluxx.domain.Fluxxer;
import fr.kaddath.apps.fluxx.service.UserService;

public abstract class ConnectedFluxxerBean {
    
    public static final String USERNAME_COOKIE = "username";
    public static final String PASSWORD_COOKIE = "password";

    @Inject
    protected UserService userService;

    public Fluxxer getFluxxer() {
        FacesContext context = FacesContext.getCurrentInstance();
        String remoteUser = context.getExternalContext().getRemoteUser();

        if (remoteUser == null) {
            tryToAutoLogin(context);
        }

        Fluxxer fluxxer = userService.findByUsername(remoteUser);

        if (fluxxer != null) {
            putInCookie(fluxxer);
        }

        return fluxxer;
    }

    private void tryToAutoLogin(FacesContext context) {
        try {
            String[] loginInfos = findInCookie();
            if (loginInfos != null) {
                String url = MessageFormat.format("/fluxx/j_security_check?j_username={0}&j_password={1}", loginInfos[0], loginInfos[1]);
                context.getExternalContext().redirect(url);
            }
        } catch (IOException ex) {
            Logger.getLogger(ConnectedFluxxerBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String[] findInCookie() {
        String username= getCookie(USERNAME_COOKIE);
        String password = getCookie(PASSWORD_COOKIE);

        if (StringUtils.isNotEmpty(username) && StringUtils.isNotEmpty(password)) {
            return new String[]{username, password};
        } else {
            return null;
        }
    }

    private String getCookie(String key) {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie:cookies) {
            if (cookie.getName().equals(key)) {
                return cookie.getValue();
            }            
        }
        return null;
    }

    private void putInCookie(Fluxxer fluxxer) {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
        Cookie username = new Cookie(USERNAME_COOKIE, fluxxer.getUsername());
        username.setMaxAge(Integer.MAX_VALUE);
        response.addCookie(username);

        Cookie password = new Cookie(PASSWORD_COOKIE, fluxxer.getPassword());
        password.setMaxAge(Integer.MAX_VALUE);
        response.addCookie(password);
    }
}
