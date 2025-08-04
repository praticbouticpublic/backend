// FrontRequest.java - Classe de requête
package com.ecommerce.praticboutic_backend_java.requests;

public class FrontRequest {
    private String requete;
    private String sessionId;
    private Integer bouticid;
    private Integer catid;
    private Integer artid;
    private Integer grpoptid;
    private String customer;
    private String method;
    private String table;
    private String param;
    
    // Getters et Setters
    public String getRequete() {
        return requete;
    }
    
    public void setRequete(String requete) {
        this.requete = requete;
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    public Integer getBouticid() {
        return bouticid;
    }
    
    public void setBouticid(Integer bouticid) {
        this.bouticid = bouticid;
    }
    
    public Integer getCatid() {
        return catid;
    }
    
    public void setCatid(Integer catid) {
        this.catid = catid;
    }
    
    public Integer getArtid() {
        return artid;
    }
    
    public void setArtid(Integer artid) {
        this.artid = artid;
    }
    
    public Integer getGrpoptid() {
        return grpoptid;
    }
    
    public void setGrpoptid(Integer grpoptid) {
        this.grpoptid = grpoptid;
    }
    
    public String getCustomer() {
        return customer;
    }
    
    public void setCustomer(String customer) {
        this.customer = customer;
    }
    
    public String getMethod() {
        return method;
    }
    
    public void setMethod(String method) {
        this.method = method;
    }
    
    public String getTable() {
        return table;
    }
    
    public void setTable(String table) {
        this.table = table;
    }
    
    public String getParam() {
        return param;
    }
    
    public void setParam(String param) {
        this.param = param;
    }
}