package com.ecommerce.praticboutic_backend_java.services;

import com.ecommerce.praticboutic_backend_java.requests.ClientPropertyRequest;
import com.ecommerce.praticboutic_backend_java.utils.Utils;
import com.ecommerce.praticboutic_backend_java.entities.Client;
import com.ecommerce.praticboutic_backend_java.entities.Customer;
import com.ecommerce.praticboutic_backend_java.exceptions.DatabaseException;
import com.ecommerce.praticboutic_backend_java.repositories.ClientRepository;
import com.ecommerce.praticboutic_backend_java.repositories.CustomerRepository;
import com.ecommerce.praticboutic_backend_java.requests.BuildBouticRequest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Service
@Transactional
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private SessionService sessionService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Déclarez le logger en tant que champ statique en haut de votre classe
    private static final Logger logger = LoggerFactory.getLogger(ClientService.class);



    public Client authenticate(String email, String password) {
        Optional<Client> optionalClient = clientRepository.findByEmail(email);
        if (optionalClient.isPresent()) {
            Client client = optionalClient.get();
            if (passwordEncoder.matches(password, client.getPass())) {
                return client;
            }
        }
        return null;
    }
    

    public Optional<Client> findById(Integer clientId) {
        return clientRepository.findClientById(clientId);
    }
    

    public Client findByEmail(String email) {
        return clientRepository.findByEmail(email).orElse(null);
    }
    

    public Client save(Client client, boolean encodePassword) {
        if (encodePassword && client.getPass() != null) {
            client.setPass(passwordEncoder.encode(client.getPass()));
        }
        return clientRepository.save(client);
    }
    

    public boolean emailExists(String email) {
        return clientRepository.findByEmail(email).isPresent();
    }
    

    public Client updateClient(Integer clientId, Client updatedClient) throws Exception {
        Optional<Client> client = clientRepository.findClientById(clientId);
        if (client.isEmpty())
            throw new Exception ("Le client n'existe pas");

            
            if (updatedClient.getNom() != null) {
                client.get().setNom(updatedClient.getNom());
            }
            if (updatedClient.getPrenom() != null) {
                client.get().setPrenom(updatedClient.getPrenom());
            }
            if (updatedClient.getAdr1() != null) {
                client.get().setAdr1(updatedClient.getAdr1());
            }
            if (updatedClient.getAdr2() != null) {
                client.get().setAdr2(updatedClient.getAdr2());
            }
            if (updatedClient.getCp() != null) {
                client.get().setCp(updatedClient.getCp());
            }
            if (updatedClient.getVille() != null) {
                client.get().setVille(updatedClient.getVille());
            }
            if (updatedClient.getTel() != null) {
                client.get().setTel(updatedClient.getTel());
            }
            
            return clientRepository.save(client.get());


    }

    public List<?> getClientInfo(String strCustomer) throws Exception {
        Customer customer = customerRepository.findByCustomer(strCustomer);
        Optional <Client> client = clientRepository.findClientById(customer.getCltid());
        if (!client.isPresent())
            throw new Exception ("Le client n'existe pas");

        return List.of(customer.getCustomId(),customer.getNom(), customer.getNom() + " " + client.get().getAdr1() + " " + client.get().getAdr2() + " " + client.get().getCp() + " " + client.get().getVille(), customer.getLogo()  );

    }


    public Client createAndSaveClient(BuildBouticRequest input, String token)
            throws DatabaseException.EmailAlreadyExistsException, DataAccessException {

        Map <java.lang.String, java.lang.Object> payload = JwtService.parseToken(token).getClaims();
        // Récupération et validation de l'email
        Object verifyEmail = payload.get("verify_email");
        if (verifyEmail == null) {
            throw new DatabaseException.InvalidSessionDataException("L'email ne peut pas être vide");
        }

        // Vérification de l'unicité de l'email
        Long existingClientCount = clientRepository.countByEmail(verifyEmail.toString());
        if (existingClientCount > 0) {
            throw new DatabaseException.EmailAlreadyExistsException("Ce courriel est déjà utilisé: " + verifyEmail);
        }

        // Création du client avec hachage sécurisé du mot de passe
        Client client = new Client();
        client.setEmail(verifyEmail.toString());

        String password = payload.get("registration_pass").toString();
        if (StringUtils.isEmpty(password)) {
            throw new DatabaseException.InvalidSessionDataException("Le mot de passe ne peut pas être vide");
        }

        client.setPass(passwordEncoder.encode(password));

        // Remplissage des autres informations client
        client.setQualite(payload.get("registration_qualite").toString());
        client.setNom(payload.get("registration_nom").toString());
        client.setPrenom(payload.get("registration_prenom").toString());
        client.setAdr1(payload.get("registration_adr1").toString());
        client.setAdr2(payload.get("registration_adr2").toString());
        client.setCp(payload.get("registration_cp").toString());
        client.setVille(payload.get("registration_ville").toString());
        client.setTel(payload.get("registration_tel").toString());
        client.setStripeCustomerId(payload.get("registration_stripe_customer_id").toString());
        client.setActif(1);
        client.setDevice_id(Utils.sanitizeInput(input.getDeviceId()));
        client.setDevice_type(Utils.sanitizeInput(input.getDeviceType().toString()));

        try {
            return clientRepository.save(client);
        } catch (DataAccessException e) {
            logger.error("Erreur lors de la sauvegarde du client", e);
            throw new DataAccessException("Erreur lors de la sauvegarde du client", e) {};
        }
    }

    public String getValeur(String paramName, Integer bouticId) {
        // Création de la requête JPQL
        String sql = "SELECT cl." + paramName + " FROM customer c " +
                "JOIN client cl ON c.cltid = cl.cltid " +
                "WHERE c.customid = ?";

        List<String> result = jdbcTemplate.query(
                sql,
                new Object[]{bouticId}, // uniquement le paramètre pour customid
                (rs, rowNum) -> rs.getString(paramName) // récupérer la colonne dynamique
        );

        if (!result.isEmpty()) {
            return result.get(0);
        } else {
            return "";
        }
    }
}