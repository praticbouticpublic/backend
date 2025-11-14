package com.ecommerce.praticboutic_backend_java.repositories;

import com.ecommerce.praticboutic_backend_java.entities.Connexion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ConnexionRepositoryTest {

    @Autowired
    private ConnexionRepository connexionRepository;

    private Connexion connexion1;
    private Connexion connexion2;

    @BeforeEach
    void setUp() {
        connexionRepository.deleteAll();

        LocalDateTime now = LocalDateTime.now();

        connexion1 = new Connexion();
        connexion1.setIp("192.168.0.1");
        connexion1.setTs(now.minusMinutes(10));

        connexion2 = new Connexion();
        connexion2.setIp("192.168.0.1");
        connexion2.setTs(now.minusMinutes(5));

        connexionRepository.save(connexion1);
        connexionRepository.save(connexion2);
    }

    @Test
    void testCountByIpAndTsAfter() {
        LocalDateTime limit = LocalDateTime.now().minusMinutes(7);
        int count = connexionRepository.countByIpAndTsAfter("192.168.0.1", limit);
        assertEquals(1, count);

        count = connexionRepository.countByIpAndTsAfter("192.168.0.1", LocalDateTime.now().minusMinutes(15));
        assertEquals(2, count);

        count = connexionRepository.countByIpAndTsAfter("10.0.0.1", LocalDateTime.now().minusMinutes(15));
        assertEquals(0, count);
    }

    @Test
    void testSaveAndFind() {
        Connexion newConn = new Connexion();
        newConn.setIp("10.0.0.2");
        newConn.setTs(LocalDateTime.now());
        Connexion saved = connexionRepository.save(newConn);

        assertNotNull(saved.getId());
        assertEquals("10.0.0.2", saved.getIp());
        assertNotNull(saved.getTs());
    }
}
