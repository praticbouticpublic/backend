package com.ecommerce.praticboutic_backend_java.controllers;


import com.ecommerce.praticboutic_backend_java.repositories.ClientRepository;
import com.ecommerce.praticboutic_backend_java.services.EmailService;
import com.ecommerce.praticboutic_backend_java.utils.Utils;
import com.ecommerce.praticboutic_backend_java.entities.Identifiant;
import com.ecommerce.praticboutic_backend_java.repositories.IdentifiantRepository;
import com.ecommerce.praticboutic_backend_java.requests.SendCodeRequest;
import com.ecommerce.praticboutic_backend_java.responses.ErrorResponse;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class SendCodeController {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    IdentifiantRepository identifiantRepository;

    @Autowired
    ClientRepository clientRepository;

    @Value("${application.url}")
    private String applicationUrl;

    @Value("${app.mail.from.address}")
    private String fromEmail;

    @Value("${app.mail.from.name}")
    private String fromName;

    @Value("${identification.key}")
    private String idkey;

    // Déclarez le logger en tant que champ statique en haut de votre classe
    private static final Logger logger = LoggerFactory.getLogger(SendCodeController.class);

    @PostMapping("/send-code")
    public ResponseEntity<?> sendVerificationCode(@RequestBody SendCodeRequest request) {
        try {
            // Vérifier que l'e-mail n'existe pas déjà dans la table client

            if (clientRepository.existsByEmail(request.getEmail())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("error", "Le courriel " + request.getEmail() + " est déjà attribué à un client. Impossible de continuer."));
            }

            // Générer un code à 6 chiffres
            SecureRandom secureRandom = new SecureRandom();
            int verificationCode = secureRandom.nextInt(999999);
            String formattedCode = String.format("%06d", verificationCode);

            // Générer un hash aléatoire (comme le md5 dans PHP)
            String hash = DigestUtils.md5DigestAsHex(
                    (System.nanoTime() * 100000 + "").getBytes(StandardCharsets.UTF_8)
            );

            // Stocker en base : email + hash + actif = 0
            Identifiant identifiant = new Identifiant(request.getEmail(), hash, 0);
            identifiantRepository.save(identifiant);

            // Chiffrer le code avec AES-256-CBC
            byte[] iv = new byte[16];
            secureRandom.nextBytes(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            byte[] keyBytes = Hex.decodeHex(idkey); // doit être 32 octets pour AES-256
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);

            byte[] encrypted = cipher.doFinal(formattedCode.getBytes(StandardCharsets.UTF_8));
            String encryptedCode = Base64.getEncoder().encodeToString(encrypted);
            String encodedIv = Base64.getEncoder().encodeToString(iv);

            // Envoyer le code en clair par e-mail
            sendEmail(request.getEmail(), formattedCode);

            // Retourner le code chiffré + IV (comme en PHP)
            return ResponseEntity.ok(new String[]{encryptedCode, encodedIv});

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    private void sendEmail(String recipientEmail, String verificationCode) throws MessagingException, UnsupportedEncodingException {
        StringBuilder text = new StringBuilder();
        InputStream inputStream = EmailService.class.getClassLoader().getResourceAsStream("./static/logopbsvg.html");
        String logopb = "";
        if (inputStream != null) {
            try {
                logopb = new String(inputStream.readAllBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            logger.error("InputStream est null !");
        }
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        String subject = "Votre code confidentiel";
        String htmlContent = "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta charset=\"UTF-8\">" +
                "<link href='https://fonts.googleapis.com/css?family=Public+Sans' rel='stylesheet'>" +
                "</head>" +
                "<body>" +
                logopb +
                "<p>Bonjour,</p>" +
                "<p>Voici le code de vérification : " + verificationCode + "</p>" +
                "<p>Cordialement,<br>L'équipe Praticboutic</p>" +
                "</body>" +
                "</html>";

        helper.setFrom(fromEmail, fromName);
        helper.setTo(recipientEmail);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }
}
