package fr.kaddath.apps.fluxx;

import fr.kaddath.apps.fluxx.service.BitlyService;
import fr.kaddath.apps.fluxx.service.MailService;
import fr.kaddath.apps.fluxx.service.NotificationService;
import fr.kaddath.apps.fluxx.service.TwitterService;
import java.util.Properties;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

public class Services {

    public static final TwitterService twitterService = new TwitterService();
    public static final NotificationService notificationService = new NotificationService();
    public static final BitlyService bitlyService = new BitlyService();
    public static final MailService mailService = new MailService();

    static {
        bitlyService.setUserAccount("fluxx");
        bitlyService.setApiKey("R_dcae92ea45295e81fe47d0b6ad928e93");
        bitlyService.init();

        twitterService.setUsernameAccount("fluxxservice");
        twitterService.setPasswordAccount("kjdfsklfsdkl");
        twitterService.setBitlyService(bitlyService);
        twitterService.init();

        Properties props = System.getProperties();
        props.setProperty("mail.host", "smtp.gmail.com");
        props.put("mail.smtp.starttls.enable","true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("julien.smadja@gmail.com", "xedy7bsa");
            }
        });
        mailService.setSession(session);
        mailService.setResponseAddress("payetonspam@free.fr");

        notificationService.setBitlyService(bitlyService);
        notificationService.setTwitterService(twitterService);
        notificationService.setMailService(mailService);
    }
}
