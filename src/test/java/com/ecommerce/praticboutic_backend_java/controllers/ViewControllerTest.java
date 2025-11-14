package com.ecommerce.praticboutic_backend_java.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.*;

// ... existing code ...

class ViewControllerTest {

    @Test
    @DisplayName("showAutoclosePage - retourne le nom de vue 'autoclose'")
    void showAutoclosePage_returnsViewName() {
        ViewController controller = new ViewController();
        Model model = new ConcurrentModel();

        String view = controller.showAutoclosePage(model);

        assertEquals("autoclose", view);
    }
}