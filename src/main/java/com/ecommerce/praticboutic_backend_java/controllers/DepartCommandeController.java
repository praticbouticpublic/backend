package com.ecommerce.praticboutic_backend_java.controllers;

import com.ecommerce.praticboutic_backend_java.entities.Client;
import com.ecommerce.praticboutic_backend_java.entities.Customer;
import com.ecommerce.praticboutic_backend_java.repositories.ClientRepository;
import com.ecommerce.praticboutic_backend_java.repositories.CustomerRepository;
import com.ecommerce.praticboutic_backend_java.services.*;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.lang.Integer.parseInt;


@RestController
@RequestMapping("/api")
public class DepartCommandeController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private ParameterService paramService;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private NotificationService notificationService;

    @Value("${application.url}")
    private String applicationUrl;

    @Value("${mail.sender.address}")
    private String sendmail;

    @Autowired
    private DepartCommandeService departCommandeService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private StripeService stripeService;

    @Autowired
    private SmsService smsService;

    // Déclarez le logger en tant que champ statique en haut de votre classe
    private static final Logger logger = LoggerFactory.getLogger(DepartCommandeController.class);

    @PostMapping("/depart-commande")
    public ResponseEntity<?> creerDepartCommande(@RequestBody Map<String, Object> input, @RequestHeader("Authorization") String authHeader) {
        Customer customerInfo;
        try {
            logger.info("==== Début de traitement /depart-commande ====");
            logger.info("Données reçues : {}", input);
            String token = authHeader.replace("Bearer ", "");
            Map <java.lang.String, java.lang.Object> payload = JwtService.parseToken(token).getClaims();

            // Check if customer exists in session
            String customer = payload.get("customer").toString();
            logger.info("Customer dans la session : {}", customer);
            if (customer == null || customer.isEmpty()) {
                throw new IllegalStateException("Aucun 'customer' dans la session");
            }

            String method = payload.get("method").toString();
            String table = payload.get("table").toString();
            logger.info("Méthode : {}, Table : {}", method, table);

            String emailStatus = payload.get(customer + "_mail").toString();
            logger.info("Statut email pour customer '{}': {}", customer, emailStatus);
            if (emailStatus == null) {
                throw new IllegalStateException("Aucun envoi d'email n'a encore eu lieu");
            }
            if ("oui".equals(emailStatus)) {
                throw new IllegalStateException("Email déjà envoyé pour ce customer");
            }

            customerInfo = customerRepository.findByCustomer(customer);
            if (customerInfo == null) {
                throw new IllegalStateException("Informations du customer introuvables en base");
            }
            logger.info("Customer info : {}", customerInfo);

            Optional<Client> clientInfo = clientRepository.findClientById(customerInfo.getCltid());
            logger.info("Client ID recherché : {}, Client info trouvé : {}", customerInfo.getCltid(), clientInfo);
            if (clientInfo.isEmpty()) {
                throw new IllegalStateException("Client introuvable avec l'ID donné");
            }

            Integer compteur = parseInt(paramService.getParameterValue("CMPT_CMD", customerInfo.getCustomId()));
            String compteurCommande = String.format("%010d", compteur);
            logger.info("Compteur actuel : {}, Commande générée : {}", compteur, compteurCommande);
            paramService.setValeurParam("CMPT_CMD", customerInfo.getCustomId(), (++compteur).toString());

            String subject = paramService.getParameterValue("Subject_mail", customerInfo.getCustomId());
            Double[] sum = new Double[]{0.0};

            logger.info("Envoi d'email à : {}, sujet : {}", customerInfo.getCourriel(), subject);
            departCommandeService.sendEmail(customerInfo.getCourriel(), subject, compteurCommande, input, sum, token);

            Integer cmdId = departCommandeService.enregistreCommande(compteurCommande, input, sum, token);
            logger.info("Commande enregistrée avec ID : {}", cmdId);

            Integer cmptneworder = Integer.valueOf(paramService.getParameterValue("NEW_ORDER", customerInfo.getCustomId()));
            paramService.setValeurParam("NEW_ORDER", customerInfo.getCustomId(), (++cmptneworder).toString());

            logger.info("Envoi de notification push au device : {}", clientInfo.get().getDevice_id());
            notificationService.sendPushNotification(
                    clientInfo.get().getDevice_id(),
                    "Nouvelle(s) commande(s) dans votre Praticboutic",
                    "Commande(s) en attente de validation");

            double remise = Double.parseDouble(input.get("remise").toString());
            double fraisLivraison = Double.parseDouble(input.get("fraislivr").toString());
            logger.info("Enregistrement sur Stripe - remise : {}, frais livraison : {}, total : {}", remise, fraisLivraison, sum[0]);

            boolean usageRecordCreated = stripeService.recordSubscriptionUsage(
                    customerInfo.getCustomId(), sum[0], remise, fraisLivraison);

            if (usageRecordCreated) {
                logger.info("L'enregistrement d'utilisation Stripe a été créé avec succès.");
            } else {
                logger.warn("Aucun enregistrement Stripe créé.");
            }

            String validSms = paramService.getParameterValue("VALIDATION_SMS", customerInfo.getCustomId());
            logger.info("Paramètre SMS : {}, numéro : {}", validSms, input.get("telephone").toString());
            smsService.sendOrderSms(validSms, cmdId, customerInfo.getCustomId(), input.get("telephone").toString());

            payload.put(customer + "_mail", "oui");
            logger.info("Email marqué comme envoyé dans la session.");
            logger.info("==== Fin de traitement /depart-commande ====");
            String jwt = JwtService.generateToken(payload, "" );
            Map<String, Object> response = new HashMap<>();
            response.put("token", jwt);
            return ResponseEntity.ok(response);


        } catch (Exception e) {
            logger.error("Erreur lors de la création de la commande : {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }

    }
}

