package com.ecommerce.praticboutic_backend_java.repositories;

import com.ecommerce.praticboutic_backend_java.entities.Client;
import com.ecommerce.praticboutic_backend_java.entities.Customer;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


/**
 * Repository pour l'entité Client
 */
@Repository
public interface ClientRepository extends JpaRepository<Client, Integer> {
    @Query("SELECT COUNT(c) FROM Client c WHERE c.email = :email")
    Long countByEmail(String email);

    @Modifying
    @Transactional
    @Query("UPDATE Client c SET c.email = :email WHERE c.id = :id")
    void updateEmailById(@Param("email") String email, @Param("id") Integer id);

    Optional<Client> findByEmailAndActif(String email, Integer actif);

    Optional<Client> findByEmail(String email);

    //Client find(Integer bouticid);

    @Query("SELECT c FROM Client c JOIN Customer cust ON c.cltid = cust.cltid WHERE cust = :customer")
    Client findByCustomer(@Param("customer") Customer customer);

    //Client findByCltid(Integer clientId);

    @Query("SELECT c FROM Client c WHERE c.cltid = :cltid")
    Optional<Client> findClientById(@Param("cltid") Integer cltid);

    boolean existsByEmail(String email);
}
