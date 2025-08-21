package com.ecommerce.praticboutic_backend_java.services;

import com.ecommerce.praticboutic_backend_java.entities.Image;
import com.ecommerce.praticboutic_backend_java.repositories.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service gérant les opérations liées aux images
 */
@Service
@Transactional
public class ImageService {

    @Autowired
    private ImageRepository imageRepository;
    
    private final Path rootLocation = Paths.get("uploads/images");

    /**
     * Récupère les images pour un article donné
     * 
     * @param artId L'identifiant de l'article
     * @return Liste des images
     */
    public List<Image> getImagesByArticle(Integer artId) {
        return imageRepository.findByArtid(artId);
    }
    
    /**
     * Trouve une image par son identifiant
     * 
     * @param imageId L'identifiant de l'image
     * @return L'image ou null si non trouvée
     */
    public Image findById(Integer imageId) {
        return imageRepository.findById(imageId).orElse(null);
    }
    
    /**
     * Sauvegarde une image
     * 
     * @param image L'image à sauvegarder
     * @return L'image sauvegardée
     */
    public Image save(Image image) {
        return imageRepository.save(image);
    }
    
    /**
     * Supprime une image
     * 
     * @param imageId L'identifiant de l'image à supprimer
     */
    public void delete(Integer imageId) {
        Optional<Image> optionalImage = imageRepository.findById(imageId);
        if (optionalImage.isPresent()) {
            Image image = optionalImage.get();
            try {
                // Supprime le fichier physique
                Path filePath = rootLocation.resolve(image.getImage());
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                // Logger l'erreur mais continuer la suppression en base
            }
            imageRepository.delete(image);
        }
    }
    
    /**
     * Télécharge et enregistre une nouvelle image
     * 
     * @param file Fichier image à télécharger
     * @param artId Identifiant de l'article associé
     * @return L'image créée
     * @throws IOException En cas d'erreur lors de l'écriture du fichier
     */
    public Image uploadImage(MultipartFile file, Integer artId) throws IOException {
        // Création du répertoire si nécessaire
        if (!Files.exists(rootLocation)) {
            Files.createDirectories(rootLocation);
        }
        
        // Génère un nom de fichier unique
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path destinationFile = rootLocation.resolve(fileName).normalize();
        
        // Copie le fichier
        Files.copy(file.getInputStream(), destinationFile);
        

        // Crée et sauvegarde l'entité Image
        Image newImage = new Image();
        newImage.setArtid(artId);
        newImage.setImage(fileName);

        return imageRepository.save(newImage);
    }
    
    public List<?> getImages(Integer bouticid, Integer artid) {
        return imageRepository.findByCustomidAndArtid(bouticid, artid);
    }
}