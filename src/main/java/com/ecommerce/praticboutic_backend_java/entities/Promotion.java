package com.ecommerce.praticboutic_backend_java.entities;

import com.ecommerce.praticboutic_backend_java.models.BaseEntity;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "promotion")
public class Promotion extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "promoid")
    private Integer promoid;

    @Column(name = "customid", nullable = false)
    private Integer customid;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "taux", nullable = false)
    private Double taux;

    @Column(name = "actif", nullable = false, columnDefinition = "TINYINT UNSIGNED DEFAULT 1")
    private Integer actif = 1;

    public List<Object> getDisplayData()
    {
        List<Object> row = new ArrayList<>();
        row.add(promoid);
        row.add(code);
        row.add(taux);
        row.add(actif.toString());
        return row;
    }

}