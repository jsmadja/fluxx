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

package fr.fluxx.admin.model;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

@SuppressWarnings("rawtypes")
public class CollectionDataModel {

	private final int rowsPerPage = Integer.MAX_VALUE;

	private final Collection data;
	private Paginator paginator;
	private final DataModel dataModel;

	public CollectionDataModel(Collection data) {
		this.data = data;
		this.dataModel = getPaginator().createPageDataModel();
	}

	private Paginator getPaginator() {
		if (paginator == null) {
			paginator = new Paginator(rowsPerPage) {

				@Override
				public int getItemsCount() {
					return data.size();
				}

				@SuppressWarnings("unchecked")
				@Override
				public DataModel createPageDataModel() {
					int first = getPageFirstItem();
					int last = Math.min(data.size(), getPageFirstItem() + getPageSize());
					return new ListDataModel(new ArrayList(data).subList(first, last));
				}
			};
		}

		return paginator;
	}

	public DataModel getDataModel() {
		return dataModel;
	}
}
