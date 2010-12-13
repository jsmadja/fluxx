package fr.fluxx.core.collection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PairList<A, B> implements Iterable<Pair<A, B>> {

	private final List<Pair<A, B>> list = new ArrayList<Pair<A, B>>();

	public void add(A left, B right) {
		list.add(new Pair<A, B>(left, right));
	}

	public int size() {
		return list.size();
	}

	@Override
	public Iterator<Pair<A, B>> iterator() {
		return list.iterator();
	}

	public Pair<A, B> get(int i) {
		return list.get(i);
	}

	public boolean isEmpty() {
		return list.isEmpty();
	}

}
