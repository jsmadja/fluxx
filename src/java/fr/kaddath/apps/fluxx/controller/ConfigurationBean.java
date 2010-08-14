package fr.kaddath.apps.fluxx.controller;

import fr.kaddath.apps.fluxx.service.NotificationService;
import javax.inject.Inject;
import javax.inject.Named;

@Named("configuration")
public class ConfigurationBean {

    @Inject
    private NotificationService notificationService;

    public NotificationService getNotificationService() {
        return notificationService;
    }
    
}
