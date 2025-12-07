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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AbonnementServiceTest {

    @Mock
    private AbonnementRepository abonnementRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private AbonnementService abonnementService;

    @Test
    @DisplayName("createAndSaveAbonnement - happy path")
    void createAndSaveAbonnement_happyPath() {
        String token = "jwt-token";

        // Mock client
        Client client = new Client();
        client.setCltId(10);

        // Mock customer
        Customer customer = new Customer();
        customer.setCustomId(20);

        // Mock abonnement created by repository
        Abonnement savedAbo = new Abonnement();
        savedAbo.setAboId(99);
        savedAbo.setCltId(10);
        savedAbo.setBouticId(20);
        savedAbo.setStripeSubscriptionId("sub_123");
        savedAbo.setActif(1);

        when(abonnementRepository.save(any())).thenReturn(savedAbo);

        // Mock JWT payload
        JwtPayload mockPayload = mock(JwtPayload.class);
        when(mockPayload.getClaims()).thenReturn(Map.of(
                "creationabonnement_stripe_subscription_id", "sub_123"
        ));

        try (MockedStatic<JwtService> jwt = mockStatic(JwtService.class)) {
            jwt.when(() -> JwtService.parseToken(token)).thenReturn(mockPayload);

            Abonnement result = abonnementService.createAndSaveAbonnement(client, customer, token);

            assertNotNull(result);
            assertEquals(99, result.getAboId());
            assertEquals(10, result.getCltId());
            assertEquals(20, result.getBouticId());
            assertEquals("sub_123", result.getStripeSubscriptionId());
        }
    }

    @Test
    @DisplayName("findById - retourne l'abonnement quand existe")
    void findById_returnsAbonnement_whenExists() {

        Abonnement abo = new Abonnement();
        abo.setAboId(5);

        when(abonnementRepository.findById(5))
                .thenReturn(Optional.of(abo));

        Abonnement result = abonnementService.findById(5);

        assertNotNull(result);
        assertEquals(5, result.getAboId());
    }


    @Test
    @DisplayName("findById - retourne null quand absent")
    void findById_returnsNull_whenNotFound() {
        when(abonnementRepository.findById(any())).thenReturn(Optional.empty());

        Abonnement result = abonnementService.findById(-1);

        assertNull(result);
    }

    @Test
    @DisplayName("save - persiste et retourne l'entité")
    void save_persistsEntity() {
        Abonnement abo = new Abonnement();
        abo.setActif(1);

        Abonnement saved = new Abonnement();
        saved.setAboId(100);
        saved.setActif(1);

        when(abonnementRepository.save(abo)).thenReturn(saved);

        Abonnement result = abonnementService.save(abo);

        assertEquals(100, result.getAboId());
        assertEquals(1, result.getActif());
    }
}

