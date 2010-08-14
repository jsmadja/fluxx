package fr.kaddath.apps.fluxx.cache;

import fr.kaddath.apps.fluxx.AbstractTest;
import org.junit.Test;
import static org.junit.Assert.*;

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