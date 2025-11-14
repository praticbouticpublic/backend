package com.ecommerce.praticboutic_backend_java.repositories;

import com.ecommerce.praticboutic_backend_java.entities.Article;
import com.ecommerce.praticboutic_backend_java.entities.Categorie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Rollback // assure que chaque test est rollbacké automatiquement
class ArticleRepositoryTest {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private CategorieRepository categorieRepository;

    private Categorie categorie;
    private Article article1;
    private Article article2;

    @BeforeEach
    void setUp() {
        // Crée une catégorie pour les articles
        categorie = new Categorie();
        categorie.setCustomid(1);
        categorie.setNom("Catégorie 1");
        categorie.setVisible(1);
        categorie = categorieRepository.save(categorie); // sauvegarde en DB

        // Crée un article visible
        article1 = new Article();
        article1.setNom("Article visible");
        article1.setCustomId(1);
        article1.setCatid(categorie.getCatid());
        article1.setPrix(10.0);
        article1.setVisible(1);
        article1.setUnite("€");
        articleRepository.save(article1);

        // Crée un article invisible
        article2 = new Article();
        article2.setNom("Article invisible");
        article2.setCustomId(1);
        article2.setCatid(categorie.getCatid());
        article2.setPrix(5.0);
        article2.setVisible(0);
        article2.setUnite("€");
        articleRepository.save(article2);
    }

    @Test
    void testFindByCustomid() {
        List<Article> result = articleRepository.findByCustomid(1);
        assertEquals(2, result.size());
    }

    @Test
    void testFindByCustomidAndCatid() {
        List<Article> result = articleRepository.findByCustomidAndCatid(1, categorie.getCatid());
        assertEquals(2, result.size());
    }

    @Test
    void testFindByCustomidAndVisible() {
        List<Article> visibleArticles = articleRepository.findByCustomidAndVisible(1, 1);
        assertEquals(1, visibleArticles.size());
        assertEquals(article1.getNom(), visibleArticles.get(0).getNom());

        List<Article> invisibleArticles = articleRepository.findByCustomidAndVisible(1, 0);
        assertEquals(1, invisibleArticles.size());
        assertEquals(article2.getNom(), invisibleArticles.get(0).getNom());
    }
}
