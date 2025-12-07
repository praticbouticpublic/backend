package com.ecommerce.praticboutic_backend_java.repositories;

import com.ecommerce.praticboutic_backend_java.entities.GroupeOpt;
import com.ecommerce.praticboutic_backend_java.entities.Identifiant;
import com.ecommerce.praticboutic_backend_java.entities.Image;
import com.ecommerce.praticboutic_backend_java.entities.RelGrpOptArt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RelGrpOptArtRepository extends JpaRepository<RelGrpOptArt, Integer>  {


    @Query(value = "SELECT g.grpoptid, g.nom, g.multiple FROM groupeopt g " +
            "INNER JOIN relgrpoptart r ON g.grpoptid = r.grpoptid " +
            "WHERE r.customid = :customid AND g.customid = :customid " +
            "AND r.visible = 1 AND g.visible = 1 AND r.artid = :artId " +
            "ORDER BY g.grpoptid",
            nativeQuery = true)
    List<?> findByCustomidAndArtId(@Param("customid") Integer customid, @Param("artId") Integer artId);


/*    @Query(value = "SELECT g.grpoptid, g.nom, g.multiple FROM relgrpoptart r " +
            "JOIN groupeopt g ON r.grpoptid = g.grpoptid " +
            "WHERE r.customid = :customid AND g.customid = :customid " +
            "AND r.visible = 1 AND g.visible = 1 AND r.artid = :artId " +
            "ORDER BY g.grpoptid", nativeQuery = true)
    List<Object[]> findByCustomidAndArtId(@Param("customid") Integer customid, @Param("artId") Integer artId);*/
// au lieu de findByArtid
}
