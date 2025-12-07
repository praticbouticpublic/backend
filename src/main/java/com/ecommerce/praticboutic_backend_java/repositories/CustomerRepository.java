package com.ecommerce.praticboutic_backend_java.repositories;

import com.ecommerce.praticboutic_backend_java.entities.Client;
import com.ecommerce.praticboutic_backend_java.entities.Customer;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'accès aux données des boutiques
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {


    Customer findByCustomer(String strCustomer);
    Optional<Customer> findByCustomid(Integer customid);

    /**
     * Sauvegarde ou met à jour un client
     *
     * @param customer le client à sauvegarder
     * @return le client sauvegardé avec son identifiant généré si c'est une création
     */
    Customer save(Customer customer);



    List<Customer> findByActif(Integer actif);

}