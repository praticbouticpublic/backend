package com.ecommerce.praticboutic_backend_java.entities;

import com.ecommerce.praticboutic_backend_java.repositories.CustomerRepository;
import com.ecommerce.praticboutic_backend_java.repositories.OptionRepository;
import com.ecommerce.praticboutic_backend_java.repositories.GroupeOptionRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest.*;
import org.springframework.boot.test.autoconfigure.jdbc.*;
import org.springframework.boot.test.autoconfigure.json.*;

import org.springframework.boot.test.context.SpringBootTest;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
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
        // Création du client sans repository
        customer = new Customer();
        customer.setNom("Customer Test");
        customer.setCourriel("test@example.com");
        customer.setActif(1);

        // Création du groupe d’options sans repository
        groupeOpt = new GroupeOpt();
        groupeOpt.setNom("Tailles");
        groupeOpt.setCustomId(Integer.valueOf("123")); // ou ce que tu veux
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
        // Préparation de l'entité Option
        Option o = new Option();
        o.setCustomId(customer.getCustomId());
        o.setGroupeOption(groupeOpt);   // FK déjà persistée dans @BeforeEach
        o.setNom("Extra sauce");
        o.setSurcout(0.5);

        // Action
        Option saved = optionRepository.save(o);

        // Vérifications
        assertNotNull(saved.getOptId(), "L'ID doit être généré après persistance");
        assertEquals("Extra sauce", saved.getNom());
        assertEquals(0.5, saved.getSurcout());
        assertEquals(customer.getCustomId(), saved.getCustomId());
        assertEquals(groupeOpt.getGrpoptid(), saved.getGroupeOption().getGrpoptid());
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
