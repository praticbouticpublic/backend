package com.ecommerce.praticboutic_backend_java.repositories;

import com.ecommerce.praticboutic_backend_java.repositories.CategorieRepository;
import com.ecommerce.praticboutic_backend_java.entities.Categorie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CategorieRepositoryTest {

    @Autowired
    private CategorieRepository categorieRepository;

    @Test
    void testFindByCustomidOrCatidOrderByCatid() {
        Categorie cat1 = new Categorie();
        cat1.setCustomid(0);
        cat1.setNom("Catégorie globale");

        Categorie cat2 = new Categorie();
        cat2.setCustomid(10);
        cat2.setNom("Catégorie boutique 10");

        categorieRepository.save(cat1);
        categorieRepository.save(cat2);

        List<Categorie> result = categorieRepository.findByCustomidOrCatidOrderByCatid(10, cat2.getCatid());


        assertThat(result)
                .hasSize(1)
                .extracting(Categorie::getNom)
                .containsExactly("Catégorie boutique 10");

    }
}
