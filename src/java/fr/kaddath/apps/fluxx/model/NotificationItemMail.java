package fr.kaddath.apps.fluxx.model;

import fr.kaddath.apps.fluxx.domain.Fluxxer;
import fr.kaddath.apps.fluxx.domain.Item;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class NotificationItemMail extends MimeMessage {

    private Item item;
    private Fluxxer fluxxer;

    public NotificationItemMail(Fluxxer fluxxer, Item item, Session session) {
        super(session);
        this.fluxxer = fluxxer;
        this.item = item;
    }

    public void buildMail(String emailFrom) throws MessagingException {
        setFrom(new InternetAddress(emailFrom));
        addRecipient(Message.RecipientType.TO, new InternetAddress(fluxxer.getEmail()));
        addSubject();
        addBody();
    }

    private void addSubject() throws MessagingException {
        String subject = createSubject();
        setSubject(subject);
    }

    private void addBody() throws MessagingException {
        MimeMultipart multipart = new MimeMultipart("related");
        BodyPart messageBodyPart = new MimeBodyPart();
        String text = createText();
        messageBodyPart.setContent(text, "text/html");
        multipart.addBodyPart(messageBodyPart);
        setContent(multipart);
    }

    private String createSubject() {
        return "New item in your aggregated feed : "+item.getTitle();
    }

    private String createText() {
        return "You can read your item <a target=\"blank\" href=\""+item.getUri()+"\">here</a>";
    }
}
