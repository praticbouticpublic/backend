package com.ecommerce.praticboutic_backend_java.repositories;

import com.ecommerce.praticboutic_backend_java.entities.Client;
import com.ecommerce.praticboutic_backend_java.entities.Customer;
import com.ecommerce.praticboutic_backend_java.entities.GroupeOpt;
import com.ecommerce.praticboutic_backend_java.entities.Option;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@EnableJpaRepositories(basePackages = "com.ecommerce.praticboutic_backend_java.repositories")
@EntityScan(basePackages = "com.ecommerce.praticboutic_backend_java.entities")
@TestPropertySource(locations = "classpath:application.properties")
@Transactional
class OptionRepositoryTest {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private GroupeOptionRepository groupeOptRepository;

    @Autowired
    private OptionRepository optionRepository;

    private Client client;
    private Customer customer;
    private GroupeOpt groupeOpt;
    private Option option1;
    private Option option2;

    @BeforeEach
    void setUp() {
        // Nettoyage
        optionRepository.deleteAll();
        groupeOptRepository.deleteAll();
        customerRepository.deleteAll();
        clientRepository.deleteAll();

        // 🔹 Crée un client
        client = new Client();
        client.setNom("Jean Dupont");
        client.setEmail("jean@example.com");
        client.setActif(1);
        client = clientRepository.save(client);

        // 🔹 Crée un customer lié au client
        customer = new Customer();
        customer.setNom("Test Boutique");
        customer.setClient(client);
        customer = customerRepository.save(customer);

        // 🔹 Crée un groupe d'options
        groupeOpt = new GroupeOpt();
        groupeOpt.setNom("Tailles");
        groupeOpt.setCustomId(customer.getCustomId());
        groupeOpt.setVisible(1);
        groupeOpt = groupeOptRepository.save(groupeOpt);

        // 🔹 Crée des options liées au groupe
        option1 = new Option();
        option1.setNom("Option 1");
        option1.setGroupeOptionId(groupeOpt.getGrpoptid());
        option1.setSurcout(5.0);
        option1.setVisible(1);
        option1.setCustomId(customer.getCustomId());
        option1.setGroupeOption(groupeOpt);
        option1 = optionRepository.save(option1);

        option2 = new Option();
        option2.setNom("Option 2");
        option2.setGroupeOptionId(groupeOpt.getGrpoptid());
        option2.setSurcout(3.0);
        option2.setVisible(1);
        option2.setCustomId(customer.getCustomId());
        option2.setGroupeOption(groupeOpt);
        option2 = optionRepository.save(option2);
    }

    @Test
    void testFindByGrpoptid() {
        List<?> options = optionRepository.findByGrpoptid(groupeOpt.getGrpoptid());
        assertEquals(2, options.size());

        // Vérifie que chaque résultat contient les bonnes colonnes
        Object[] first = (Object[]) options.get(0);
        assertEquals(option1.getOptId(), ((Number) first[0]).intValue());
        assertEquals(option1.getNom(), first[1]);
        assertEquals(option1.getSurcout(), ((Number) first[2]).doubleValue());

        Object[] second = (Object[]) options.get(1);
        assertEquals(option2.getOptId(), ((Number) second[0]).intValue());
        assertEquals(option2.getNom(), second[1]);
        assertEquals(option2.getSurcout(), ((Number) second[2]).doubleValue());
    }

    @Test
    void testSaveOption() {

        Option newOption = new Option();
        newOption.setNom("Option 3");
        newOption.setGroupeOptionId(groupeOpt.getGrpoptid());
        newOption.setSurcout(2.5);
        newOption.setVisible(1);
        newOption.setCustomId(customer.getCustomId());
        newOption.setGroupeOption(groupeOpt);


        Option saved = optionRepository.save(newOption);

        assertNotNull(saved.getOptId());
        assertEquals("Option 3", saved.getNom());
        assertEquals(groupeOpt.getGrpoptid(), saved.getGroupeOptionId());
    }
}
