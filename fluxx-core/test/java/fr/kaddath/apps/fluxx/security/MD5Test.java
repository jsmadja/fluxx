package fr.kaddath.apps.fluxx.security;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.junit.Test;

public class MD5Test {

    @Test
    public void md5() throws NoSuchAlgorithmException {
        String password = "kaddath";
        MessageDigest digest = MessageDigest.getInstance("MD5");
        byte[] hash = digest.digest(password.getBytes());
        String encryptedPassword = new BigInteger(hash).toString(16);
        assertEquals("379111c5742882056340a85cb25e9d2a", encryptedPassword);
    }
}
