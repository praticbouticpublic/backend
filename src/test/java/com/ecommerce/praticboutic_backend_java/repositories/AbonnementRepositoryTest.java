package com.ecommerce.praticboutic_backend_java.repositories;

import com.ecommerce.praticboutic_backend_java.entities.Abonnement;
import com.ecommerce.praticboutic_backend_java.entities.Client;
import com.ecommerce.praticboutic_backend_java.entities.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@EntityScan("com.ecommerce.praticboutic_backend_java.entities")
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.show-sql=true"
})

class AbonnementRepositoryTest {

    @Autowired
    private AbonnementRepository abonnementRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private CustomerRepository customerRepository;

    private Abonnement abonnement1;
    private Abonnement abonnement2;

    @BeforeEach
    void setUp() {
        // Nettoyage de toutes les tables avant chaque test
        abonnementRepository.deleteAll();
        customerRepository.deleteAll();
        clientRepository.deleteAll();

        // Création des clients
        Client client1 = new Client();
        client1.setNom("Client 1");
        clientRepository.save(client1);

        Client client2 = new Client();
        client2.setNom("Client 2");
        clientRepository.save(client2);

        // Création des customers
        Customer customer1 = new Customer();
        customer1.setNom("Customer 1");
        customer1.setClient(client1);
        customerRepository.save(customer1);

        Customer customer2 = new Customer();
        customer2.setNom("Customer 2");
        customer2.setClient(client2);
        customerRepository.save(customer2);

        // Création des abonnements
        abonnement1 = new Abonnement();
        abonnement1.setCltId(client1.getCltId());
        abonnement1.setDateDebut(LocalDate.now());
        abonnement1.setDateFin(LocalDate.now().plusMonths(1));
        abonnement1.setActif(1);
        abonnement1.setBouticId(customer1.getCustomId());
        abonnement1.setCreationBoutic(true);
        abonnement1.setStripeSubscriptionId("stripetest");

        abonnement2 = new Abonnement();
        abonnement2.setCltId(client2.getCltId());
        abonnement2.setDateDebut(LocalDate.now().minusMonths(1));
        abonnement2.setDateFin(LocalDate.now());
        abonnement2.setActif(1);
        abonnement2.setBouticId(customer2.getCustomId());
        abonnement2.setCreationBoutic(true);
        abonnement2.setStripeSubscriptionId("stripetest");

        abonnementRepository.save(abonnement1);
        abonnementRepository.save(abonnement2);
    }

    @Test
    void testFindAll() {
        List<Abonnement> abonnements = abonnementRepository.findAll();
        assertThat(abonnements).hasSize(2);
    }

    @Test
    void testFindByCltIdNoResults() {
        List<Abonnement> result = abonnementRepository.findByCltId(9999);
        assertThat(result).isEmpty();
    }

    @Test
    void testFindByCltIdExisting() {
        List<Abonnement> result = abonnementRepository.findByCltId(abonnement1.getCltId());
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCltId()).isEqualTo(abonnement1.getCltId());
    }
}
