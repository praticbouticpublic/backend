package com.ecommerce.praticboutic_backend_java.services;


import com.ecommerce.praticboutic_backend_java.requests.SuppressionRequest;
import com.stripe.Stripe;
import com.stripe.model.Account;
import com.stripe.model.Subscription;
import com.stripe.model.SubscriptionCollection;
import com.stripe.net.RequestOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SuppressionService {

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void supprimerCompte(SuppressionRequest request, String ip) throws Exception {

        Stripe.apiKey = stripeSecretKey;

        String sqlClient = "SELECT stripe_customer_id, cltid FROM client WHERE email = ? AND actif = 1";
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sqlClient, request.getEmail());

        if (result.isEmpty()) {
            throw new Exception("Erreur ! Client non trouvé");
        }

        String stripeCustomerId = (String) result.get(0).get("stripe_customer_id");
        Integer cltid = Integer.parseInt(result.get(0).get("cltid").toString());

        // Suppression du compte Stripe
        try {
            String accountId = getValeurParam("STRIPE_ACCOUNT_ID", request.getBouticid());
            Account.retrieve(accountId).delete();
        } catch (Exception e) {
            System.err.println("Erreur suppression compte Stripe");
        }

        // Annulation des abonnements
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("customer", stripeCustomerId);
            SubscriptionCollection subscriptions = Subscription.list(params);
            for (Subscription s : subscriptions.getData()) {
                s.cancel();
            }
        } catch (Exception e) {
            System.err.println("Erreur annulation abonnements Stripe");
        }

        // Suppression des images
        supprimerImages(request.getBouticid());

        // Suppression des données en base
        supprimerDonneesBase(request.getBouticid(), cltid, request.getEmail(), ip);
    }

    private String getValeurParam(String nom, Integer customid) {
        return jdbcTemplate.queryForObject(
                "SELECT valeur FROM parametre WHERE nom = ? AND customid = ?",
                String.class,
                nom,
                customid
        );
    }

    private void supprimerImages(Integer bouticid) {
        String basePath = "../../upload/";
        try {
            // Images articles
            jdbcTemplate.query("SELECT image FROM artlistimg WHERE customid = ?", new Object[]{bouticid},
                    (rs, rowNum) -> {
                        File f = new File(basePath + rs.getString("image"));
                        f.delete();
                        return null;
                    });

            // Logo
            jdbcTemplate.query("SELECT logo FROM customer WHERE customid = ?", new Object[]{bouticid},
                    (rs, rowNum) -> {
                        File f = new File(basePath + rs.getString("logo"));
                        f.delete();
                        return null;
                    });

        } catch (Exception e) {
            System.err.println("Erreur suppression images");
        }
    }

    private void supprimerDonneesBase(Integer customid, Integer cltid, String email, String ip) {
        // Suppressions liées à customid
        String[] customidQueries = {
                "DELETE FROM artlistimg WHERE customid = ?",
                "DELETE FROM promotion WHERE customid = ?",
                "DELETE FROM cpzone WHERE customid = ?",
                "DELETE FROM barlivr WHERE customid = ?",
                "DELETE FROM relgrpoptart WHERE customid = ?",
                "DELETE FROM `option` WHERE customid = ?",
                "DELETE FROM groupeopt WHERE customid = ?",
                "DELETE FROM article WHERE customid = ?",
                "DELETE FROM categorie WHERE customid = ?",
                "DELETE FROM lignecmd WHERE customid = ?",
                "DELETE FROM statutcmd WHERE customid = ?",
                "DELETE FROM commande WHERE customid = ?",
                "DELETE FROM parametre WHERE customid = ?",
                "DELETE FROM customer WHERE customid = ?"
        };

        for (String query : customidQueries) {
            jdbcTemplate.update(query, customid);
        }

        // Suppressions liées à cltid
        String[] cltidQueries = {
                "DELETE FROM abonnement WHERE cltid = ?",
                "DELETE FROM client WHERE cltid = ?"
        };

        for (String query : cltidQueries) {
            jdbcTemplate.update(query, cltid);
        }

        // Suppression liée à l'email
        jdbcTemplate.update("DELETE FROM identifiant WHERE email = ?", email);

        // Suppression liée à l'adresse IP
        jdbcTemplate.update("DELETE FROM connexion WHERE ip = ?", ip);
    }
}

