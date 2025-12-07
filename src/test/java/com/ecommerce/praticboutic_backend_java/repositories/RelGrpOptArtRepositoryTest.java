package com.ecommerce.praticboutic_backend_java.repositories;

import com.ecommerce.praticboutic_backend_java.entities.Article;
import com.ecommerce.praticboutic_backend_java.entities.Categorie;
import com.ecommerce.praticboutic_backend_java.entities.GroupeOpt;
import com.ecommerce.praticboutic_backend_java.entities.RelGrpOptArt;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
class RelGrpOptArtRepositoryTest {

    @Autowired
    private RelGrpOptArtRepository relGrpOptArtRepository;

    @Autowired
    private GroupeOptionRepository groupeOptRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private CategorieRepository categorieRepository;

    private RelGrpOptArt rel1;
    private RelGrpOptArt rel2;
    private GroupeOpt grp1;
    private Article art1;

    @BeforeEach
    void setUp() {
        relGrpOptArtRepository.deleteAll();
        groupeOptRepository.deleteAll();
        articleRepository.deleteAll();
        categorieRepository.deleteAll(); // Assurez-vous d'avoir injecté ce repository

        // Création d'une catégorie
        Categorie cat = new Categorie();
        cat.setNom("Catégorie Test");
        cat.setCustomid(1);
        cat.setVisible(1);
        cat = categorieRepository.save(cat);

        // Création du groupe d'options
        grp1 = new GroupeOpt();
        grp1.setNom("Group 1");
        grp1.setMultiple(0);
        grp1.setVisible(1);
        grp1.setCustomId(1);
        grp1 = groupeOptRepository.save(grp1);

        // Création de l'article avec la catégorie valide
        art1 = new Article();
        art1.setNom("Article 1");
        art1.setCustomId(1);
        art1.setVisible(1);
        art1.setPrix(9.90);
        art1.setUnite("€");
        art1.setCatid(cat.getCatid()); // ✅ assigner la catégorie
        art1 = articleRepository.save(art1);

        // Création des relations
        rel1 = new RelGrpOptArt();
        rel1.setCustomId(1);
        rel1.setGrpoptid(grp1.getGrpoptid());
        rel1.setArtid(art1.getArtid());
        rel1.setVisible(1);
        rel1 = relGrpOptArtRepository.save(rel1);

        rel2 = new RelGrpOptArt();
        rel2.setCustomId(1);
        rel2.setGrpoptid(grp1.getGrpoptid());
        rel2.setArtid(art1.getArtid());
        rel2.setVisible(0); // invisible
        rel2 = relGrpOptArtRepository.save(rel2);
    }


    @Test
    void testFindByCustomidAndArtId() {
        List<?> results = relGrpOptArtRepository.findByCustomidAndArtId(1, art1.getArtid());
        assertEquals(1, results.size()); // seul rel1 est visible

        Object[] row = (Object[]) results.get(0);
        assertEquals(grp1.getGrpoptid(), ((Number) row[0]).intValue());
        assertEquals(grp1.getNom(), row[1]);
        assertEquals(grp1.getMultiple(), ((Number) row[2]).intValue());
    }

    @Test
    void testSaveAndVisibility() {
        RelGrpOptArt rel = new RelGrpOptArt();
        rel.setCustomId(2);
        rel.setGrpoptid(grp1.getGrpoptid());
        rel.setArtid(art1.getArtid());
        rel.setVisible(1);

        RelGrpOptArt saved = relGrpOptArtRepository.save(rel);
        assertNotNull(saved.getRelgrpoartid());
        assertTrue(saved.isVisible());

        saved.setVisible(0);
        RelGrpOptArt updated = relGrpOptArtRepository.save(saved);
        assertFalse(updated.isVisible());
    }

    @Test
    void testDisplayData() {
        rel1.setGroupeOpt(grp1);
        rel1.setArticle(art1);

        List<Object> display = rel1.getDisplayData();
        assertEquals(4, display.size());
        assertEquals(rel1.getRelgrpoartid(), display.get(0));
        assertEquals(grp1.getNom(), display.get(1));
        assertEquals(art1.getNom(), display.get(2));
        assertEquals("1", display.get(3)); // visible = 1
    }
}
