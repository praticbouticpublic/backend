package com.ecommerce.praticboutic_backend_java.controllers;

import com.ecommerce.praticboutic_backend_java.entities.Client;
import com.ecommerce.praticboutic_backend_java.repositories.ClientRepository;
import com.ecommerce.praticboutic_backend_java.repositories.CustomerRepository;
import com.ecommerce.praticboutic_backend_java.requests.LoginLinkRequest;
import com.ecommerce.praticboutic_backend_java.services.JwtService;
import com.ecommerce.praticboutic_backend_java.services.ParameterService;
import com.ecommerce.praticboutic_backend_java.services.SessionService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.model.AccountLink;
import com.stripe.model.LoginLink;
import com.stripe.model.SubscriptionCollection;
import com.stripe.param.AccountCreateParams;
import com.stripe.param.AccountLinkCreateParams;
import com.stripe.param.LoginLinkCreateOnAccountParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class LoginLinkController {

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @Value("${app.protocol:http}")
    private String protocol;
    
    @Value("${app.version:0.0.2}")
    private String appVersion;

    @Value("${app.base-url}")
    private String baseUrl;

    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private ParameterService paramService;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    protected JwtService jwtService;



    @PostMapping("/login-link")
    public ResponseEntity<?> createLoginLink(@RequestBody LoginLinkRequest request, @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            Map <java.lang.String, java.lang.Object> payload = JwtService.parseToken(token).getClaims();

            // Vérifier l'authentification
            if (!jwtService.isAuthenticated(payload)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Non authentifié"));
            }

            // Configurer Stripe
            Stripe.apiKey = stripeSecretKey;
            Stripe.setAppInfo(
                "pratic-boutic/registration",
                appVersion,
                "https://praticboutic.fr"
            );
            
            // Récupérer l'ID client Stripe
            //String userEmail = sessionService.getUserEmail();
            String userEmail = payload.get("bo_email").toString();
            Optional<Client> client = clientRepository.findByEmailAndActif(userEmail, 1);
            if (client.isEmpty())
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Pas de client avec courriel " + userEmail));

            String stripeCustomerId = client.get().getStripeCustomerId();
            if (stripeCustomerId == null || stripeCustomerId.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Id compte stripe client manquant"));
            }

            // Vérifier que le client a un abonnement actif
            Map<String, Object> subscriptionParams = new HashMap<>();
            subscriptionParams.put("customer", stripeCustomerId);
            subscriptionParams.put("status", "active");
            SubscriptionCollection subscriptions = com.stripe.model.Subscription.list(subscriptionParams);
            
            if (subscriptions.getData().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Pas d'abonnement actif"));
            }
            
            // Récupérer ou créer un compte Stripe Connect
            String stripeAccountId = paramService.getValeur("STRIPE_ACCOUNT_ID", request.getBouticid());
            String url;
            Map<String, Object> response = new HashMap<>();

            if (stripeAccountId != null && !stripeAccountId.isEmpty()) {
                Account account = Account.retrieve(stripeAccountId);

                if (account.getDetailsSubmitted()) {
                    // Compte déjà onboardé → login link
                    LoginLinkCreateOnAccountParams params = LoginLinkCreateOnAccountParams.builder().build();
                    LoginLink loginLink = LoginLink.createOnAccount(stripeAccountId, params);
                    String jwt = JwtService.generateToken(payload, "");
                    response.put("token", jwt);
                    response.put("url", loginLink.getUrl());
                } else {
                    // Compte existant mais onboarding incomplet → créer un NOUVEL account link
                    String refreshUrl = baseUrl + "/api/redirect-handler?platform=" + request.getPlatform() + "&status=refresh";
                    String returnUrl = baseUrl + "/api/redirect-handler?platform=" + request.getPlatform() + "&status=return";

                    AccountLinkCreateParams linkParams = AccountLinkCreateParams.builder()
                            .setAccount(stripeAccountId)
                            .setRefreshUrl(refreshUrl)
                            .setReturnUrl(returnUrl)
                            .setType(AccountLinkCreateParams.Type.ACCOUNT_ONBOARDING)
                            .build();

                    AccountLink accountLink = AccountLink.create(linkParams);
                    String jwt = JwtService.generateToken(payload, "");
                    response.put("token", jwt);
                    response.put("url", accountLink.getUrl());
                }
            } else {
                // Pas de compte → créer nouveau
                response = createInscription(request.getBouticid(), request.getPlatform(), payload);
            }

            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }
    
    private Map<String, Object> createInscription(Integer bouticId, String platform, Map <java.lang.String, java.lang.Object> payload) throws StripeException {

        // Créer un compte Stripe Connect Express
        AccountCreateParams accountParams = AccountCreateParams.builder()
                .setType(AccountCreateParams.Type.EXPRESS)
                .setCountry("FR")
                .setEmail(payload.get("bo_email").toString())
                .setCapabilities(
                        AccountCreateParams.Capabilities.builder()
                                .setCardPayments(AccountCreateParams.Capabilities.CardPayments.builder().setRequested(true).build())
                                .setTransfers(AccountCreateParams.Capabilities.Transfers.builder().setRequested(true).build())
                                .build()
                )
                .build();
                
        Account account = Account.create(accountParams);
        
        // Enregistrer l'ID du compte Stripe
        paramService.setValeur("STRIPE_ACCOUNT_ID", account.getId(), bouticId);
        
        // Stocker l'ID de boutique dans la session
        //sessionService.setBoId(bouticId);
        payload.put("bo_id", bouticId);
        
        // Créer un lien d'onboarding
        String refreshUrl = baseUrl + "/api/redirect-handler?platform=" + platform;
        String returnUrl = baseUrl + "/api/redirect-handler?platform=" + platform;
        
        AccountLinkCreateParams linkParams = AccountLinkCreateParams.builder()
                .setAccount(account.getId())
                .setRefreshUrl(refreshUrl)
                .setReturnUrl(returnUrl)
                .setType(AccountLinkCreateParams.Type.ACCOUNT_ONBOARDING)
                .build();
                
        AccountLink accountLink = AccountLink.create(linkParams);

        String jwt = JwtService.generateToken(payload, "" );
        Map<String, Object> response = new HashMap<>();
        response.put("token", jwt);
        response.put("url", accountLink.getUrl());
        
        return response;
    }
    
}