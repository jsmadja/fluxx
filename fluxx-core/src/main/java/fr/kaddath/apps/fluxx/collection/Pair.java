package fr.kaddath.apps.fluxx.collection;

public class Pair<K, V> {

	private final K left;
	private final V right;

	public Pair(K left, V right) {
		this.left = left;
		this.right = right;
	}

	public K left() {
		return left;
	}

	public V right() {
		return right;
	}

	@Override
	public String toString() {
		return left + " -> " + right;
	}
}
