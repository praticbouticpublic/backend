package com.ecommerce.praticboutic_backend_java.services;


import com.ecommerce.praticboutic_backend_java.entities.Client;
import com.ecommerce.praticboutic_backend_java.repositories.ClientRepository;
import com.ecommerce.praticboutic_backend_java.repositories.ConnexionRepository;
import com.ecommerce.praticboutic_backend_java.exceptions.ClientNotFoundException;
import com.ecommerce.praticboutic_backend_java.exceptions.TooManyRequestsException;
import com.ecommerce.praticboutic_backend_java.services.EmailService;
import com.ecommerce.praticboutic_backend_java.entities.Connexion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class MotDePasseService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ConnexionRepository connexionRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.reset.password.max-retry}")
    private int maxRetry;

    @Value("${app.reset.password.interval}")
    private int intervalMinutes;


    /**
     * Génère un mot de passe sécurisé
     *
     * @return le mot de passe généré
     */
    public String genererMotDePasseSecurise() {
        String minuscules = "abcdefghjkmnpqrstuvwxyz";
        String majuscules = "ABCDEFGHJKMNPQRSTUVWXYZ";
        String chiffres = "23456789";
        String speciaux = "!@#$%&*?";

        StringBuilder password = new StringBuilder();
        Random random = new Random();

        // Au moins un caractère de chaque type
        password.append(minuscules.charAt(random.nextInt(minuscules.length())));
        password.append(majuscules.charAt(random.nextInt(majuscules.length())));
        password.append(chiffres.charAt(random.nextInt(chiffres.length())));
        password.append(speciaux.charAt(random.nextInt(speciaux.length())));

        // Ajouter des caractères aléatoires jusqu'à atteindre la longueur souhaitée
        String allChars = minuscules + majuscules + chiffres + speciaux;
        for (int i = 4; i < 9; i++) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }

        // Mélanger les caractères
        char[] passwordArray = password.toString().toCharArray();
        for (int i = 0; i < passwordArray.length; i++) {
            int j = random.nextInt(passwordArray.length);
            char temp = passwordArray[i];
            passwordArray[i] = passwordArray[j];
            passwordArray[j] = temp;
        }

        return new String(passwordArray);
    }

    /**
     * Réinitialise le mot de passe d'un utilisateur et envoie un email
     *
     * @param email     L'email du client
     * @param ipAddress L'adresse IP du client
     * @return true si la réinitialisation a réussi
     * @throws ClientNotFoundException  si le client n'existe pas
     * @throws TooManyRequestsException si trop de tentatives ont été effectuées
     */
    public boolean reinitialiserMotDePasse(String email, String ipAddress)
            throws Exception {

        // Vérifier les limites de tentatives
        int count = connexionRepository.countByIpAndTsAfter(
                ipAddress,
                LocalDateTime.now().minusMinutes(intervalMinutes));

        if (count >= maxRetry) {
            throw new TooManyRequestsException("Vous êtes autorisé à " + maxRetry +
                    " tentative(s) en " + intervalMinutes + " minutes");
        }

        // Chercher le client par email
        Optional<Client> client = clientRepository.findByEmailAndActif(email , 1);
        if (client.isEmpty())
            throw new Exception("Aucun client trouvé");


        // Générer un nouveau mot de passe
        String nouveauMotDePasse = genererMotDePasseSecurise();

        // Mettre à jour le mot de passe du client
        client.get().setPass(passwordEncoder.encode(nouveauMotDePasse));
        clientRepository.save(client.get());

        // Enregistrer la tentative
        Connexion connexion = new Connexion();
        connexion.setIp(ipAddress);
        connexion.setTs(LocalDateTime.now());
        connexionRepository.save(connexion);

        // Envoyer l'email
        emailService.envoyerEmailReinitialisationMotDePasse(email, nouveauMotDePasse);

        return true;
    }

}