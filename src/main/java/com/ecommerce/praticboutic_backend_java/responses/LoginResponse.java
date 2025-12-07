package com.ecommerce.praticboutic_backend_java.responses;

public class LoginResponse {
    private Integer bouticid;
    private String customer;
    private String stripecustomerid;
    private String subscriptionstatus;

    // Getters and Setters
    public Integer getCustomerId() {
        return bouticid;
    }
    
    public void setCustomerId(Integer bouticid) {
        this.bouticid = bouticid;
    }
    
    public String getCustomer() {
        return customer;
    }
    
    public void setCustomer(String customer) {
        this.customer = customer;
    }
    
    public String getStripeCustomerId() {
        return stripecustomerid;
    }
    
    public void setStripeCustomerId(String stripecustomerid) {
        this.stripecustomerid = stripecustomerid;
    }
    
    public String getSubscriptionStatus() {
        return subscriptionstatus;
    }
    
    public void setSubscriptionStatus(String subscriptionstatus) {
        this.subscriptionstatus = subscriptionstatus;
    }

}