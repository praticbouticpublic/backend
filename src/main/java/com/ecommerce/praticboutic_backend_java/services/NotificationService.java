package com.ecommerce.praticboutic_backend_java.services;

import com.google.auth.oauth2.GoogleCredentials;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.messaging.*;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Subscription;
import com.stripe.model.SubscriptionItem;
import com.stripe.model.UsageRecord;

import com.stripe.net.RequestOptions;
import com.stripe.param.UsageRecordCreateOnSubscriptionItemParams;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class NotificationService {

    @Value("${stripe.secret.key}")
    private String stripeApiKey;
    
    private final RestTemplate restTemplate = new RestTemplate();

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @Value("${app.base-url}")
    private String rootUrlBack;

    @Value("${app.root.url.front}")
    private String rootUrlFront;

    @Autowired
    FirebaseMessaging messaging;

    /**
     * Envoie une notification push à un appareil spécifique en utilisant Firebase Cloud Messaging API v1
     *
     * @param deviceId L'identifiant unique de l'appareil destinataire (token FCM)
     * @param title Titre de la notification
     * @param body Corps du message de la notification
     * @return L'ID du message envoyé ou null en cas d'échec
     */
    public String sendPushNotification(String deviceId, String title, String body) {
        try {
            if (deviceId == null || deviceId.isEmpty()) {
                logger.error("Impossible d'envoyer la notification: deviceId est null ou vide");
                return null;
            }

            // Créer l'objet Notification avec titre et corps
            Notification notification = Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build();

            // Config spécifique pour Android
            AndroidConfig androidConfig = AndroidConfig.builder()
                    .setNotification(AndroidNotification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .setSound("default")
                            .setChannelId("default_channel") // Important pour Android 8+
                            .build())
                    .build();

            // Config spécifique pour iOS (APNs)
            ApnsConfig apnsConfig = ApnsConfig.builder()
                    .setAps(Aps.builder()
                            .setAlert(ApsAlert.builder()
                                    .setTitle(title)
                                    .setBody(body)
                                    .build())
                            .setBadge(1)             // Badge sur l'icône de l'app
                            .setSound("default")     // Son par défaut
                            .build())
                    .build();

            // Config spécifique pour Web
            WebpushConfig webpushConfig = WebpushConfig.builder()
                    .setNotification(WebpushNotification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .setIcon(rootUrlBack + "img/logo-pratic-boutic.png")
                            .build())
                    .build();

            Message message = Message.builder()
                    .setToken(deviceId)
                    .setNotification(notification)
                    .setApnsConfig(apnsConfig)        // Pour iOS
                    .setAndroidConfig(androidConfig)  // ✅ Pour Android
                    .setWebpushConfig(webpushConfig)  // ✅ Pour Web
                    .build();

            // Envoyer le message et récupérer l'ID du message
            String messageId = FirebaseMessaging.getInstance().send(message);
            logger.info("Notification envoyée avec succès à deviceId: {}, messageId: {}", deviceId, messageId);
            return messageId;

        } catch (FirebaseMessagingException e) {
            logger.error("Erreur lors de l'envoi de notification à deviceId: {}", deviceId, e);

            // Gérer les erreurs spécifiques comme token invalide, quota dépassé, etc.
            if (e.getMessagingErrorCode() == MessagingErrorCode.INVALID_ARGUMENT ||
                    e.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED) {
                // Token invalide ou appareil désinscrit
                logger.warn("Token FCM invalide détecté pour deviceId: {}", deviceId);
            }

            return null;
        } catch (Exception e) {
            logger.error("Exception inattendue lors de l'envoi de notification à deviceId: {}", deviceId, e);
            return null;
        }
    }


    /**
     * Récupère le token FCM associé à un deviceId particulier
     *
     * @param deviceId L'identifiant de l'appareil
     * @return Le token FCM associé ou null si non trouvé
     */
    private String getTokenFromDeviceId(String deviceId) throws FirebaseAuthException {
        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(deviceId);
        return decodedToken.getUid();
    }

    /**
     * Gère les différentes erreurs FCM et prend les mesures appropriées
     */
    private void handleFCMError(String deviceId, FirebaseMessagingException e) {
        switch (e.getMessagingErrorCode()) {
            case INVALID_ARGUMENT:
                // Message mal formé
                logger.error("Message mal formé pour deviceId: {}", deviceId);
                break;
            case UNREGISTERED:
                // Token n'est plus valide, il faut le supprimer
                logger.warn("Token FCM expiré pour deviceId: {}, suppression du token", deviceId);
                removeInvalidToken(deviceId);
                break;
            case SENDER_ID_MISMATCH:
                logger.error("Problème de configuration FCM: sender ID ne correspond pas");
                break;
            case QUOTA_EXCEEDED:
                logger.error("Quota FCM dépassé, implémentez une logique de retry avec backoff");
                // Implémentation d'une stratégie de retry possible ici
                break;
            default:
                logger.error("Erreur FCM non spécifique: {}", e.getMessage());
        }
    }

    /**
     * Supprime un token invalide de la base de données
     */
    private void removeInvalidToken(String deviceId) {
        // Implémentez la logique pour marquer le token comme invalide ou le supprimer
        // Par exemple:
        // deviceRepository.removeTokenForDeviceId(deviceId);
    }

    
    public void sendSms(String phoneNumber, String content, String sender, String token) {
        Map<String, Object> recipient = new HashMap<>();
        recipient.put("value", phoneNumber);
        
        Map<String, Object> recipientsList = new HashMap<>();
        recipientsList.put("gsm", new Object[]{recipient});
        
        Map<String, Object> messageContent = new HashMap<>();
        messageContent.put("text", content);
        messageContent.put("sender", sender);
        
        Map<String, Object> smsData = new HashMap<>();
        smsData.put("message", messageContent);
        smsData.put("recipients", recipientsList);
        
        Map<String, Object> payload = new HashMap<>();
        payload.put("sms", smsData);
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Accept", "application/json");
        headers.set("Authorization", "Bearer " + token);
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
        
        restTemplate.postForObject("https://api.smsfactor.com/send", request, String.class);
    }
    
    public void reportStripeUsage(String subscriptionId, int usageQuantity, String idempotencyKey) throws StripeException {
        Stripe.apiKey = stripeApiKey;
        
        Subscription subscription = Subscription.retrieve(subscriptionId);
        
        for (SubscriptionItem item : subscription.getItems().getData()) {
            String usageType = item.getPlan().getUsageType();
            if ("metered".equals(usageType)) {

                SubscriptionItem subscriptionItem = SubscriptionItem.retrieve(subscriptionId);

                UsageRecordCreateOnSubscriptionItemParams params =
                        UsageRecordCreateOnSubscriptionItemParams.builder()
                                .setQuantity((long) usageQuantity)
                                .setTimestamp(Instant.now().getEpochSecond())
                                .setAction(UsageRecordCreateOnSubscriptionItemParams.Action.INCREMENT)
                                .build();

                RequestOptions options =
                        RequestOptions.builder()
                                .setIdempotencyKey(idempotencyKey)
                                .build();

                UsageRecord.createOnSubscriptionItem(subscriptionItem.getId(), params, options);
                break;
            }
        }
    }

    public void FirebaseNotificationSender(String credentialsPath) throws IOException {

        FileInputStream serviceAccount = new FileInputStream(credentialsPath);

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }

        messaging = FirebaseMessaging.getInstance();
    }

    public void sendNotification(String deviceId, int deviceType) {
        String title = "Nouvelle(s) commande(s) dans votre Praticboutic";
        String body = "Commande(s) en attente de validation";
        String imageUrl = rootUrlFront + "assets/img/logo-pratic-boutic.png";

        String icon = rootUrlBack + "img/pb_notificon.png";
        String link = rootUrlFront + "pushstart";

        Message message = null;

        try {
            // Version simplifiée comme dans le code PHP final qui n'utilise pas les conditions deviceType
            message = Message.builder()
                    .setToken(deviceId)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .setImage(icon)
                            .build())
                    .build();

            String response = messaging.send(message);
            logger.info("Message envoyé avec succès: {}", response);

        } catch (FirebaseMessagingException e) {
            logger.error("Erreur d'envoi: {}", e.getMessage(), e);

            if (e.getMessagingErrorCode() == MessagingErrorCode.UNAVAILABLE) {
                // Implémentation de la logique de retry comme dans le code PHP
                int retryAfterSeconds = 60; // Valeur par défaut si l'en-tête n'est pas présent

                try {
                    // Attente avant de réessayer (en production, utilisez un système de file d'attente)
                    Thread.sleep(retryAfterSeconds * 1000L);
                    messaging.send(message);
                } catch (InterruptedException | FirebaseMessagingException ex) {
                    logger.error("Échec de la nouvelle tentative: " + ex.getMessage(), ex);
                }
            }
        } catch (Exception e) {
            logger.error("Erreur générale: {}", e.getMessage(), e);
        }

    }






}

