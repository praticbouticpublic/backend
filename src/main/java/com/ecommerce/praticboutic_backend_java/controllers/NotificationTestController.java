package com.ecommerce.praticboutic_backend_java.controllers;

/*import com.ecommerce.praticboutic_backend_java.services.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class NotificationTestController {

    private static final Logger logger = LoggerFactory.getLogger(NotificationTestController.class);

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/test/send")
    public ResponseEntity<NotificationResponse> sendTestNotification(
            @RequestBody NotificationRequest request) {

        logger.info("Demande d'envoi de notification de test reçue pour deviceId: {}", request.getDevice_id());

        if (request.getDevice_id() == null || request.getDevice_id().isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(new NotificationResponse(false, "Le deviceId est requis", null));
        }

        try {
            String messageId = notificationService.sendPushNotification(
                    request.getDevice_id(),
                    request.getTitle() != null ? request.getTitle() : "Notification de test",
                    request.getBody() != null ? request.getBody() : "Ceci est une notification de test"
            );

            if (messageId != null) {
                logger.info("Notification de test envoyée avec succès, messageId: {}", messageId);
                return ResponseEntity
                        .ok(new NotificationResponse(true, "Notification envoyée avec succès", messageId));
            } else {
                logger.warn("Échec de l'envoi de notification de test");
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new NotificationResponse(false, "Échec de l'envoi de la notification", null));
            }
        } catch (Exception e) {
            logger.error("Exception lors de l'envoi de la notification de test", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new NotificationResponse(false, "Erreur: " + e.getMessage(), null));
        }
    }

    @GetMapping("/test/device/{deviceId}")
    public ResponseEntity<NotificationResponse> sendSimpleTestNotification(
            @PathVariable String deviceId) {

        logger.info("Demande d'envoi de notification simple à deviceId: {}", deviceId);

        try {
            String messageId = notificationService.sendPushNotification(
                    deviceId,
                    "Test rapide",
                    "Notification de test rapide envoyée à " + deviceId
            );

            if (messageId != null) {
                return ResponseEntity
                        .ok(new NotificationResponse(true, "Notification de test envoyée", messageId));
            } else {
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new NotificationResponse(false, "Échec de l'envoi de la notification", null));
            }
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new NotificationResponse(false, "Erreur: " + e.getMessage(), null));
        }
    }

    // Classes DTO internes pour simplifier l'utilisation
    public static class NotificationRequest {
        private String deviceId;
        private String title;
        private String body;

        // Constructeurs
        public NotificationRequest() {}

        public NotificationRequest(String deviceId, String title, String body) {
            this.deviceId = deviceId;
            this.title = title;
            this.body = body;
        }

        // Getters et Setters
        public String getDevice_id() {
            return deviceId;
        }

        public void setDevice_id(String deviceId) {
            this.deviceId = deviceId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }
    }

    public static class NotificationResponse {
        private boolean success;
        private String message;
        private String messageId;

        // Constructeurs
        public NotificationResponse() {}

        public NotificationResponse(boolean success, String message, String messageId) {
            this.success = success;
            this.message = message;
            this.messageId = messageId;
        }

        // Getters et Setters
        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getMessageId() {
            return messageId;
        }

        public void setMessageId(String messageId) {
            this.messageId = messageId;
        }
    }
}*/