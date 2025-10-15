package com.ecommerce.praticboutic_backend_java.controllers;

import com.ecommerce.praticboutic_backend_java.models.JwtPayload;
import com.ecommerce.praticboutic_backend_java.requests.ShopConfigRequest;
import com.ecommerce.praticboutic_backend_java.services.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

// ... existing code ...

class ShopSettingsControllerTest {

    @Test
    @DisplayName("configureShop - 401 si email non vérifié")
    void configureShop_unauthorized_whenEmailNotVerified() {
        ShopSettingsController controller = new ShopSettingsController();

        ShopConfigRequest req = new ShopConfigRequest();
        req.setChxmethode("TOUS");
        req.setChxpaie("TOUS");
        req.setMntmincmd("0");
        req.setValidsms(1);

        Map<String, Object> payload = new HashMap<>();

        try (MockedStatic<JwtService> jwtStatic = Mockito.mockStatic(JwtService.class)) {
            jwtStatic.when(() -> JwtService.parseToken("tok"))
                    .thenReturn(new JwtPayload(null, null, payload));

            ResponseEntity<?> resp = controller.configureShop(req, "Bearer tok");

            assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
        }
    }

    @Test
    @DisplayName("configureShop - 200 et token quand email vérifié")
    void configureShop_ok() {
        ShopSettingsController controller = new ShopSettingsController();

        ShopConfigRequest req = new ShopConfigRequest();
        req.setChxmethode("TOUS");
        req.setChxpaie("TOUS");
        req.setMntmincmd("10");
        req.setValidsms(1);

        Map<String, Object> payload = new HashMap<>();
        payload.put("verify_email", "user@example.com");

        try (MockedStatic<JwtService> jwtStatic = Mockito.mockStatic(JwtService.class)) {
            jwtStatic.when(() -> JwtService.parseToken("tok"))
                    .thenReturn(new JwtPayload(null, null, payload));
            jwtStatic.when(() -> JwtService.generateToken(Mockito.anyMap(), Mockito.anyString()))
                    .thenReturn("new.jwt");

            ResponseEntity<?> resp = controller.configureShop(req, "Bearer tok");

            assertEquals(HttpStatus.OK, resp.getStatusCode());
            Map<?, ?> body = (Map<?, ?>) resp.getBody();
            assertEquals("OK", body.get("result"));
            assertEquals("new.jwt", body.get("token"));
        }
    }
}
