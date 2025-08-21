package com.ecommerce.praticboutic_backend_java.controllers;





import com.ecommerce.praticboutic_backend_java.services.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


@RestController
@RequestMapping("/api")
public class FileUploadController {

    @Value("${file.upload.location}")
    private String uploadDirectory;

    @Value("${file.upload.maxSize:5242880}")  // Default 5MB
    private long maxFileSize;

    private final List<String> ALLOWED_EXTENSIONS = Arrays.asList(".png", ".gif", ".jpg", ".jpeg");
    private final List<String> ALLOWED_MIME_TYPES = Arrays.asList("image/gif", "image/png", "image/jpeg");

    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);


    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam(value = "file", required = false) MultipartFile file, @RequestHeader("Authorization") String authHeader) {

        // Vérifier si l'email est vérifié
        String token = authHeader.replace("Bearer ", "");
        Map <java.lang.String, java.lang.Object> payload = JwtService.parseToken(token).getClaims();
        Object verifyEmail = payload.get("verify_email");
        if (verifyEmail == null || verifyEmail.toString().isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error","Courriel non vérifié"));
        }

        if (file == null || file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pas de fichier");
        }

        try {
            Path uploadPath = Paths.get(uploadDirectory).toAbsolutePath().normalize();
            logger.info("UPLOAD PATH = {}", uploadPath);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Get file details
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename != null ?
                    originalFilename.substring(originalFilename.lastIndexOf('.')).toLowerCase() : "";
            String contentType = file.getContentType();
            long fileSize = file.getSize();

            // Validation checks
            if (!ALLOWED_EXTENSIONS.contains(fileExtension)) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error","Vous devez uploader un fichier de type png, gif, jpg, jpeg..."));
            }

            if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType)) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Type mime non reconnu"));
            }

            if (fileSize > maxFileSize) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Le fichier est trop gros..."));
            }

            // Generate unique filename
            String newFilename = UUID.randomUUID().toString() + fileExtension;
            Path filePath = uploadPath.resolve(newFilename);

            // Save the file
            Files.copy(file.getInputStream(), filePath);

            // Store filename in session
            payload.put("initboutic_logo", newFilename);
            String jwt = JwtService.generateToken(payload, "" );
            Map<String, Object> response = new HashMap<>();
            response.put("result", newFilename);
            response.put("token", jwt);

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error","Echec de l'upload!"));
        }
    }

    @PostMapping("/boupload")
    public ResponseEntity<?> uploadFiles(@RequestParam(value = "file[]", required = false) MultipartFile[] files) {

        if (files == null || files.length == 0) {
            return ResponseEntity.ok().body(new ArrayList<>());
        }

        List<String> uploadedFiles = new ArrayList<>();

        try {
            Path uploadPath = Paths.get(uploadDirectory).toAbsolutePath().normalize();
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Process each file
            for (MultipartFile file : files) {
                if (file.isEmpty()) continue;

                // Get file details
                String originalFilename = file.getOriginalFilename();
                String fileExtension = originalFilename != null ?
                        originalFilename.substring(originalFilename.lastIndexOf('.')).toLowerCase() : "";
                String contentType = file.getContentType();
                long fileSize = file.getSize();

                // Validation checks
                if (!ALLOWED_EXTENSIONS.contains(fileExtension)) {
                    return ResponseEntity
                            .status(HttpStatus.BAD_REQUEST)
                            .body("{\"error\": \"Vous devez uploader des fichiers de type png, gif, jpg, jpeg...\"}");
                }

                if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType)) {
                    return ResponseEntity
                            .status(HttpStatus.BAD_REQUEST)
                            .body("{\"error\": \"Un des type mime d'un fichier est non reconnu\"}");
                }

                if (fileSize > maxFileSize) {
                    return ResponseEntity
                            .status(HttpStatus.BAD_REQUEST)
                            .body("{\"error\": \"Un des fichiers est trop gros...\"}");
                }

                // Generate unique filename
                String newFilename = UUID.randomUUID().toString() + fileExtension;
                Path filePath = uploadPath.resolve(newFilename);

                // Save the file
                Files.copy(file.getInputStream(), filePath);

                // Add filename to the result list
                uploadedFiles.add(newFilename);
            }

            return ResponseEntity.ok().body(uploadedFiles);

        } catch (IOException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Echec d'un upload de fichier !\"}");
        }
    }
}