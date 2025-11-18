package com.ecommerce.praticboutic_backend_java.repositories;

import com.ecommerce.praticboutic_backend_java.entities.Commande;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CommandeRepositoryTest {

    @Autowired
    private CommandeRepository commandeRepository;

    private Commande commande;

    @BeforeEach
    void setUp() {
        commandeRepository.deleteAll();

        commande = new Commande();
        commande.setDateCreation(LocalDate.now().atStartOfDay());
        commande.setTotal(150.0);
        // set d'autres propriétés nécessaires de la commande ici

        commande = commandeRepository.save(commande);
    }

    @Test
    void testSaveCommande() {
        assertNotNull(commande.getTotal());
        assertEquals(150.0, commande.getTotal());
    }

    @Test
    void testFindById() {
        Optional<Commande> found = commandeRepository.findById(commande.getCmdId());
        assertTrue(found.isPresent());
        assertEquals(commande.getTotal(), found.get().getTotal());
    }

    @Test
    void testUpdateCommande() {
        commande.setTotal(200.0);
        Commande updated = commandeRepository.save(commande);
        assertEquals(200.0, updated.getTotal());
    }

    @Test
    void testDeleteCommande() {
        commandeRepository.delete(commande);
        Optional<Commande> found = commandeRepository.findById(commande.getCmdId());
        assertTrue(found.isEmpty());
    }
}
