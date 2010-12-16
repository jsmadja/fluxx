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
