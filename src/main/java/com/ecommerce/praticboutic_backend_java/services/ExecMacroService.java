package com.ecommerce.praticboutic_backend_java.services;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.SubscriptionCollection;
import com.stripe.param.SubscriptionListParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ExecMacroService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Déclarez le logger en tant que champ statique en haut de votre classe
    private static final Logger logger = LoggerFactory.getLogger(ExecMacroService.class);

    @Value("${stripe.secret.key}")
    private String stripeApiKey;

    public Integer desactiveBoutic() {

        Stripe.apiKey = stripeApiKey;

        try {
            // Récupérer les clients sans cltid
            String sql = "SELECT customid FROM customer WHERE cltid = 0";

            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

            for (Map<String, Object> row : rows) {
                Long customId = ((Number) row.get("customid")).longValue();
                updateCustomerActiveStatus(customId, false);
            }

            return 0;

        } catch (Exception e) {
            logger.error("Error during macro execution: {}", e.getMessage());
        }

        try {
            // Récupérer les clients avec leur stripe_customer_id
            String sql = "SELECT customer.customid, client.stripe_customer_id " +
                    "FROM customer JOIN client ON customer.cltid = client.cltid";

            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

            for (Map<String, Object> row : rows) {
                Long customId = ((Number) row.get("customid")).longValue();
                String stripeCustomerId = (String) row.get("stripe_customer_id");

                boolean actif = checkActiveSubscription(stripeCustomerId);
                updateCustomerActiveStatus(customId, actif);
            }

             return 0;

        } catch (Exception e) {
            logger.error("Error during macro execution: {}", e.getMessage());
        }
        return -1;
    }

    private boolean checkActiveSubscription(String stripeCustomerId) throws StripeException {
        if (stripeCustomerId == null || stripeCustomerId.isEmpty()) {
            return false;
        }

        SubscriptionListParams params = SubscriptionListParams.builder()
                .setCustomer(stripeCustomerId)
                .setStatus(SubscriptionListParams.Status.ACTIVE)
                .build();

        SubscriptionCollection subscriptions = com.stripe.model.Subscription.list(params);
        return !subscriptions.getData().isEmpty();
    }

    private void updateCustomerActiveStatus(Long customId, boolean actif) {
        String updateSql = "UPDATE customer SET actif = ? WHERE customid = ?";
        jdbcTemplate.update(updateSql, actif ? 1 : 0, customId);
    }
}
