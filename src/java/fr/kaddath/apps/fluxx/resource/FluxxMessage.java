package fr.kaddath.apps.fluxx.resource;

import java.util.ResourceBundle;

public class FluxxMessage {

    private static ResourceBundle messageResourceBundle;

    static {
        messageResourceBundle = ResourceBundle.getBundle("Messages");
    }

    public static String m(String key) {
        return messageResourceBundle.getString(key);
    }

}
