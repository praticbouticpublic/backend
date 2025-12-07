package com.ecommerce.praticboutic_backend_java.configurations;

import com.stripe.Stripe;
import com.stripe.StripeClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfig {

    @Value("${stripe.secret.key}")
    private String secretKey;
    
    @Value("${stripe.public.key}")
    private String publicKey;

    private StripeClient stripeClient;


    // Getters
    public String getSecretKey() {
        return secretKey;
    }
    
    public String getPublicKey() {
        return publicKey;
    }

    public StripeClient getStripeClient() {
        if (this.stripeClient == null) {
            Stripe.apiKey = this.secretKey;
            this.stripeClient = new StripeClient(this.secretKey);
        }
        return this.stripeClient;

    }
}