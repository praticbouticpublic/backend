package com.ecommerce.praticboutic_backend_java.controllers;

import com.ecommerce.praticboutic_backend_java.services.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class NotifPushController {

    @Autowired
    private NotificationService notificationService;

    // Déclarez le logger en tant que champ statique en haut de votre classe
    private static final Logger logger = LoggerFactory.getLogger(NotifPushController.class);

    @PostMapping("/send-push-notif")
    public ResponseEntity<?> creerDepartCommande(@RequestBody Map<String, Object> input) {

        try {
            logger.info("==== Début de traitement /send-push-notif ====");
            logger.info("Données reçues : {}", input);

            String device_id = input.get("deviceid").toString();
            String subject = input.get("subject").toString();
            String msg = input.get("msg").toString();

            logger.info("Envoi de notification push au device : {}", device_id);
            notificationService.sendPushNotification(device_id, subject, msg);

            logger.info("==== Fin de traitement /send-push-notif ====");
            return ResponseEntity.ok().build();


        } catch (Exception e) {
            logger.error("Erreur lors de l'envoi de la notification : {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }

    }
}
