package com.ecommerce.praticboutic_backend_java.requests;

public class LoginLinkRequest {
    private Integer bouticid;
    private String platform;

    public Integer getBouticid() {
        return bouticid;
    }

    public void setBouticid(Integer bouticid) {
        this.bouticid = bouticid;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }
}
