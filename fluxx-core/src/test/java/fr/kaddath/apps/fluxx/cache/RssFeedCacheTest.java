package fr.kaddath.apps.fluxx.cache;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import fr.kaddath.apps.fluxx.AbstractTest;

public class RssFeedCacheTest extends AbstractTest {

    private static String key = "mykey";
    private static String value = "myvalue";

    @Test
    public void testPut() {
        cache.put(key, value);
    }

    @Test
    public void testContains() {
        assertTrue(cache.contains(key));
    }

    @Test
    public void testGet() {
        assertNotNull(cache.get(key));
    }

     @Test
     public void testClear() {
         cache.clear();
         assertNull(cache.get(key));
     }

     @Test
     public void testRemove() {
         cache.put(key, value);
         cache.remove(key);
         assertNull(cache.get(key));
     }
}