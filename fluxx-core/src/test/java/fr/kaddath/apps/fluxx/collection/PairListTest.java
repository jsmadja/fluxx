package fr.kaddath.apps.fluxx.collection;

import org.junit.Assert;
import org.junit.Test;

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
