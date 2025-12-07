package com.ecommerce.praticboutic_backend_java.requests;

public class ShopConfigRequest {
    private String chxmethode;
    private String chxpaie;
    private String mntmincmd;
    private Integer validsms;

    public String getChxmethode() {
        return chxmethode;
    }

    public void setChxmethode(String chxmethode) {
        this.chxmethode = chxmethode;
    }

    public String getChxpaie() {
        return chxpaie;
    }

    public void setChxpaie(String chxpaie) {
        this.chxpaie = chxpaie;
    }

    public String getMntmincmd() {
        return mntmincmd;
    }

    public void setMntmincmd(String mntmincmd) {
        this.mntmincmd = mntmincmd;
    }

    public Integer getValidsms() {
        return validsms;
    }

    public void setValidsms(Integer validsms) {
        this.validsms = validsms;
    }
}