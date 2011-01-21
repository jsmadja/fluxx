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

package fr.fluxx.core.collection;

import org.junit.Assert;
import org.junit.Test;

import fr.fluxx.core.collection.Pair;
import fr.fluxx.core.collection.PairList;

public class PairListTest {

	@Test
	public void should_add_an_element() {
		Pair<Integer, String> pair = new Pair<Integer, String>(0, "");
		PairList<Integer, String> list = new PairList<Integer, String>();
		list.add(0, "");
		Assert.assertEquals(pair, list.get(0));
	}

	@Test
	public void should_have_0_size() {
		PairList<Integer, String> list = new PairList<Integer, String>();
		Assert.assertEquals(0, list.size());
	}

	@Test
	public void should_have_size_equals_to_2() {
		PairList<Integer, String> list = new PairList<Integer, String>();
		list.add(0, "right");
		list.add(0, "right");
		Assert.assertEquals(2, list.size());
	}

	@Test
	public void should_iterate() {
		PairList<Integer, String> list = new PairList<Integer, String>();
		for (Pair<Integer, String> pair : list) {
			Assert.assertNotNull(pair);
		}
	}

}
