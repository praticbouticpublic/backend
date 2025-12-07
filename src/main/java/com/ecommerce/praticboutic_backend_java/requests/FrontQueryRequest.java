package com.ecommerce.praticboutic_backend_java.requests;

/**
 * Classe représentant une requête vers l'API frontale
 * Correspond aux paramètres JSON envoyés dans le script frontquery.php
 */
public class FrontQueryRequest {

    /**
     * Type de requête (categories, articles, options, etc.)
     */
    private String requete;

    /**
     * Identifiant de la boutique
     */
    private Integer bouticid;
    
    /**
     * Identifiant de la catégorie
     */
    private Integer catid;
    
    /**
     * Identifiant de l'article
     */
    private Integer artid;
    
    /**
     * Identifiant du groupe d'options
     */
    private Integer grpoptid;
    
    /**
     * Identifiant du client (customer)
     */
    private String customer;
    
    /**
     * Méthode de commande (livraison, sur place, etc.)
     */
    private String method;
    
    /**
     * Numéro de table (pour commande sur place)
     */
    private String table;
    
    /**
     * Nom du paramètre à récupérer
     */
    private String param;

    // Constructeurs
    public FrontQueryRequest() {
    }

    public FrontQueryRequest(String requete) {
        this.requete = requete;
    }

    // Getters et Setters
    public String getRequete() {
        return requete;
    }

    public void setRequete(String requete) {
        this.requete = requete;
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

    @Override
    public String toString() {
        return "FrontQueryRequest{" +
                "requete='" + requete + '\'' +
                ", bouticid=" + bouticid +
                ", catid=" + catid +
                ", artid=" + artid +
                ", grpoptid=" + grpoptid +
                ", customer='" + customer + '\'' +
                ", method='" + method + '\'' +
                ", table='" + table + '\'' +
                ", param='" + param + '\'' +
                '}';
    }

    /**
     * Builder pour faciliter la création d'instances
     */
    public static class Builder {
        private String requete;
        private Integer bouticid;
        private Integer catid;
        private Integer artid;
        private Integer grpoptid;
        private String customer;
        private String method;
        private String table;
        private String param;

        public Builder requete(String requete) {
            this.requete = requete;
            return this;
        }

        public Builder bouticid(Integer bouticid) {
            this.bouticid = bouticid;
            return this;
        }

        public Builder catid(Integer catid) {
            this.catid = catid;
            return this;
        }

        public Builder artid(Integer artid) {
            this.artid = artid;
            return this;
        }

        public Builder grpoptid(Integer grpoptid) {
            this.grpoptid = grpoptid;
            return this;
        }

        public Builder customer(String customer) {
            this.customer = customer;
            return this;
        }

        public Builder method(String method) {
            this.method = method;
            return this;
        }

        public Builder table(String table) {
            this.table = table;
            return this;
        }

        public Builder param(String param) {
            this.param = param;
            return this;
        }

        public FrontQueryRequest build() {
            FrontQueryRequest request = new FrontQueryRequest();
            request.requete = this.requete;
            request.bouticid = this.bouticid;
            request.catid = this.catid;
            request.artid = this.artid;
            request.grpoptid = this.grpoptid;
            request.customer = this.customer;
            request.method = this.method;
            request.table = this.table;
            request.param = this.param;
            return request;
        }
    }

    /**
     * Méthode pratique pour créer un nouveau Builder
     * @return un nouveau Builder pour FrontQueryRequest
     */
    public static Builder builder() {
        return new Builder();
    }
}