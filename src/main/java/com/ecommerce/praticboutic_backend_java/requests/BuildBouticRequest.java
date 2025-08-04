package com.ecommerce.praticboutic_backend_java.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BuildBouticRequest {

    @JsonProperty("device_id")
    private String deviceId;

    @JsonProperty("device_type")
    private Integer deviceType;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Integer getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(Integer deviceType) {
        this.deviceType = deviceType;
    }

    @Override
    public String toString() {
        return "BuildBouticRequest{" +
                ", deviceId='" + deviceId + '\'' +
                ", deviceType=" + deviceType +
                '}';
    }
}
