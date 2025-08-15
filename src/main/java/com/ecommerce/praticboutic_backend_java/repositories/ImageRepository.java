package com.ecommerce.praticboutic_backend_java.repositories;

import com.ecommerce.praticboutic_backend_java.entities.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository pour l'accès aux données des images
 */
@Repository
public interface ImageRepository extends JpaRepository<Image, Integer> {

    // Dans ImageRepository.java
    List<Image> findByArtid(Integer artid); // au lieu de findByArtid

    List<Image> findByCustomidAndArtid(Integer customid, Integer artid); // au lieu de findByArtid

}