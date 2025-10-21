package com.ecommerce.praticboutic_backend_java.services;

import com.ecommerce.praticboutic_backend_java.entities.Abonnement;
import com.ecommerce.praticboutic_backend_java.entities.Client;
import com.ecommerce.praticboutic_backend_java.entities.Customer;
import com.ecommerce.praticboutic_backend_java.models.JwtPayload;
import com.ecommerce.praticboutic_backend_java.repositories.AbonnementRepository;
import com.ecommerce.praticboutic_backend_java.repositories.ClientRepository;
import com.ecommerce.praticboutic_backend_java.repositories.CustomerRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
class AbonnementServiceTest {

    @Autowired
    private AbonnementService abonnementService;

    @Autowired
    private AbonnementRepository abonnementRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    @DisplayName("createAndSaveAbonnement - happy path")
    void createAndSaveAbonnement_happyPath() {
        String token = "jwt-token";

        // 🔹 Crée et sauvegarde le client
        Client client = new Client();
        client.setNom("Client Test");
        client.setEmail("client@test.com");
        client.setActif(1);
        client = clientRepository.save(client);

        // 🔹 Crée et sauvegarde le customer
        Customer customer = new Customer();
        customer.setNom("Boutique Test");
        customer.setCourriel("contact@test.com");
        customer.setCustomer("boutique-test");
        customer.setActif(1);
        customer = customerRepository.save(customer);

        // 🔹 Mock du JwtPayload
        JwtPayload mockPayload = mock(JwtPayload.class);
        when(mockPayload.getClaims()).thenReturn(Map.of(
                "creationabonnement_stripe_subscription_id", "sub_123"
        ));

        // 🔹 Mock statique de JwtService
        try (MockedStatic<JwtService> mockedJwt = mockStatic(JwtService.class)) {
            mockedJwt.when(() -> JwtService.parseToken(token)).thenReturn(mockPayload);

            // 🔹 Appel de la méthode
            Abonnement result = abonnementService.createAndSaveAbonnement(client, customer, token);

            // 🔹 Assertions
            assertNotNull(result, "L'abonnement ne doit pas être null");
            assertNotNull(result.getAboId(), "L'ID de l'abonnement doit être généré");
            assertEquals(client.getCltId(), result.getCltId(), "L'abonnement doit être lié au bon client");
            assertEquals(customer.getCustomId(), result.getBouticId(), "L'abonnement doit être lié à la bonne boutique");
            assertEquals("sub_123", result.getStripeSubscriptionId(), "Le Stripe Subscription ID doit être correct");
            assertEquals(1, result.getActif(), "L'abonnement doit être actif");
        }
    }

    @Test
    @DisplayName("findById - retourne l'abonnement quand existe")
    void findById_returnsAbonnement_whenExists() {

        // 🔹 Crée et sauvegarde le client
        Client client = new Client();
        client.setNom("Client Test 1");
        client.setEmail("client@test.com");
        client.setActif(1);
        client = clientRepository.save(client);

        // 🔹 Crée et sauvegarde le customer
        Customer customer = new Customer();
        customer.setNom("Boutique Test 1");
        customer.setCltid(client.getCltId());
        customer = customerRepository.save(customer);

        // 🔹 Crée l'abonnement en liant correctement le client et le customer
        Abonnement abo = new Abonnement();
        abo.setActif(1);
        abo.setCltId(client.getCltId());
        abo.setBouticId(customer.getCustomId());  // ✅ utiliser l'ID réel du customer
        abo.setCreationBoutic(false);
        abo.setStripeSubscriptionId("sub_123");
        abo = abonnementRepository.save(abo);

        // 🔹 Test
        Abonnement result = abonnementService.findById(abo.getAboId());
        assertNotNull(result);
        assertEquals(abo.getAboId(), result.getAboId());
    }


    @Test
    @DisplayName("findById - retourne null quand absent")
    void findById_returnsNull_whenNotFound() {
        Abonnement result = abonnementService.findById(-1);
        assertNull(result);
    }

    @Test
    @DisplayName("save - persiste et retourne l'entité")
    void save_persistsEntity() {
        // 🔹 Crée et sauvegarde le client
        Client client = new Client();
        client.setNom("Client Test 1");
        client.setEmail("client@test.com");
        client.setActif(1);
        client = clientRepository.save(client);

        Customer customer = new Customer();
        customer.setNom("Boutique Test 1");
        customer.setCltid(client.getCltId());
        customer = customerRepository.save(customer);


        Abonnement abo = new Abonnement();
        abo.setActif(1);
        abo.setBouticId(customer.getCustomId());
        abo.setCltId(client.getCltId());
        abo.setCreationBoutic(false);
        abo.setStripeSubscriptionId("sub_123");


        Abonnement saved = abonnementService.save(abo);
        assertNotNull(saved.getAboId());
        assertEquals(1, saved.getActif());
    }

}
