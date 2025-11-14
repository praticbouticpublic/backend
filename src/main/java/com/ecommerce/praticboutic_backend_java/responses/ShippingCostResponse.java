package com.ecommerce.praticboutic_backend_java.responses;

public class ShippingCostResponse {
    private final double cost;

    public ShippingCostResponse(double cost) {
        this.cost = cost;
    }

    public double getCost() {
        return cost;
    }
}