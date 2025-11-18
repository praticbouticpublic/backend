package com.ecommerce.praticboutic_backend_java.entities;

import com.ecommerce.praticboutic_backend_java.repositories.CustomerRepository;
import com.ecommerce.praticboutic_backend_java.repositories.OptionRepository;
import com.ecommerce.praticboutic_backend_java.repositories.GroupeOptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class OptionTest {

    @Autowired
    private OptionRepository optionRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private GroupeOptionRepository groupeOptRepository;

    private Customer customer;
    private GroupeOpt groupeOpt;

    @BeforeEach
    void setUp() {
        // Création du client pour respecter les FK
        customer = new Customer();
        customer.setNom("Customer Test");
        customer.setCourriel("test@example.com");
        customer.setActif(1);
        customerRepository.save(customer);

        // Création du groupe d'options
        groupeOpt = new GroupeOpt();
        groupeOpt.setNom("Tailles");
        groupeOpt.setCustomId(customer.getCustomId());
        groupeOptRepository.save(groupeOpt);
    }

    @Test
    @DisplayName("Instanciation - valeurs par défaut plausibles")
    void defaultValues() {
        Option o = new Option();
        assertNull(o.getOptId());
        assertNull(o.getCustomId());
        assertNull(o.getGroupeOptionId());
        assertNull(o.getNom());
        assertNull(o.getSurcout());
    }

    @Test
    @DisplayName("Getters/Setters basiques")
    void gettersSetters() {
        Option o = new Option();
        o.setOptId(101);
        o.setCustomId(1); // doit correspondre à testCustomer
        o.setGroupeOption(groupeOpt);
        o.setNom("Supplément fromage");
        o.setSurcout(1.50);

        assertEquals(101, o.getOptId());
        assertEquals(1, o.getCustomId());
        assertEquals(groupeOpt, o.getGroupeOption());
        assertEquals("Supplément fromage", o.getNom());
        assertEquals(1.50, o.getSurcout());
    }

    @Test
    @DisplayName("Persistance en base")
    void saveOption() {
        Option o = new Option();
        o.setCustomId(customer.getCustomId());
        o.setGroupeOption(groupeOpt);
        o.setNom("Extra sauce");
        o.setSurcout(0.5);

        Option saved = optionRepository.save(o);
        assertNotNull(saved.getOptId());
        assertEquals("Extra sauce", saved.getNom());
    }

    @Test
    @DisplayName("getDisplayData() - retourne des données cohérentes")
    void getDisplayData_ifPresent() {
        // Création de l'option avec groupe associé
        Option o = new Option();
        o.setOptId(5);
        o.setNom("Sans oignon");
        o.setSurcout(0.0);
        o.setGroupeOption(groupeOpt); // le nom du groupe doit correspondre à l'attendu

        // Récupération des données pour affichage
        List<Object> row = o.getDisplayData();

        assertNotNull(row);
        assertEquals(5, row.size()); // 5 colonnes dans getDisplayData()

        // Vérification de chaque colonne
        assertEquals(5, row.get(0));                     // optId
        assertEquals("Sans oignon", row.get(1));        // nom de l'option
        assertEquals(0.0, row.get(2));                  // surcout
        assertEquals("Tailles", row.get(3));            // nom du groupe
        assertEquals(true, row.get(4));                 // visible
    }


    @Test
    @DisplayName("toString() ne jette pas d'exception et reflète le nom si défini")
    void toString_ok() {
        Option o = new Option();
        o.setNom("Extra sauce");
        String s = o.toString();
        assertNotNull(s);
        assertTrue(s.contains("Extra sauce"));
    }
}
