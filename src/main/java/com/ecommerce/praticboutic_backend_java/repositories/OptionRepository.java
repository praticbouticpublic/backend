package com.ecommerce.praticboutic_backend_java.repositories;

import com.ecommerce.praticboutic_backend_java.entities.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'accès aux données des options
 */
@Repository
public interface OptionRepository extends JpaRepository<Option, Integer> {

    @Query(value = "SELECT o.optid, o.nom, o.surcout FROM `option` o " +
            "WHERE o.grpoptid = :grpoptid AND o.visible = 1 ORDER BY o.optid",
            nativeQuery = true)
    List<?> findByGrpoptid(@Param("grpoptid") Integer grpOptId);



}