package com.ecommerce.praticboutic_backend_java.requests;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RegistrationRequestTest {

    @Test
    void testSetAndGetFields() {
        RegistrationRequest request = new RegistrationRequest();

        request.pass = "securePass";
        request.qualite = "Admin";
        request.nom = "Doe";
        request.prenom = "John";
        request.adr1 = "123 Street";
        request.adr2 = "Apt 4B";
        request.cp = "75001";
        request.ville = "Paris";
        request.tel = "0123456789";

        assertAll(
                () -> assertEquals("securePass", request.pass),
                () -> assertEquals("Admin", request.qualite),
                () -> assertEquals("Doe", request.nom),
                () -> assertEquals("John", request.prenom),
                () -> assertEquals("123 Street", request.adr1),
                () -> assertEquals("Apt 4B", request.adr2),
                () -> assertEquals("75001", request.cp),
                () -> assertEquals("Paris", request.ville),
                () -> assertEquals("0123456789", request.tel)
        );
    }

    @Test
    void testDefaultsAreNull() {
        RegistrationRequest request = new RegistrationRequest();

        assertAll(
                () -> assertNull(request.pass),
                () -> assertNull(request.qualite),
                () -> assertNull(request.nom),
                () -> assertNull(request.prenom),
                () -> assertNull(request.adr1),
                () -> assertNull(request.adr2),
                () -> assertNull(request.cp),
                () -> assertNull(request.ville),
                () -> assertNull(request.tel)
        );
    }
}
