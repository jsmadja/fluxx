package fr.kaddath.apps.fluxx.model;

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
