package com.ecommerce.praticboutic_backend_java.requests;

import com.ecommerce.praticboutic_backend_java.models.ColumnData;

import java.util.List;


public class InsertRowRequest {
    private String table;
    private Long bouticid;
    private List<ColumnData> row;

    // Getters et Setters
    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public Long getBouticid() {
        return bouticid;
    }

    public void setBouticid(Long bouticid) {
        this.bouticid = bouticid;
    }

    public List<ColumnData> getRow() {
        return row;
    }

    public void setRow(List<ColumnData> row) {
        this.row = row;
    }
}

