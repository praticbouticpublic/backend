package com.ecommerce.praticboutic_backend_java.controllers;

import com.ecommerce.praticboutic_backend_java.requests.SMSRequest;
import com.ecommerce.praticboutic_backend_java.services.ParameterService;
import com.ecommerce.praticboutic_backend_java.services.SmsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// ... existing code ...

class SMSControllerTest {

    private SMSController controller;
    private ParameterService paramService;
    private SmsService smsService;

    @BeforeEach
    void setUp() {
        controller = new SMSController();
        paramService = mock(ParameterService.class, Answers.RETURNS_DEEP_STUBS);
        smsService = mock(SmsService.class, Answers.RETURNS_DEEP_STUBS);

        inject(controller, "paramService", paramService);
        inject(controller, "smsService", smsService);
    }

    @Test
    @DisplayName("sendSMS - envoie si VALIDATION_SMS == 1 et OK")
    void sendSMS_sends_whenEnabled_andOk() throws Exception {
        SMSRequest req = new SMSRequest();
        req.setBouticid(42);
        req.setMessage("Hello");
        req.setTelephone("0600000000");

        when(paramService.getParameterValue("VALIDATION_SMS", 42)).thenReturn("1");
        when(smsService.sendSmsViaApi("Hello", "0600000000")).thenReturn(true);

        ResponseEntity<?> resp = controller.sendSMS(req);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals("SMS OK", ((java.util.Map<?, ?>) resp.getBody()).get("result"));
    }

    @Test
    @DisplayName("sendSMS - renvoie 'Aucun SMS...' si VALIDATION_SMS != 1")
    void sendSMS_noSend_whenDisabled() {
        SMSRequest req = new SMSRequest();
        req.setBouticid(42);
        req.setMessage("Hello");
        req.setTelephone("0600000000");

        when(paramService.getParameterValue("VALIDATION_SMS", 42)).thenReturn("0");

        ResponseEntity<?> resp = controller.sendSMS(req);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals("Aucun SMS n'a été envoyé", ((java.util.Map<?, ?>) resp.getBody()).get("result"));
    }

    @Test
    @DisplayName("sendSMS - renvoie 'Aucun SMS...' si API retourne false")
    void sendSMS_apiReturnsFalse() throws Exception {
        SMSRequest req = new SMSRequest();
        req.setBouticid(42);
        req.setMessage("Hello");
        req.setTelephone("0600000000");

        when(paramService.getParameterValue("VALIDATION_SMS", 42)).thenReturn("1");
        when(smsService.sendSmsViaApi("Hello", "0600000000")).thenReturn(false);

        ResponseEntity<?> resp = controller.sendSMS(req);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals("Aucun SMS n'a été envoyé", ((java.util.Map<?, ?>) resp.getBody()).get("result"));
    }

    private static void inject(Object target, String field, Object value) {
        try {
            java.lang.reflect.Field f = target.getClass().getDeclaredField(field);
            f.setAccessible(true);
            f.set(target, value);
        } catch (Exception e) {
            fail("Injection échouée: " + field + " - " + e.getMessage());
        }
    }
}