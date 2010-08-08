package fr.kaddath.apps.fluxx.service;

import fr.kaddath.apps.fluxx.domain.Feed;
import fr.kaddath.apps.fluxx.domain.Fluxxer;
import fr.kaddath.apps.fluxx.domain.Item;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.mail.MessagingException;

@Stateless
public class NotificationService {

    private static final Logger LOG = Logger.getLogger(NotificationService.class.getName());
    private static final Logger STACK = Logger.getLogger("fluxx.stack");

    @EJB
    private BitlyService bitlyService;

    @EJB
    private TwitterService twitterService;

    @EJB
    private MailService mailService;

    @EJB
    private UserService userService;

    public void notifyOnNewItem(Feed feed, Item item) {
        List<Fluxxer> fluxxersToNotify = userService.findByFeed(feed);
        for (Fluxxer fluxxer:fluxxersToNotify) {
            notifyOnNewItem(fluxxer, item);
        }        
    }

    public void notifyOnNewItem(Fluxxer fluxxer, Item item) {

        if (fluxxer.getTwitterNotification()) {
            try {
                notifyTwitter(fluxxer, item);
            } catch (Exception ex) {
               LOG.severe("Can't send tweet of item ["+item.getLink()+"] to fluxxer "+fluxxer.getUsername());
               STACK.throwing(NotificationService.class.getName(), "notifyTwitter", ex);
            }
        }

        if (fluxxer.getMailNotification()) {
            try {
                notifyMail(fluxxer, item);
            } catch (MessagingException ex) {
                LOG.severe("Can't send mail of item ["+item.getLink()+"] to fluxxer "+fluxxer.getUsername());
                STACK.throwing(NotificationService.class.getName(), "notifyMail", ex);
            }
        }
    }

    private void notifyTwitter(Fluxxer fluxxer, Item item) throws Exception {
        String tweet = twitterService.createPublicTweetWithUrl(item.getTitle(), item.getUri(), fluxxer.getTwitterAccount());
        twitterService.updateStatus(tweet);
    }

    private void notifyMail(Fluxxer fluxxer, Item item) throws MessagingException {
        mailService.notifyMail(fluxxer, item);
    }

    public BitlyService getBitlyService() {
        return bitlyService;
    }

    public void setBitlyService(BitlyService bitlyService) {
        this.bitlyService = bitlyService;
    }

    public TwitterService getTwitterService() {
        return twitterService;
    }

    public void setTwitterService(TwitterService twitterService) {
        this.twitterService = twitterService;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public MailService getMailService() {
        return mailService;
    }

    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }

}
