package fr.kaddath.apps.fluxx.resource;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class FluxxMessage {

    private static ResourceBundle messageResourceBundle;

    static {
        messageResourceBundle = ResourceBundle.getBundle("Messages");
    }

    public static String m(String key) {
        return messageResourceBundle.getString(key);
    }

    public static String m(String key, Object... attrs) {
        return MessageFormat.format(m(key), attrs);
    }

}
