package com.ecommerce.praticboutic_backend_java.models;

import com.ecommerce.praticboutic_backend_java.entities.Abonnement;
import jakarta.persistence.EntityManager;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@EntityScan("com.ecommerce.praticboutic_backend_java.entities")
class BaseEntityTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private SessionFactory sessionFactory;

    @Test
    void testCapitalize() {
        assertEquals("Hello", BaseEntity.capitalize("hello"));
        assertEquals("A", BaseEntity.capitalize("a"));
        assertEquals("", BaseEntity.capitalize(""));
        assertNull(BaseEntity.capitalize(null));
    }

    @Test
    void testLoadEntityClassThrowsException() {
        assertThrows(ClassNotFoundException.class, () -> BaseEntity.loadEntityClass("Inconnue"));
    }

    @Test
    void testGetPrimaryKeyNameWithRealEntity() throws ClassNotFoundException {
        // On utilise la vraie table "abonnement" créée par Hibernate
        String pkName = BaseEntity.getPrimaryKeyName(sessionFactory, entityManager, "Abonnement");
        assertEquals("aboId", pkName); // correspond au champ @Id dans Abonnement
    }
}
