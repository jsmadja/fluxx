package fr.kaddath.apps.fluxx.security;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CryptographicService {

    public static String toMD5(String phrase) {
        try {
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(phrase.getBytes());
            byte[] hash = digest.digest();
            return new BigInteger(hash).toString(16);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(CryptographicService.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }

}
