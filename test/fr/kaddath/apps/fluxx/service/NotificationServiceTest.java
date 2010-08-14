package fr.kaddath.apps.fluxx.service;

import fr.kaddath.apps.fluxx.Services;
import fr.kaddath.apps.fluxx.domain.Fluxxer;
import fr.kaddath.apps.fluxx.domain.Item;
import org.junit.Test;

public class NotificationServiceTest {

    @Test
    public void notifyNewItem() {        
        String uri = "http://www.bestechvideos.com/2010/01/02/tekzilla-daily-tip-519-upload-large-files-online-free";
        String title = "Tekzilla Daily Tip #519: Upload Large Files Online Free!";

        Fluxxer fluxxer = new Fluxxer();
        fluxxer.setTwitterAccount("jsmadja");
        fluxxer.setEmail("julien.smadja@gmail.com");
        fluxxer.setMailNotification(Boolean.TRUE);
        fluxxer.setTwitterNotification(Boolean.FALSE);

        Item item = new Item();
        item.setUri(uri);
        item.setTitle(title);

        Services.notificationService.notifyOnNewItem(fluxxer, item);
    }
    
}