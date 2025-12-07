package com.ecommerce.praticboutic_backend_java.services;

import com.ecommerce.praticboutic_backend_java.entities.Article;
import com.ecommerce.praticboutic_backend_java.repositories.ArticleRepository;
// ... existing code ...
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// ... existing code ...

class ArticleServiceTest {

    @Mock
    private ArticleRepository articleRepository;

    @InjectMocks
    private ArticleService articleService;

    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
    }

    // ... existing code ...

    @Test
    @DisplayName("getArticles - retourne uniquement les articles visibles et mappe les champs attendus")
    void getArticles_returnsOnlyVisible_andMapsFields() {
        Integer bouticId = 10;
        Integer catId = 3;

        Article visible = new Article();
        visible.setArtid(1);
        visible.setNom("Nom A");
        visible.setPrix(12.5);
        visible.setUnite("kg");
        visible.setDescription("Desc");
        visible.setVisible(1);

        Article hidden = new Article();
        hidden.setArtid(2);
        hidden.setNom("Nom B");
        hidden.setPrix(7.0);
        hidden.setUnite("u");
        hidden.setDescription("Hidden");
        hidden.setVisible(0);

        when(articleRepository.findByCustomidAndCatid(bouticId, catId))
                .thenReturn(Arrays.asList(visible, hidden));

        List<List<Object>> result = articleService.getArticles(bouticId, catId);

        assertEquals(1, result.size());
        List<Object> row = result.get(0);
        assertEquals(5, row.size());
        assertEquals(visible.getArtid(), row.get(0));
        assertEquals(visible.getNom(), row.get(1));
        assertEquals(visible.getPrix(), row.get(2));
        assertEquals(visible.getUnite(), row.get(3));
        assertEquals(visible.getDescription(), row.get(4));

        verify(articleRepository).findByCustomidAndCatid(bouticId, catId);
        verifyNoMoreInteractions(articleRepository);
    }

    // ... existing code ...

    @Test
    @DisplayName("findById - retourne Optional avec l'article quand trouvé")
    void findById_returnsOptionalPresent_whenFound() {
        Integer artId = 5;
        Article article = new Article();
        when(articleRepository.findById(artId)).thenReturn(Optional.of(article));

        Optional<Article> result = articleService.findById(artId);

        assertTrue(result.isPresent());
        assertSame(article, result.get());
        verify(articleRepository).findById(artId);
        verifyNoMoreInteractions(articleRepository);
    }

    // ... existing code ...

    @Test
    @DisplayName("getArticleById - retourne null quand introuvable")
    void getArticleById_returnsNull_whenMissing() {
        Integer artId = 404;
        when(articleRepository.findById(artId)).thenReturn(Optional.empty());

        Article result = articleService.getArticleById(artId);

        assertNull(result);
        verify(articleRepository).findById(artId);
        verifyNoMoreInteractions(articleRepository);
    }

    // ... existing code ...

    @Test
    @DisplayName("save - persiste et renvoie l'article")
    void save_persistsAndReturns() {
        Article input = new Article();
        Article saved = new Article();
        when(articleRepository.save(input)).thenReturn(saved);

        Article result = articleService.save(input);

        assertSame(saved, result);
        verify(articleRepository).save(input);
        verifyNoMoreInteractions(articleRepository);
    }

    // ... existing code ...

    @Test
    @DisplayName("delete - supprime par identifiant")
    void delete_deletesById() {
        Integer artId = 11;

        doNothing().when(articleRepository).deleteById(artId);

        articleService.delete(artId);

        verify(articleRepository).deleteById(artId);
        verifyNoMoreInteractions(articleRepository);
    }

    // ... existing code ...

    @Test
    @DisplayName("exists - retourne vrai/faux selon le repository")
    void exists_delegatesToRepository() {
        Integer artId = 12;
        when(articleRepository.existsById(artId)).thenReturn(true);

        boolean result = articleService.exists(artId);

        assertTrue(result);
        verify(articleRepository).existsById(artId);
        verifyNoMoreInteractions(articleRepository);
    }

    // ... existing code ...

    @Test
    @DisplayName("getAllArticlesByBoutic - retourne la liste pour une boutique")
    void getAllArticlesByBoutic_returnsList() {
        Integer bouticId = 9;
        List<Article> expected = new ArrayList<>();
        expected.add(new Article());
        when(articleRepository.findByCustomid(bouticId)).thenReturn(expected);

        List<Article> result = articleService.getAllArticlesByBoutic(bouticId);

        assertSame(expected, result);
        verify(articleRepository).findByCustomid(bouticId);
        verifyNoMoreInteractions(articleRepository);
    }

    // ... existing code ...

    @Test
    @DisplayName("updateVisibility - met à jour quand trouvé")
    void updateVisibility_updates_whenFound() {
        Integer artId = 20;
        Article existing = new Article();
        existing.setVisible(0);
        Article saved = new Article();
        saved.setVisible(1);

        when(articleRepository.findById(artId)).thenReturn(Optional.of(existing));
        when(articleRepository.save(existing)).thenReturn(saved);

        Article result = articleService.updateVisibility(artId, true);

        assertSame(saved, result);
        assertEquals(1, existing.getVisible());
        verify(articleRepository).findById(artId);
        verify(articleRepository).save(existing);
        verifyNoMoreInteractions(articleRepository);
    }

    // ... existing code ...

    @Test
    @DisplayName("updateVisibility - retourne null quand introuvable")
    void updateVisibility_returnsNull_whenMissing() {
        Integer artId = 21;
        when(articleRepository.findById(artId)).thenReturn(Optional.empty());

        Article result = articleService.updateVisibility(artId, false);

        assertNull(result);
        verify(articleRepository).findById(artId);
        verifyNoMoreInteractions(articleRepository);
    }

    // ... existing code ...

    @Test
    @DisplayName("updatePrix - met à jour le prix quand trouvé")
    void updatePrix_updates_whenFound() {
        Integer artId = 30;
        Article existing = new Article();
        existing.setPrix(9.99);
        Double newPrice = 14.5;
        Article saved = new Article();
        saved.setPrix(newPrice);

        when(articleRepository.findById(artId)).thenReturn(Optional.of(existing));
        when(articleRepository.save(existing)).thenReturn(saved);

        Article result = articleService.updatePrix(artId, newPrice);

        assertSame(saved, result);
        assertEquals(newPrice, existing.getPrix());
        verify(articleRepository).findById(artId);
        verify(articleRepository).save(existing);
        verifyNoMoreInteractions(articleRepository);
    }

    // ... existing code ...

    @Test
    @DisplayName("updatePrix - retourne null quand introuvable")
    void updatePrix_returnsNull_whenMissing() {
        Integer artId = 31;
        when(articleRepository.findById(artId)).thenReturn(Optional.empty());

        Article result = articleService.updatePrix(artId, 100.0);

        assertNull(result);
        verify(articleRepository).findById(artId);
        verifyNoMoreInteractions(articleRepository);
    }
}