package com.ecommerce.praticboutic_backend_java.services;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;



@Service
public class JsonParserService {

    public JsonNode lireJson(String cheminFichier) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
		return mapper.readTree(new File(cheminFichier));
    }
}


