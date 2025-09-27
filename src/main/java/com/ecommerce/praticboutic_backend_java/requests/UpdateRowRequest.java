package com.ecommerce.praticboutic_backend_java.requests;

import com.ecommerce.praticboutic_backend_java.models.ColumnData;

import java.util.List;

public class UpdateRowRequest {
    private String table;
    private Long bouticid;
    private List<ColumnData> row;
    private Long idtoup;
    private String colonne;

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

    public Long getIdtoup() {
        return idtoup;
    }

    public void setIdtoup(Long idtoup) {
        this.idtoup = idtoup;
    }

    public String getColonne() {
        return colonne;
    }

    public void setColonne(String colonne) {
        this.colonne = colonne;
    }
}