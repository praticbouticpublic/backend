package com.ecommerce.praticboutic_backend_java.controllers;


import com.ecommerce.praticboutic_backend_java.requests.SMSRequest;
import com.ecommerce.praticboutic_backend_java.services.ParameterService;
import com.ecommerce.praticboutic_backend_java.services.SmsService;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class SMSController {

    @Autowired
    private ParameterService paramService;

    @Autowired
    private SmsService smsService;

    @PostMapping("/send-sms")
    public ResponseEntity<?> sendSMS(@RequestBody SMSRequest request) {
        try {
            // Envoyer sms si nécessaire
            // Get SMS validation parameter
            String validSms = paramService.getParameterValue("VALIDATION_SMS", request.getBouticid());
            if (validSms.equals("1"))
            {
                if(smsService.sendSmsViaApi(request.getMessage(), request.getTelephone()))
                    return ResponseEntity.ok(Map.of("result","SMS OK"));
                else
                    return ResponseEntity.ok(Map.of("result","Aucun SMS n'a été envoyé"));
            }
            else
                return ResponseEntity.ok(Map.of("result","Aucun SMS n'a été envoyé"));


        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur: " + e.getMessage());
        }

    }
}
