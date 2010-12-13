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

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Pair) {
			Pair<?, ?> p = (Pair<?, ?>) obj;
			return left.equals(p.left) && right.equals(p.right);
		}
		return false;
	}
}
