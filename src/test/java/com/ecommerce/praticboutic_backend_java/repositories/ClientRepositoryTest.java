package com.ecommerce.praticboutic_backend_java.repositories;

import com.ecommerce.praticboutic_backend_java.entities.Client;
import com.ecommerce.praticboutic_backend_java.entities.Customer;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ClientRepositoryTest {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private EntityManager em;

    private Client client;
    private Customer customer;

    @BeforeEach
    void setUp() {
        // Supprime tout pour être sûr
        customerRepository.deleteAll();
        clientRepository.deleteAll();

        // Création et sauvegarde du client
        client = new Client();
        client.setEmail("user@example.com");
        client.setActif(1);
        client.setNom("Client1");
        client = clientRepository.saveAndFlush(client); // 🔹 flush pour générer l'ID

        // Création et sauvegarde du customer lié
        customer = new Customer();
        customer.setNom("Customer1");
        customer.setClient(client); // ⚡ lien vers client persistant
        customer = customerRepository.saveAndFlush(customer); // 🔹 flush
    }

    @Test
    void testCountByEmail() {

        Long count = clientRepository.countByEmail("user@example.com");
        assertEquals(1L, count);

        count = clientRepository.countByEmail("nonexistent@example.com");
        assertEquals(0L, count);
    }

    @Test
    @Transactional
    void testUpdateEmailById() {
        // Création et sauvegarde du client
        client = new Client();
        client.setEmail("user@example.com");
        client.setActif(1);
        client.setNom("Client1");
        client = clientRepository.save(client);

        // Met à jour son email
        clientRepository.updateEmailById("newemail@example.com", client.getCltId());

        em.flush();
        em.clear();

        // Vérifie le résultat
        Optional<Client> updated = clientRepository.findById(client.getCltId());
        assertTrue(updated.isPresent());
        assertEquals("newemail@example.com", updated.get().getEmail());
    }

    @Test
    void testFindByEmailAndActif() {

        Optional<Client> found = clientRepository.findByEmailAndActif("user@example.com", 1);
        assertTrue(found.isPresent());
        assertEquals(client.getCltId(), found.get().getCltId());

        found = clientRepository.findByEmailAndActif("user@example.com", 0);
        assertTrue(found.isEmpty());
    }

    @Test
    void testFindByEmail() {

        Optional<Client> found = clientRepository.findByEmail("user@example.com");
        assertTrue(found.isPresent());
        assertEquals(client.getCltId(), found.get().getCltId());

        found = clientRepository.findByEmail("nonexistent@example.com");
        assertTrue(found.isEmpty());
    }

    @Test
    void testFindByCustomer() {

        Client found = clientRepository.findByCustomer(customer);
        assertNotNull(found);
        assertEquals(client.getCltId(), found.getCltId());
    }

    @Test
    void testFindClientById() {

        Optional<Client> found = clientRepository.findClientById(client.getCltId());
        assertTrue(found.isPresent());
        assertEquals(client.getEmail(), found.get().getEmail());

        found = clientRepository.findClientById(999);
        assertTrue(found.isEmpty());
    }

    @Test
    void testExistsByEmail() {
        assertTrue(clientRepository.existsByEmail("user@example.com"));
        assertFalse(clientRepository.existsByEmail("nonexistent@example.com"));
    }
}
