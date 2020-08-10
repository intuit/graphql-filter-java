package com.intuit.graphql.demo.context;

import java.util.ArrayList;
import java.util.List;

public class DataFetcherContext<T> {

    private String type;
    private List<T> idList = new ArrayList<>();

    public DataFetcherContext(String type, List<T> idList) {
        this.type = type;
        this.idList = idList;
    }

    public String getType() {
        return type;
    }

    public List<T> getIdList() {
        return idList;
    }
}
