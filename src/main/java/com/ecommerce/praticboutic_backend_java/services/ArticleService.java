package com.ecommerce.praticboutic_backend_java.services;

import com.ecommerce.praticboutic_backend_java.entities.Article;
import com.ecommerce.praticboutic_backend_java.repositories.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Service gérant les opérations liées aux articles
 */
@Service
@Transactional
public class ArticleService {

    @Autowired
    private ArticleRepository articleRepository;
    
    /**
     * Récupère les articles pour une boutique et une catégorie données
     * Correspond à la requête "articles" dans le fichier PHP original
     * 
     * @param bouticId L'identifiant de la boutique
     * @param catId L'identifiant de la catégorie (peut être null pour tous les articles)
     * @return Liste des articles sous forme de tableaux d'objets
     */
    public List<List<Object>> getArticles(Integer bouticId, Integer catId) {
        List<Article> articles;
        
        // Récupère les articles d'une catégorie spécifique
        articles = articleRepository.findByCustomidAndCatid(bouticId, catId);

        List<List<Object>> result = new ArrayList<>();
        
        for (Article article : articles) {
            if (article.getVisible() == 1) {
                List<Object> articleArray = Arrays.asList(
                    article.getArtid(),
                    article.getNom(),
                    article.getPrix(),
                    article.getUnite(),
                    article.getDescription()
                );
                result.add(articleArray);
            }
        }
        
        return result;
    }
    
    /**
     * Trouve un article par son identifiant
     * 
     * @param artId L'identifiant de l'article
     * @return L'article trouvé ou empty si non trouvé
     */
    public Optional<Article> findById(Integer artId) {
        return articleRepository.findById(artId);
    }
    
    /**
     * Récupère un article par son identifiant
     * 
     * @param artId L'identifiant de l'article
     * @return L'article ou null si non trouvé
     */
    public Article getArticleById(Integer artId) {
        return articleRepository.findById(artId).orElse(null);
    }
    
    /**
     * Sauvegarde un article
     * 
     * @param article L'article à sauvegarder
     * @return L'article sauvegardé
     */
    public Article save(Article article) {
        return articleRepository.save(article);
    }
    
    /**
     * Supprime un article
     * 
     * @param artId L'identifiant de l'article à supprimer
     */
    public void delete(Integer artId) {
        articleRepository.deleteById(artId);
    }
    
    /**
     * Vérifie si un article existe
     * 
     * @param artId L'identifiant de l'article
     * @return true si l'article existe, false sinon
     */
    public boolean exists(Integer artId) {
        return articleRepository.existsById(artId);
    }
    
    /**
     * Récupère tous les articles d'une boutique
     * 
     * @param bouticId L'identifiant de la boutique
     * @return Liste des articles
     */
    public List<Article> getAllArticlesByBoutic(Integer bouticId) {
        return articleRepository.findByCustomid(bouticId);
    }
    
    /**
     * Met à jour la visibilité d'un article
     * 
     * @param artId L'identifiant de l'article
     * @param visible Nouvel état de visibilité
     * @return L'article mis à jour ou null si non trouvé
     */
    public Article updateVisibility(Integer artId, boolean visible) {
        Optional<Article> optionalArticle = articleRepository.findById(artId);
        if (optionalArticle.isPresent()) {
            Article article = optionalArticle.get();
            article.setVisible(visible ? 1 : 0);
            return articleRepository.save(article);
        }
        return null;
    }
    
    /**
     * Met à jour le prix d'un article
     * 
     * @param artId L'identifiant de l'article
     * @param prix Nouveau prix
     * @return L'article mis à jour ou null si non trouvé
     */
    public Article updatePrix(Integer artId, Double prix) {
        Optional<Article> optionalArticle = articleRepository.findById(artId);
        if (optionalArticle.isPresent()) {
            Article article = optionalArticle.get();
            article.setPrix(prix);
            return articleRepository.save(article);
        }
        return null;
    }
}