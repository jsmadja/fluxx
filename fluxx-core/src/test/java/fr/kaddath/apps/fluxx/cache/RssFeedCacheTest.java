/**
 * Copyright (C) 2010 Julien SMADJA <julien.smadja@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.kaddath.apps.fluxx.cache;

import fr.kaddath.apps.fluxx.AbstractTest;
import org.junit.Test;

import static org.junit.Assert.*;

public class RssFeedCacheTest extends AbstractTest {

    private static final Long KEY_DOESNT_EXIST = -1L;
    private static Long key = 1L;
    private static String value = "myvalue";

    @Test
    public void should_put_an_object_in_cache() {
        cache.put(key, value);
        assertNotNull(cache.get(key));
    }

    @Test
    public void should_return_true_when_calling_contains_on_an_existing_object() {
        cache.put(key, value);
        assertTrue(cache.contains(key));
    }

    @Test
    public void should_return_false_when_calling_contains_on_an_inexistant_object() {
        assertFalse(cache.contains(KEY_DOESNT_EXIST));
    }

    @Test
    public void should_return_a_value() {
        cache.put(key, value);
        assertEquals(value, cache.get(key));
    }

    @Test
    public void should_not_fail_on_getting_an_inexistant_value() {
        assertNull(cache.get(KEY_DOESNT_EXIST));
    }

    @Test
    public void should_clear_the_cache() {
        cache.clear();
        assertNull(cache.get(key));
    }

    @Test
    public void should_remove_an_existing_value() {
        cache.put(key, value);
        cache.remove(key);
        assertNull(cache.get(key));
    }

    @Test
    public void should_not_fail_when_removing_an_inexistant_value() {
        cache.remove(KEY_DOESNT_EXIST);
    }
}