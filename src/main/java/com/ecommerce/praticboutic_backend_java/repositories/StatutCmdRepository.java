package com.ecommerce.praticboutic_backend_java.repositories;

import com.ecommerce.praticboutic_backend_java.entities.StatutCmd;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface StatutCmdRepository extends JpaRepository<StatutCmd, Integer> {

    StatutCmd findByCustomidAndDefaut(Integer customid, Integer defaut);

}
