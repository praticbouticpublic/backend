package com.ecommerce.praticboutic_backend_java.services;

import com.ecommerce.praticboutic_backend_java.entities.Parametre;
import com.ecommerce.praticboutic_backend_java.repositories.ParametreRepository;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class ParameterService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private ParametreRepository parametreRepository;

    // Déclarez le logger en tant que champ statique en haut de votre classe
    private static final Logger logger = LoggerFactory.getLogger(ParameterService.class);



    public String getParameterValue(String paramName, Integer bouticId) {
        String sql = "SELECT valeur FROM parametre WHERE nom = ? AND customid = ?";
        try {
            return jdbcTemplate.queryForObject(sql, String.class, paramName, bouticId);
        } catch (Exception e) {
            return "";
        }
    }

    public String getValeur(String paramName, Integer bouticId) {
        String sql = "SELECT valeur FROM parametre WHERE nom = ? AND customid = ?";

        List<String> result = jdbcTemplate.query(
                sql,
                new Object[]{paramName, bouticId},
                (rs, rowNum) -> rs.getString("valeur")
        );

        if (!result.isEmpty()) {
            return result.get(0);
        } else {
            return "";
        }
    }

    public void setValeur(String paramName, String paramValue, Integer bouticId) {
        // Implémentation pour définir la valeur d'un paramètre
        String checkSql = "SELECT COUNT(*) FROM parametre WHERE nom = ? AND customid = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, paramName, bouticId);

        if (count == null || count == 0) {
            // Insert
            String insertSql = "INSERT INTO parametre (nom, valeur, customid) VALUES (?, ?, ?)";
            jdbcTemplate.update(insertSql, paramName, paramValue, bouticId);
        } else {
            // Update
            String updateSql = "UPDATE parametre SET valeur = ? WHERE nom = ? AND customid = ?";
            jdbcTemplate.update(updateSql, paramValue, paramName, bouticId);
        }
    }


    public String getValeurParam(String param, Integer bouticId, String defaultValue) throws SQLException {
            String query = "SELECT valeur FROM parametre WHERE nom = ? AND customid = ?";

            try {
                return jdbcTemplate.queryForObject(
                        query,
                        String.class,
                        param,
                        bouticId
                );
            } catch (org.springframework.dao.EmptyResultDataAccessException e) {
                return defaultValue;
            }
    }

    public boolean setValeurParam(String param, int bouticId, String valeur) throws SQLException {
            String query = "UPDATE parametre SET valeur = ? WHERE nom = ? AND customid = ?";

            int rowsUpdated = jdbcTemplate.update(query, valeur, param, bouticId);
            return rowsUpdated > 0;
    }

    public List<?> getParam(String param, Integer bouticid) {
        return null;
    }

    /**
     * Méthode pour créer les paramètres par défaut d'une boutique
     */
    public void createDefaultParameters(Integer customId, String token) {
        Map<String, Object> payload = JwtService.parseToken(token).getClaims();

        List<Parametre> parametres = Arrays.asList(
                new Parametre(customId, "isHTML_mail", "1", "HTML activé pour l'envoi de mail"),
                new Parametre(customId, "Subject_mail", "Commande Praticboutic", "Sujet du courriel pour l'envoi de mail"),
                new Parametre(customId, "VALIDATION_SMS", payload.get("confboutic_validsms").toString(), "Commande validée par sms ?"),
                new Parametre(customId, "VerifCP", "0", "Activation de la verification des codes postaux"),
                new Parametre(customId, "Choix_Paiement", payload.get("confboutic_chxpaie").toString(), "COMPTANT ou LIVRAISON ou TOUS"),
                new Parametre(customId, "MP_Comptant", "Par carte bancaire", "Texte du paiement comptant"),
                new Parametre(customId, "MP_Livraison", "Moyens conventionnels", "Texte du paiement à la livraison"),
                new Parametre(customId, "Choix_Method", payload.get("confboutic_chxmethode").toString(), "TOUS ou EMPORTER ou LIVRER"),
                new Parametre(customId, "CM_Livrer", "Vente avec livraison", "Texte de la vente à la livraison"),
                new Parametre(customId, "CM_Emporter", "Vente avec passage à la caisse", "Texte de la vente à emporter"),
                new Parametre(customId, "MntCmdMini", payload.get("confboutic_mntmincmd").toString(), "Montant commande minimal"),
                new Parametre(customId, "SIZE_IMG", "smallimg", "bigimg ou smallimg"),
                new Parametre(customId, "CMPT_CMD", "1", "Compteur des références des commandes"),
                new Parametre(customId, "MONEY_SYSTEM", "STRIPE MARKETPLACE", ""),
                new Parametre(customId, "STRIPE_ACCOUNT_ID", "", "ID Compte connecté Stripe"),
                new Parametre(customId, "NEW_ORDER", "0", "Nombre de nouvelle(s) commande(s)"),
                new Parametre(customId, "DATE_CREATION", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), "Date de création")
        );

        try {
            parametreRepository.saveAll(parametres);
            logger.debug("Paramètres par défaut créés pour la boutique: {}", customId);
        } catch (DataAccessException e) {
            logger.error("Erreur lors de la création des paramètres par défaut", e);
            throw e;
        }
    }
}