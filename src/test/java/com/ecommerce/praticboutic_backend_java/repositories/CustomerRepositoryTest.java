package com.ecommerce.praticboutic_backend_java.repositories;

import com.ecommerce.praticboutic_backend_java.entities.Client;
import com.ecommerce.praticboutic_backend_java.entities.Customer;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ClientRepository clientRepository;


    private Customer customer1;
    private Customer customer2;

    @BeforeEach
    void setUp() {

        customerRepository.deleteAll();
        Client client1 = new Client();
        client1.setNom("Client Test 1");
        client1.setEmail("client1@test.com");
        client1.setActif(1);
        clientRepository.save(client1);

        Client client2 = new Client();
        client2.setNom("Client Test 2");
        client2.setEmail("client2@test.com");
        client2.setActif(1);
        clientRepository.save(client2);

        customer1 = new Customer();
        customer1.setCltid(client1.getCltId()); // ID généré
        customer1.setCustomer("shop-1");
        customer1.setNom("Boutique 1");
        customer1.setLogo("logo1.png");
        customer1.setCourriel("contact1@example.com");
        customer1.setActif(1);
        customer1 = customerRepository.save(customer1);

        customer2 = new Customer();
        customer2.setCltid(client2.getCltId()); // ID généré
        customer2.setCustomer("shop-2");
        customer2.setNom("Boutique 2");
        customer2.setLogo("logo2.png");
        customer2.setCourriel("contact2@example.com");
        customer2.setActif(0);
        customer2 = customerRepository.save(customer2);
    }

    @Test
    void testFindByCustomer() {
        Customer found = customerRepository.findByCustomer("shop-1");
        assertNotNull(found);
        assertEquals(customer1.getNom(), found.getNom());

        found = customerRepository.findByCustomer("shop-2");
        assertNotNull(found);
        assertEquals(customer2.getNom(), found.getNom());
    }

    @Test
    void testFindByCustomid() {
        Optional<Customer> found = customerRepository.findByCustomid(customer1.getCustomId());
        assertTrue(found.isPresent());
        assertEquals(customer1.getCustomer(), found.get().getCustomer());

        found = customerRepository.findByCustomid(999);
        assertTrue(found.isEmpty());
    }

    @Test
    void testSaveCustomer() {
        // 🔹 Crée un client pour ce test
        Client client = new Client();
        client.setNom("Client Test 3");
        client.setEmail("client3@test.com");
        client.setActif(1);
        client = clientRepository.save(client); // récupère l'ID généré

        // 🔹 Crée un customer lié à ce client
        Customer newCustomer = new Customer();
        newCustomer.setNom("Boutique 3");
        newCustomer.setCourriel("contact3@example.com");
        newCustomer.setCustomer("boutique-3");
        newCustomer.setActif(1);
        newCustomer.setClient(client); // important pour respecter la FK

        Customer saved = customerRepository.save(newCustomer);

        assertNotNull(saved.getCustomId());
        assertEquals("boutique-3", saved.getCustomer());
        assertEquals(client.getCltId(), saved.getClient().getCltId());

    }


    @Test
    void testFindByActif() {
        List<Customer> actifs = customerRepository.findByActif(1);
        assertEquals(1, actifs.size());
        assertEquals(customer1.getCustomer(), actifs.get(0).getCustomer());

        List<Customer> inactifs = customerRepository.findByActif(0);
        assertEquals(1, inactifs.size());
        assertEquals(customer2.getCustomer(), inactifs.get(0).getCustomer());
    }
}
