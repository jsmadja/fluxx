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

package fr.fluxx.web.model;

import javax.faces.model.DataModel;

public abstract class Paginator {

	private final int pageSize;
	private int page;

	public Paginator(int pageSize) {
		this.pageSize = pageSize;
	}

	public abstract int getItemsCount();

	@SuppressWarnings("rawtypes")
	public abstract DataModel createPageDataModel();

	public int getPageFirstItem() {
		return page * pageSize;
	}

	public int getPageLastItem() {
		int i = getPageFirstItem() + pageSize - 1;
		int count = getItemsCount() - 1;
		if (i > count) {
			i = count;
		}
		if (i < 0) {
			i = 0;
		}
		return i;
	}

	public boolean isHasNextPage() {
		return (page + 1) * pageSize + 1 <= getItemsCount();
	}

	public void nextPage() {
		if (isHasNextPage()) {
			page++;
		}
	}

	public boolean isHasPreviousPage() {
		return page > 0;
	}

	public void previousPage() {
		if (isHasPreviousPage()) {
			page--;
		}
	}

	public int getPageSize() {
		return pageSize;
	}
}
