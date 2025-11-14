package com.ecommerce.praticboutic_backend_java.repositories;

import com.ecommerce.praticboutic_backend_java.entities.StatutCmd;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class StatutCmdRepositoryTest {

    @Autowired
    private StatutCmdRepository statutCmdRepository;

    private StatutCmd statut1;
    private StatutCmd statut2;

    @BeforeEach
    void setUp() {
        statutCmdRepository.deleteAll();

        statut1 = new StatutCmd(1, "En attente", "#FF0000", "Commande en attente", 1, 1);
        statut2 = new StatutCmd(1, "Livrée", "#00FF00", "Commande livrée", 0, 1);

        statut1 = statutCmdRepository.save(statut1);
        statut2 = statutCmdRepository.save(statut2);
    }

    @Test
    void testSaveAndFind() {
        assertNotNull(statut1.getStatid());
        assertNotNull(statut2.getStatid());

        StatutCmd found = statutCmdRepository.findByCustomidAndDefaut(1, 1);
        assertNotNull(found);
        assertEquals(statut1.getEtat(), found.getEtat());
        assertEquals(statut1.getCouleur(), found.getCouleur());
    }

    @Test
    void testUpdateStatut() {
        statut2.setEtat("Expédiée");
        statutCmdRepository.save(statut2);

        StatutCmd updated = statutCmdRepository.findByCustomidAndDefaut(1, 0);
        assertEquals("Expédiée", updated.getEtat());
    }

    @Test
    void testActifFlag() {
        statut1.setActif(0);
        statutCmdRepository.save(statut1);

        StatutCmd updated = statutCmdRepository.findByCustomidAndDefaut(1, 1);
    }
}
