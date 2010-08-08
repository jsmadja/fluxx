package fr.kaddath.apps.fluxx.service;

import fr.kaddath.apps.fluxx.domain.Fluxxer;
import fr.kaddath.apps.fluxx.domain.Item;
import fr.kaddath.apps.fluxx.model.NotificationItemMail;
import java.util.Properties;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;

@Stateless
public class MailService {

    private static final Logger LOG = Logger.getLogger("fluxx");
    private static final Logger STACK = Logger.getLogger("fluxx.stack");

    private Session session;

    @Resource(lookup="fluxx/gmail/username")
    private String username;

    @Resource(lookup="fluxx/gmail/password")
    private String password;

    @Resource(lookup="fluxx/email/response/address")
    private String responseAddress;

    @PostConstruct
    public void init() {
        Properties props = System.getProperties();
        props.setProperty("mail.host", "smtp.gmail.com");
        props.put("mail.smtp.starttls.enable","true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        LOG.finest("Try to create gmail session with couple "+username+"/"+password);
        try {
        session =  Session.getDefaultInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        } catch(SecurityException e) {
            LOG.warning("Envoi de mail désactivé : "+e.getMessage());
        }
    }

    public void notifyMail(Fluxxer fluxxer, Item item) throws MessagingException {
        if (session != null) {
            NotificationItemMail itemMail = new NotificationItemMail(fluxxer, item, session);
            itemMail.buildMail(responseAddress);
            Transport.send(itemMail);
        }
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getResponseAddress() {
        return responseAddress;
    }

    public void setResponseAddress(String responseAddress) {
        this.responseAddress = responseAddress;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
