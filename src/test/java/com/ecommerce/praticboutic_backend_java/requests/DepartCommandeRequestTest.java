package com.ecommerce.praticboutic_backend_java.requests;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class DepartCommandeRequestTest {

    @Test
    void testSetAndGetNom() {
        DepartCommandeRequest request = new DepartCommandeRequest();
        String expected = "Dupont";
        request.setNom(expected);
        assertEquals(expected, request.getNom());
    }

    @Test
    void testSetAndGetPrenom() {
        DepartCommandeRequest request = new DepartCommandeRequest();
        String expected = "Jean";
        request.setPrenom(expected);
        assertEquals(expected, request.getPrenom());
    }

    @Test
    void testSetAndGetAdresse1And2() {
        DepartCommandeRequest request = new DepartCommandeRequest();
        request.setAdr1("1 rue A");
        request.setAdr2("Batiment B");
        assertEquals("1 rue A", request.getAdresse1());
        assertEquals("Batiment B", request.getAdresse2());
    }

    @Test
    void testSetAndGetCodePostalAndVille() {
        DepartCommandeRequest request = new DepartCommandeRequest();
        request.setCodePostal("75001");
        request.setVille("Paris");
        assertEquals("75001", request.getCodePostal());
        assertEquals("Paris", request.getVille());
    }

    @Test
    void testSetAndGetTelephonePaiementVenteInfoSup() {
        DepartCommandeRequest request = new DepartCommandeRequest();
        request.setTelephone("0123456789");
        request.setPaiement("CB");
        request.setVente("en ligne");
        request.setInfoSup("Livrer avant midi");

        assertEquals("0123456789", request.getTelephone());
        assertEquals("CB", request.getPaiement());
        assertEquals("en ligne", request.getVente());
        assertEquals("Livrer avant midi", request.getInfoSup());
    }

    @Test
    void testSetAndGetRemiseEtFraisLivr() {
        DepartCommandeRequest request = new DepartCommandeRequest();
        request.setRemise(10.5);
        request.setFraislivr(5.0);

        assertEquals(10.5, request.getRemise());
        assertEquals(5.0, request.getFraislivr());
    }

    @Test
    void testAddRemoveContainsItemAndCount() {
        DepartCommandeRequest request = new DepartCommandeRequest();
        Item item1 = new Item();
        Item item2 = new Item();

        // add
        request.addItem(item1);
        request.addItem(item2);
        assertTrue(request.containsItem(item1));
        assertTrue(request.containsItem(item2));
        assertEquals(2, request.getItemCount());

        // remove
        request.removeItem(item1);
        assertFalse(request.containsItem(item1));
        assertEquals(1, request.getItemCount());
    }

    @Test
    void testItemsListGetterSetter() {
        DepartCommandeRequest request = new DepartCommandeRequest();
        Item item = new Item();
        request.setItems(List.of(item));
        List<Item> items = request.getItems();

        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals(item, items.get(0));
    }
}
