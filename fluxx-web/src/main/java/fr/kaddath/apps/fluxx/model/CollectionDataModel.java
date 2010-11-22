package fr.kaddath.apps.fluxx.model;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

public class CollectionDataModel {

    private int  rowsPerPage = Integer.MAX_VALUE;

    private Collection data;
    private Paginator paginator;
    private DataModel dataModel;

    public CollectionDataModel(Collection data) {
        this.data = data;
        this.dataModel = getPaginator().createPageDataModel();
    }

    private  Paginator getPaginator() {
        if (paginator == null) {
            paginator = new Paginator(rowsPerPage) {

                @Override
                public int getItemsCount() {
                    return data.size();
                }

                @Override
                public DataModel createPageDataModel() {
                    int first = getPageFirstItem();
                    int last = Math.min(data.size(), getPageFirstItem()+getPageSize());
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
