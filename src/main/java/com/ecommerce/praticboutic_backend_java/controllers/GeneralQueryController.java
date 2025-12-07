package com.ecommerce.praticboutic_backend_java.controllers;

import com.ecommerce.praticboutic_backend_java.entities.Customer;
//import com.ecommerce.praticboutic_backend_java.services.ClientService;
import com.ecommerce.praticboutic_backend_java.services.ClientService;
import com.ecommerce.praticboutic_backend_java.services.CustomerService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.*;

@RestController
@RequestMapping("/api")
public class GeneralQueryController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private EntityManager entityManager;

    @PostMapping("/genquery")
    public ResponseEntity<?> processGenQuery(@RequestBody(required = false) Map<String, Object> input) {
        try {

            String action = (String) input.get("action");

            if ("listcustomer".equals(action)) {
                List<List<Object>> result = new ArrayList<>();

                // Utilisation d'une requête native pour reproduire exactement le comportement PHP
                // Qui fait une jointure entre customer et client
                String query = "SELECT c.customid, c.customer, c.nom, c.logo, cl.stripe_customer_id " +
                        "FROM customer c, client cl " +
                        "WHERE c.actif = 1 AND c.cltid = cl.cltid";

                // Exécution de la requête native
                Query nquery = entityManager.createNativeQuery(query);
                @SuppressWarnings("unchecked")
                List<Object[]> queryResult = nquery.getResultList();

                // Construction du résultat dans le même format que le PHP
                for (Object[] row : queryResult) {
                    List<Object> customerData = new ArrayList<>();
                    customerData.add(row[0]); // customid
                    customerData.add(row[1]); // customer
                    customerData.add(row[2]); // nom
                    customerData.add(row[3]); // logo
                    customerData.add(row[4]); // stripe_customer_id
                    result.add(customerData);
                }

                return ResponseEntity.ok(result);
            }

            return ResponseEntity.ok(new ArrayList<>());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}