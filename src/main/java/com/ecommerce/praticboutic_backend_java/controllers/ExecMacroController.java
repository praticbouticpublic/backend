package com.ecommerce.praticboutic_backend_java.controllers;

import com.ecommerce.praticboutic_backend_java.services.ExecMacroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ExecMacroController {

    @Autowired
    private ExecMacroService macroService;

    @Value("${rattrapage.secret.key}")
    private String secretKey;

    @PostMapping("/execute")
    public ResponseEntity<Integer> executeMacro(@RequestHeader("X-RATTRAPAGE-KEY") String key) {
        if (!secretKey.equals(key)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(-1);
        }
        return ResponseEntity.ok().body(macroService.desactiveBoutic());
    }


}
