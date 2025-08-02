package com.ecommerce.praticboutic_backend_java.services;

import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import jakarta.mail.Message.*;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;


@Service
public class EmailService
{

    @Value("${app.mail.from.address}")
    private String fromEmail;
    
    @Value("${app.mail.from.name}")
    private String fromName;
    
    @Value("${app.base-url}")
    private String baseUrl;

    private Session session;

    @Autowired
    private JavaMailSender mailSender;

    // Déclarez le logger en tant que champ statique en haut de votre classe
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    /**
     * Envoie un email de réinitialisation de mot de passe
     * @param toEmail adresse email du destinataire
     * @param newPassword nouveau mot de passe généré
     */
    public void envoyerEmailReinitialisationMotDePasse(String toEmail, String newPassword) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail, fromName);
            helper.setTo(toEmail);
            helper.setSubject("Confidentiel");
            String htmlContent = buildResetPasswordEmail(toEmail, newPassword);
            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'envoi du mail", e);
        }
    }
    
    /**
     * Crée le contenu HTML du mail de réinitialisation
     * @param email adresse email du destinataire
     * @param password nouveau mot de passe
     * @return le contenu HTML du mail
     */
    private String buildResetPasswordEmail(String email, String password) throws IOException {
        StringBuilder content = new StringBuilder();
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
        content.append("<!DOCTYPE html>");
        content.append("<html>");
        content.append("<head>");
        content.append("<meta charset=\"UTF-8\">");
        content.append("<link href='https://fonts.googleapis.com/css?family=Public+Sans' rel='stylesheet'>");
        content.append("</head>");                
        content.append("<body>");
        content.append(logopb);
        content.append("<br><br>");
        content.append("<p style=\"font-family: 'Sans'\">Bonjour ");
        content.append(email).append("<br><br>");        
        content.append("&nbsp;&nbsp;Comme vous avez oubli&eacute; votre mot de passe praticboutic un nouveau a &eacute;t&eacute; g&eacute;n&eacute;r&eacute; automatiquement. <br>");        
        content.append("Voici votre nouveau mot de mot de passe administrateur praticboutic : ");
        content.append("<b>").append(password).append("</b><br>");
        content.append("Vous pourrez en personnaliser un nouveau &agrave; partir du formulaire client de l'arri&egrave;re boutic.<br><br>");
        content.append("Cordialement<br><br>L'&eacute;quipe praticboutic<br><br></p>");
        content.append("</body>");
        content.append("</html>");
        
        return content.toString();
    }
}