package com.ecommerce.praticboutic_backend_java.responses;


public class VerificationResponse {
    private String encryptedCode;
    private String iv;

    public String getEncryptedCode() {
        return encryptedCode;
    }

    public void setEncryptedCode(String encryptedCode) {
        this.encryptedCode = encryptedCode;
    }

    public String getIv() {
        return iv;
    }

    public void setIv(String iv) {
        this.iv = iv;
    }
}
