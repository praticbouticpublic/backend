package com.ecommerce.praticboutic_backend_java.controllers;

import com.ecommerce.praticboutic_backend_java.entities.Client;
import com.ecommerce.praticboutic_backend_java.entities.Customer;
import com.ecommerce.praticboutic_backend_java.repositories.ClientRepository;
import com.ecommerce.praticboutic_backend_java.repositories.CustomerRepository;
import com.ecommerce.praticboutic_backend_java.services.*;
import com.ecommerce.praticboutic_backend_java.requests.*;
import com.ecommerce.praticboutic_backend_java.exceptions.SessionExpiredException;
import com.ecommerce.praticboutic_backend_java.configurations.StripeConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Subscription;
import com.stripe.model.SubscriptionCollection;
import com.stripe.param.SubscriptionListParams;

import jakarta.servlet.http.HttpServletRequest;

import java.util.*;

@RestController
@RequestMapping("/api")
public class FrontQueryController {

    @Autowired
    private CategorieService categorieService;
    
    @Autowired
    private ArticleService articleService;
    
    @Autowired
    private RelGrpOptArtService relGrpOptArtService;
    
    @Autowired
    private OptionService optionService;
    
    @Autowired
    private CustomerService customerService;
    
    @Autowired
    private ClientService clientService;
    
    @Autowired
    private ImageService imageService;
    
    @Autowired
    private AbonnementService abonnementService;
    
    @Autowired
    private ParameterService paramService;
    
    @Autowired
    private StripeConfig stripeConfig;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ClientRepository clientRepository;
    
    @Value("${session.max.lifetime}")
    private int maxLifetime;


    @PostMapping("/front")
    public ResponseEntity<?> handleRequest(@RequestBody FrontQueryRequest input,
                                          HttpServletRequest request, @RequestHeader("Authorization") String authHeader) {
        
        try {


            List<?> result;
            
            // Traitement des différentes requêtes
            switch (input.getRequete()) {
                case "categories":
                    result = categorieService.getCategories(input.getBouticid());
                    break;
                    
                case "articles":
                    result = articleService.getArticles(input.getBouticid(), input.getCatid());
                    break;
                    
                case "groupesoptions":
                    result = relGrpOptArtService.getGroupesOptions(input.getBouticid(), input.getArtid());
                    break;
                    
                case "options":
                    result = optionService.getOptions(input.getGrpoptid());
                    break;
                    
                case "getBouticInfo":
                    result = customerService.getBouticInfo(input.getCustomer());
                    break;
                    
                case "getClientInfo":
                    result = clientService.getClientInfo(input.getCustomer());
                    break;
                    
                case "images":
                    result = imageService.getImages(input.getBouticid(), input.getArtid());
                    break;
                    
                case "aboactif":
                    result = Collections.singletonList(getAbonnementsActifs(input.getBouticid()));
                    break;
                    
                case "initSession":
                    result = Collections.singletonList(initSession(input, authHeader));
                    break;
                    
                case "getSession":
                    result = Collections.singletonList(getSession(authHeader));
                    break;
                    
                case "getparam":
                    result = Collections.singletonList(paramService.getValeur(input.getParam(), input.getBouticid()));
                    break;

                case "getclientprop":
                    result = Collections.singletonList(clientService.getValeur(input.getParam(), input.getBouticid()));
                    break;

                default:
                    return new ResponseEntity<>("Requête non supportée", HttpStatus.BAD_REQUEST);
            }
            
            return new ResponseEntity<>(result, HttpStatus.OK);
            
        } catch (SessionExpiredException e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    private ResponseEntity<?> initSession(FrontQueryRequest input, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");

        Map <java.lang.String, java.lang.Object> payload = JwtService.parseToken(token).getClaims();

        payload.put("customer", input.getCustomer());
        payload.put(input.getCustomer() + "_mail", "non");
        payload.put("method", input.getMethod() != null ? input.getMethod() : "3");
        payload.put("table", input.getTable() != null ? input.getTable() : "0");

        String jwt = JwtService.generateToken(payload, "" );
        Map<String, Object> response = new HashMap<>();
        response.put("token", jwt);

        return ResponseEntity.ok(response);
    }
    
    private ResponseEntity<?> getSession(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");

        Map <java.lang.String, java.lang.Object> payload = JwtService.parseToken(token).getClaims();

        String customer = payload.get("customer").toString();
        String mail = payload.get(customer + "_mail").toString();
        String method = payload.get("method").toString();
        String table = payload.get("table").toString();
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("customer", customer);
        response.put(customer + "_mail", mail);
        response.put("method", method);
        response.put("table", table);

        return ResponseEntity.ok(response);
    }
    
    private Boolean getAbonnementsActifs(Integer bouticid) throws StripeException {
        Optional<Customer> customer = customerRepository.findByCustomid(bouticid);
        if (customer.isEmpty())
            throw new RuntimeException("Impossible de récupérer l'identifiant Stripe de la boutic");
        Client client = clientRepository.findByCustomer(customer.get());

        String stripeCustomerId = client.getStripeCustomerId();
        
        if (stripeCustomerId == null || stripeCustomerId.isEmpty()) {
            throw new RuntimeException("Impossible de récupérer l'identifiant Stripe de la boutic");
        }
        
        // Configuration Stripe
        Stripe.apiKey = stripeConfig.getSecretKey();
        
        SubscriptionListParams params = SubscriptionListParams.builder()
            .setCustomer(stripeCustomerId)
            .setStatus(SubscriptionListParams.Status.ACTIVE)
            .build();
            
        SubscriptionCollection subscriptions = Subscription.list(params);
        return !subscriptions.getData().isEmpty();
    }
}