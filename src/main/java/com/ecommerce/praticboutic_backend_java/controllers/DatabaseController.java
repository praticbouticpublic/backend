package com.ecommerce.praticboutic_backend_java.controllers;



import com.ecommerce.praticboutic_backend_java.exceptions.DatabaseException;
import com.ecommerce.praticboutic_backend_java.models.BaseEntity;
import com.ecommerce.praticboutic_backend_java.models.ColumnData;
import com.ecommerce.praticboutic_backend_java.repositories.*;
import com.ecommerce.praticboutic_backend_java.responses.ErrorResponse;
import com.ecommerce.praticboutic_backend_java.services.*;
import com.stripe.exception.StripeException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import com.ecommerce.praticboutic_backend_java.entities.*;
import com.ecommerce.praticboutic_backend_java.requests.*;
import com.ecommerce.praticboutic_backend_java.utils.Utils;

import java.util.*;
import java.util.Locale;
import java.lang.reflect.Field;
import java.text.NumberFormat;
import java.lang.Integer;

import jakarta.servlet.http.HttpSession;
import jakarta.persistence.*;

import org.hibernate.SessionFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.*;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
@RequestMapping("/api")
public class DatabaseController {
    // Déclarez le logger en tant que champ statique en haut de votre classe
    private static final Logger logger = LoggerFactory.getLogger(DatabaseController.class);

    private static SessionFactory sessionFactory = null;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    protected CustomerRepository customerRepository;

    @Autowired
    protected StripeService stripeService;

    @Autowired
    protected ClientRepository clientRepository;

    @Autowired
    protected ParameterService paramService;

    @Autowired
    protected ClientService clientService;

    @Autowired
    protected BouticService bouticService;

    @Autowired
    protected CustomerService customerService;

    @Autowired
    protected AbonnementService abonnementService;

    @Autowired
    protected StatutCmdService statutCmdService;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    protected JwtService jwtService;


    //public DatabaseController() {}

    @PostMapping("/count-elements")
    public ResponseEntity<?> countElementsInTable(@RequestBody VueTableRequest input, HttpServletRequest httpRequest, @RequestHeader("Authorization") String authHeader) {
        Map<String, Object> response = new HashMap<>();
        try {
            String token = authHeader.replace("Bearer ", "");

            Map <java.lang.String, java.lang.Object> payload = JwtService.parseToken(token).getClaims();
            // Vérifier l'authentification
            if (!jwtService.isAuthenticated(payload)) {
                throw new Exception("Non authentifié");
            }
            String strTable = input.getTable(); Integer iBouticid = input.getBouticid();
            String strSelcol = input.getSelcol(); Integer iSelid = input.getSelid();
            Integer iLimit = input.getLimite(); Integer iOffset = input.getOffset();
            // Vérification si le nom de la table est fourni
            if (strTable == null || strTable.isEmpty()) {
                throw new Exception("Le nom de la table est vide.");
            }
            Class<?> entityClass;
            try {
                sessionFactory = entityManager.getEntityManagerFactory()
                        .unwrap(SessionFactory.class);
                entityClass = BaseEntity.getEntityClassFromTableName(sessionFactory, input.getTable());

            } catch (Throwable ex) {
                throw new ExceptionInInitializerError(ex);
            }
            // Création de la requête avec le EntityManager
            StringBuilder queryBuilder = new StringBuilder("SELECT COUNT(*) FROM `");
            queryBuilder.append(strTable);
            queryBuilder.append("` e WHERE e.customid = :bouticid");
            // Ajout de conditions supplémentaires
            boolean bSel = (strSelcol != null && !strSelcol.isEmpty() && iSelid != null && iSelid > 0);
            if (bSel) queryBuilder.append(" AND e.").append(strSelcol).append(" = :selid");
            // Création de la requête dynamique
            Query query = entityManager.createNativeQuery(queryBuilder.toString());
            // Paramètres de la requête
            query.setParameter("bouticid", iBouticid);
            if (bSel) query.setParameter("selid", iSelid);
            // Exécution de la requête
            Long count = (Long) query.getSingleResult();
            response.put("count", count);
            response.put("entity", strTable);
        } catch (Exception e) {
            response.put("error", "Erreur lors de l'exécution : " + e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/vue-table")
    public ResponseEntity<?> vueTable(@RequestBody VueTableRequest input, HttpServletRequest httpRequest,
                                      @RequestHeader("Authorization") String authHeader) {
        Map<String, Object> response = new HashMap<>();
        String token = authHeader.replace("Bearer ", "");

        Map <java.lang.String, java.lang.Object> payload = JwtService.parseToken(token).getClaims();

        if (!jwtService.isAuthenticated(payload)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Non authentifié"));
        }
        try {
            // Variables d'entrée
            String strTable = input.getTable();
            Integer iBouticid = input.getBouticid();
            String strSelcol = input.getSelcol();
            Integer iSelid = input.getSelid();
            Integer iLimit = input.getLimite();
            Integer iOffset = input.getOffset();
            // Validation des données d'entrée
            if (strTable == null || strTable.isEmpty()) {
                throw new IllegalArgumentException("Le nom de la table est vide.");
            }
            Class<?> entityClass;
            try {
                sessionFactory = entityManager.getEntityManagerFactory()
                        .unwrap(SessionFactory.class);
                entityClass = BaseEntity.getEntityClassFromTableName(sessionFactory, input.getTable());

            } catch (Throwable ex) {
                throw new ExceptionInInitializerError(ex);
            }
            ArrayList<Object> data = new ArrayList<>();
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("SELECT ").append(BaseEntity.getPrimaryKeyName(sessionFactory, entityManager, strTable))
                    .append(" FROM `").append(strTable)
                    .append("` WHERE customid = ").append(iBouticid);


            if (strSelcol != null && !strSelcol.isEmpty() && iSelid != null) {
                queryBuilder.append(" AND ").append(strSelcol).append(" = ").append(iSelid);
            }

            queryBuilder.append(" LIMIT ").append(iLimit).append(" OFFSET ").append(iOffset);

            Query query = entityManager.createNativeQuery(queryBuilder.toString());
            for (Object primaryKey : query.getResultList()) {
                Object entityInstance = entityManager.find(entityClass, primaryKey);
                Object ret = entityClass.getDeclaredMethod("getDisplayData").invoke(entityInstance);
                data.add(ret);
            }
            // Construction de la réponse
            response.put("data", data);
            response.put("count", data.size());
            response.put("status", "success");
        } catch (Exception e2) {
            response.put("error", "Erreur lors de l'exécution : " + e2.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/remplir-options")
    public ResponseEntity<?> remplirOption(@RequestBody RemplirOptionTableRequest input, HttpServletRequest httpRequest,
                                           @RequestHeader("Authorization") String authHeader)
    {
        Map<String, Object> response = new HashMap<>();
        String token = authHeader.replace("Bearer ", "");
        Map <java.lang.String, java.lang.Object> payload = JwtService.parseToken(token).getClaims();
        if (!jwtService.isAuthenticated(payload)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Non authentifié"));
        }
        try {
            String strClePrimaire = null;
            // Variables d'entrée
            String tableName = input.getTable(); Long idBoutic = input.getBouticid(); String strColonne = input.getColonne();
            // Validation des données d'entrée
            if (tableName == null || tableName.isEmpty()) {
                throw new Exception("Le nom de la table est vide.");
            }
            Class<?> entityClass;
            try {
                sessionFactory = entityManager.getEntityManagerFactory()
                        .unwrap(SessionFactory.class);
                entityClass = BaseEntity.getEntityClassFromTableName(sessionFactory, input.getTable());

            } catch (Throwable ex) {
                throw new ExceptionInInitializerError(ex);
            }
            strClePrimaire = BaseEntity.getPrimaryKeyName(sessionFactory, entityManager, tableName);
            if (strClePrimaire == null) {
                throw new IllegalArgumentException("Aucune clé primaire trouvée pour cette table");
            }
            // Création de la requête SQL
            StringBuilder sbQueryRemplirOption = new StringBuilder("SELECT ")
                    .append(strClePrimaire).append(", ")
                    .append(input.getColonne())
                    .append(" FROM `").append(tableName).append("`")
                    .append(" WHERE customid = ").append(idBoutic)
                    .append(" OR ").append(strClePrimaire).append(" = 0");
            if ("statutcmd".equals(tableName)) {
                sbQueryRemplirOption.append(" AND actif = 1");
            }
            Query queryRemplirOption = entityManager.createNativeQuery(sbQueryRemplirOption.toString(), Object[].class);
            List<?> results = queryRemplirOption.getResultList();
            response.put("results", results);
        }
        catch(Exception e2)
        {
            response.put("error", "Erreur lors de l'exécution : " + e2.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Insère une nouvelle ligne dans une table spécifiée
     *
     * @param input La requête contenant les informations d'insertion
     * @return Une Map contenant le résultat de l'opération
     */
    @PostMapping("/insert-row")
    @Transactional
    public ResponseEntity<?> insertRow(@RequestBody InsertRowRequest input, HttpServletRequest httpRequest,
                                       @RequestHeader("Authorization") String authHeader) {
        Map<String, Object> response = new HashMap<>();
        String token = authHeader.replace("Bearer ", "");
        Map <java.lang.String, java.lang.Object> payload = JwtService.parseToken(token).getClaims();
        try {
            if (!jwtService.isAuthenticated(payload)) {
                throw new Exception("Non authentifié");
            }
            // Validation de l'entrée
            if (input.getTable() == null || input.getTable().isEmpty()) {
                throw new Exception("Le nom de la table est requis");
            }
            if (input.getBouticid() == null) {
                throw new Exception("L'ID boutic est requis");
            }
            if (input.getRow() == null || input.getRow().isEmpty()) {
                throw new Exception( "Les données à insérer sont requises");
            }
            // Construction dynamique de la classe d'entité
            Class<?> entityClass;
            try {
                sessionFactory = entityManager.getEntityManagerFactory()
                        .unwrap(SessionFactory.class);
                entityClass = BaseEntity.getEntityClassFromTableName(sessionFactory, input.getTable());
            } catch (ClassNotFoundException ex) {
                response.put("error", "L'entité spécifiée n'existe pas : " + input.getTable());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // Vérifier les contraintes d'unicité
            for (ColumnData column : input.getRow()) {
                try {
                    Field field = entityClass.getDeclaredField(column.getNom());
                    Column columnAnnotation = field.getAnnotation(Column.class);
                    if (columnAnnotation != null && columnAnnotation.unique()) {
                        String jpql = "SELECT COUNT(e) FROM `" + entityClass.getSimpleName() + "` e " +
                                "WHERE e.customid = :customid AND e." + column.getNom() + " = :valeur";
                        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
                        query.setParameter("customid", input.getBouticid());
                        query.setParameter("valeur", column.getValeur());
                        if (query.getSingleResult() > 0)
                        {
                            response.put("error", "Impossible d'avoir plusieurs fois la valeur '" +
                                    column.getValeur() + "' dans la colonne '" +
                                    column.getDesc() + "'");
                            return ResponseEntity.status(HttpStatus.CONFLICT)
                                    .body(response);
                        }
                    }
                } catch (NoSuchFieldException e) {
                    // Ignorer les champs qui n'existent pas dans l'entité
                    continue;
                }
            }
            // Utiliser une requête native pour l'insertion
            StringBuilder columns = new StringBuilder("customid");
            StringBuilder placeholders = new StringBuilder("?");
            List<Object> values = new ArrayList<>();
            values.add(input.getBouticid());

            for (ColumnData column : input.getRow()) {
                columns.append(", `").append(column.getNom()).append("`");
                placeholders.append(", ?");

                Object value = column.getValeur();
                if ("pass".equals(column.getType())) {
                    value = new BCryptPasswordEncoder().encode(column.getValeur());
                }
                values.add(value);
            }

            String insertSql = "INSERT INTO `" + input.getTable() + "` (" + columns + ") VALUES (" + placeholders + ")";



            Query insertQuery = entityManager.createNativeQuery(insertSql);
            for (int i = 0; i < values.size(); i++) {
                insertQuery.setParameter(i + 1, values.get(i));
            }
            int result = insertQuery.executeUpdate();

            // Récupérer l'ID généré
            Query idQuery = entityManager.createNativeQuery("SELECT LAST_INSERT_ID()");
            Long lastId = ((Number) idQuery.getSingleResult()).longValue();

            response.put("id", lastId);
            response.put("success", result > 0);

        } catch (Exception e) {
            response.put("error", e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Met à jour une ligne existante dans une table spécifiée
     *
     * @param input La requête contenant les informations de mise à jour
     * @return Une Map contenant le résultat de l'opération
     */
    @PostMapping("/update-row")
    @Transactional
    public ResponseEntity<?> updateRow(@RequestBody UpdateRowRequest input, HttpServletRequest httpRequest,
                                       @RequestHeader("Authorization") String authHeader) {
        Map<String, Object> response = new HashMap<>();
        String token = authHeader.replace("Bearer ", "");
        Map <java.lang.String, java.lang.Object> payload = JwtService.parseToken(token).getClaims();

        try {
            if (!jwtService.isAuthenticated(payload)) {
                throw new Exception( "Non authentifié");
            }
            // Validation de l'entrée
            if (input.getTable() == null || input.getTable().isEmpty()) {
                throw new Exception("Le nom de la table est requis");
            }
            if (input.getBouticid() == null) {
                throw new Exception("L'ID boutic est requis");
            }
            if (input.getRow() == null || input.getRow().isEmpty()) {
                throw new Exception("Les données à mettre à jour sont requises");
            }
            if (input.getIdtoup() == null) {
                throw new Exception("L'ID de l'élément à mettre à jour est requis");
            }
            if (input.getColonne() == null || input.getColonne().isEmpty()) {
                throw new Exception("Le nom de la colonne d'ID est requis");
            }

            // Construction dynamique de la classe d'entité
            Class<?> entityClass;
            try {
                sessionFactory = entityManager.getEntityManagerFactory().unwrap(SessionFactory.class);
                entityClass = BaseEntity.getEntityClassFromTableName(sessionFactory, input.getTable());
            } catch (ClassNotFoundException ex) {
                response.put("error", "L'entité spécifiée n'existe pas : " + input.getTable());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // Vérifier les contraintes d'unicité
            for (ColumnData column : input.getRow()) {
                Field field = entityClass.getDeclaredField(column.getNom());
                Column columnAnnotation = field.getAnnotation(Column.class);
                if (columnAnnotation != null && columnAnnotation.unique()) {
                    String jpql = "SELECT COUNT(e) FROM " + entityClass.getSimpleName() + " e " +
                            "WHERE e.customid = :customid AND e." + column.getNom() +
                            " = :valeur AND e." + input.getColonne() + " != :idtoup";

                    TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
                    query.setParameter("customid", input.getBouticid());
                    query.setParameter("valeur", column.getValeur());
                    query.setParameter("idtoup", input.getIdtoup());

                    Long lCount = query.getSingleResult();
                    if (lCount > 0) {
                        response.put("error", "Impossible d'avoir plusieurs fois la valeur '" +
                                column.getValeur() + "' dans la colonne '" +
                                column.getDesc() + "'");
                        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
                    }
                }
            }

            // Préparer et exécuter la mise à jour
            StringBuilder jpql = new StringBuilder("UPDATE " + entityClass.getSimpleName() + " e SET ");

            for (int i = 0; i < input.getRow().size(); i++) {
                ColumnData column = input.getRow().get(i);
                jpql.append("e.").append(column.getNom()).append(" = :").append(column.getNom());

                if (i < input.getRow().size() - 1) {
                    jpql.append(", ");
                }
            }

            jpql.append(" WHERE e.customid = :customid AND e.").append(input.getColonne()).append(" = :idtoup");

            Query updateQuery = entityManager.createQuery(jpql.toString());

            // Set parameters
            for (ColumnData column : input.getRow()) {
                Object value = column.getValeur();
                if ("pass".equals(column.getType())) {
                    value = new BCryptPasswordEncoder().encode(column.getValeur());
                }
                if ("bool".equals(column.getType())) {
                    value = column.getValeur().equals("1") ? 1 : 0;
                }
                updateQuery.setParameter(column.getNom(), value);

            }

            updateQuery.setParameter("customid", input.getBouticid());
            updateQuery.setParameter("idtoup", input.getIdtoup());

            int updatedCount = updateQuery.executeUpdate();
            response.put("success", updatedCount > 0);

        } catch (Exception e) {
            response.put("error", e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Récupère les valeurs d'une ligne spécifique dans une table
     *
     * @param input La requête contenant les informations pour récupérer les valeurs
     * @return Une Map contenant le résultat de l'opération
     */
    @PostMapping("/get-values")
    public ResponseEntity<?> getValues(@RequestBody GetValuesRequest input, HttpServletRequest httpRequest
            , @RequestHeader("Authorization") String authHeader) {
        Map<String, Object> response = new HashMap<>();
        String token = authHeader.replace("Bearer ", "");
        Map <java.lang.String, java.lang.Object> payload = JwtService.parseToken(token).getClaims();
        try {
            if (!jwtService.isAuthenticated(payload)) {
                throw new Exception("Non authentifié");
            }
            // Validation de l'entrée
            if (input.getTable() == null || input.getTable().isEmpty()) {
                throw new Exception("Le nom de la table est requis");
            }
            if (input.getBouticid() == null) {
                throw new Exception("L'ID boutic est requis");
            }
            if (input.getIdtoup() == null) {
                throw new Exception("L'ID de l'élément est requis");
            }
            // Construction dynamique de la classe d'entité
            Class<?> entityClass;
            try {
                sessionFactory = entityManager.getEntityManagerFactory().unwrap(SessionFactory.class);
                entityClass = BaseEntity.getEntityClassFromTableName(sessionFactory, input.getTable());
            } catch (ClassNotFoundException ex) {
                response.put("error", "L'entité spécifiée n'existe pas : " + input.getTable());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            String strClePrimaire = BaseEntity.getPrimaryKeyName(sessionFactory, entityManager, input.getTable());
            if (strClePrimaire == null) {
                throw new IllegalArgumentException("Aucune clé primaire trouvée pour cette table");
            }
            StringBuilder sbQuerySelect = new StringBuilder("SELECT ");
            for (Field field : entityClass.getDeclaredFields() ) {
                if ((field.getAnnotation(Column.class) != null) && (!field.getName().equals("customid"))) {
                    if (!field.equals(entityClass.getDeclaredFields()[0])) sbQuerySelect.append(", ");
                    sbQuerySelect.append("`").append(field.getName()).append("`");
                }
            }
            sbQuerySelect.append(" FROM `").append(input.getTable()).append("` WHERE ");
            sbQuerySelect.append(strClePrimaire).append(" = ").append(input.getIdtoup());
            sbQuerySelect.append(" AND ").append("customid = ").append(input.getBouticid());
            Query qSelect = entityManager.createNativeQuery(sbQuerySelect.toString());

            List<?> results = qSelect.getResultList();

            if (!results.isEmpty()) {
                response.put("values", results.get(0)); // ou `results` si tu veux tout renvoyer
            } else {
                response.put("values", Collections.emptyList());
            }
            response.put("success", true);
        } catch (Exception e) {

            response.put("error", e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Récupère les couleurs associées aux commandes
     *
     * @param input La requête contenant les paramètres de pagination et l'ID boutic
     * @return Une Map contenant le résultat de l'opération avec les couleurs
     */
    @PostMapping("/color-row")
    public ResponseEntity<?> getOrderColors(@RequestBody ColorRowRequest input, HttpServletRequest httpRequest,
                                            @RequestHeader("Authorization") String authHeader) {
        Map<String, Object> response = new HashMap<>();
        String token = authHeader.replace("Bearer ", "");
        Map <java.lang.String, java.lang.Object> payload = JwtService.parseToken(token).getClaims();

        try {
            if (!jwtService.isAuthenticated(payload)) {
                throw new Exception("Non authentifié");
            }
            // Validation de l'entrée
            if (input.getBouticid() == null) {
                throw new Exception("L'ID boutic est requis");
            }

            if (input.getLimite() == null || input.getLimite() <= 0) {
                throw new Exception("La limite est requise et doit être positive");
            }

            if (input.getOffset() == null || input.getOffset() < 0) {
                throw new Exception("L'offset est requis et doit être non négatif");
            }

            // Créer la requête JPQL
            String jpql = "SELECT s.couleur FROM Commande c " +
                    "INNER JOIN StatutCmd s ON c.statid = s.statid " +
                    "WHERE c.customid = :customid " +
                    "ORDER BY c.cmdid";

            TypedQuery<String> query = entityManager.createQuery(jpql, String.class);
            query.setParameter("customid", input.getBouticid());
            query.setFirstResult(input.getOffset());
            query.setMaxResults(input.getLimite());

            // Exécuter la requête et récupérer les résultats
            List<String> colors = query.getResultList();

            // Transformer les résultats pour correspondre au format attendu
            List<List<String>> formattedResults = new ArrayList<>();
            for (String color : colors) {
                List<String> colorWrapper = new ArrayList<>();
                colorWrapper.add(color);
                formattedResults.add(colorWrapper);
            }

            response.put("colors", formattedResults);
            response.put("success", true);

        } catch (Exception e) {
            response.put("error", e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Récupère les données d'une commande et formate un message avec ces données
     *
     * @param input La requête contenant l'ID de la commande et l'ID boutic
     * @return Une Map contenant le résultat de l'opération
     */
    @PostMapping("/get-com-data")
    public ResponseEntity<?> getOrderData(@RequestBody GetComDataRequest input, HttpServletRequest httpRequest,
                                          @RequestHeader("Authorization") String authHeader) {
        Map<String, Object> response = new HashMap<>();
        String token = authHeader.replace("Bearer ", "");
        Map <java.lang.String, java.lang.Object> payload = JwtService.parseToken(token).getClaims();

        try {
            if (!jwtService.isAuthenticated(payload)) {
                throw new Exception("Non authentifié");
            }
            // Validation de l'entrée
            if (input.getCmdid() == null) {
                throw new Exception("L'ID de la commande est requis");
            }

            if (input.getBouticid() == null) {
                throw new Exception("L'ID boutic est requis");
            }

            // Créer la requête JPQL pour récupérer les données
            String jpql = "SELECT c.telephone, s.message, c.numref, c.nom, c.prenom, " +
                    "c.adresse1, c.adresse2, c.codepostal, c.ville, c.vente, " +
                    "c.paiement, c.sstotal, c.fraislivraison, c.total, c.commentaire, " +
                    "s.etat, cust.nom " +
                    "FROM Commande c " +
                    "INNER JOIN StatutCmd s ON c.statid = s.statid " +
                    "INNER JOIN Customer cust ON c.customid = cust.customid " +
                    "WHERE c.cmdid = :cmdid AND c.customid = :customid " +
                    "AND s.customid = :customid AND cust.customid = :customid " +
                    "ORDER BY c.cmdid";

            Query query = entityManager.createQuery(jpql);
            query.setParameter("cmdid", input.getCmdid());
            query.setParameter("customid", input.getBouticid());

            // Exécuter la requête et récupérer le résultat
            Object[] row;
            try {
                row = (Object[]) query.getSingleResult();
            } catch (NoResultException e) {
                response.put("error", "Aucune commande trouvée avec l'ID " + input.getCmdid());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // Formater le message en remplaçant les variables
            String content = (String) row[1]; // message

            // Remplacer toutes les variables par leurs valeurs
            content = content.replace("%boutic%", (String) row[16]); // nom du customer
            content = content.replace("%telephone%", (String) row[0]); // téléphone
            content = content.replace("%numref%", (String) row[2]); // numref
            content = content.replace("%nom%", (String) row[3]); // nom
            content = content.replace("%prenom%", (String) row[4]); // prenom
            content = content.replace("%adresse1%", (String) row[5]); // adresse1
            content = content.replace("%adresse2%", (String) row[6] != null ? (String) row[6] : ""); // adresse2
            content = content.replace("%codepostal%", (String) row[7]); // codepostal
            content = content.replace("%ville%", (String) row[8]); // ville
            content = content.replace("%vente%", (String) row[9]); // vente
            content = content.replace("%paiement%", (String) row[10]); // paiement

            // Formater les montants (nombre avec 2 décimales, virgule comme séparateur décimal et espace pour les milliers)
            NumberFormat formatter = NumberFormat.getNumberInstance(Locale.FRANCE);
            formatter.setMinimumFractionDigits(2);
            formatter.setMaximumFractionDigits(2);

            Double sstotal = (Double) row[11];
            Double fraislivraison = (Double) row[12];
            Double total = (Double) row[13];

            content = content.replace("%sstotal%", formatter.format(sstotal)); // sstotal
            content = content.replace("%fraislivraison%", formatter.format(fraislivraison)); // fraislivraison
            content = content.replace("%total%", formatter.format(total)); // total

            content = content.replace("%commentaire%", row[14] != null ? (String) row[14] : ""); // commentaire
            content = content.replace("%etat%", (String) row[15]); // etat

            String message = content;

            // Créer le tableau de réponse
            List<String> result = new ArrayList<>();
            result.add((String) row[0]); // téléphone
            result.add(message); // message formaté

            response.put("data", result);
            response.put("success", true);

        } catch (Exception e) {
            response.put("error", e.getMessage());
        }

        return ResponseEntity.ok(response);
    }


    @PostMapping("/get-param")
    public ResponseEntity<?> getParam(@RequestBody ParamRequest request, HttpServletRequest httpRequest,
                                      @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Map <java.lang.String, java.lang.Object> payload = JwtService.parseToken(token).getClaims();
        if (request.getParam() == null || request.getBouticid() == null) {
            return ResponseEntity.badRequest().body("Missing parameters");
        }

        try {
            if (!jwtService.isAuthenticated(payload)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Non authentifié"));
            }
            String value = paramService.getValeurParam(request.getParam(), Integer.parseInt(request.getBouticid()),"");
            return ResponseEntity.ok(Collections.singletonMap("value", value));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching parameter");
        }
    }

    @PostMapping("/set-param")
    public ResponseEntity<?> setParam(@RequestBody ParamRequest request, HttpServletRequest httpRequest,
                                      @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Map <java.lang.String, java.lang.Object> payload = JwtService.parseToken(token).getClaims();
        if (request.getParam() == null || request.getValeur() == null || request.getBouticid() == null) {
            return ResponseEntity.badRequest().body("Missing parameters");
        }

        try {
            if (!jwtService.isAuthenticated(payload)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Non authentifié"));
            }
            boolean success = paramService.setValeurParam(request.getParam(), Integer.parseInt(request.getBouticid()), request.getValeur());
            return ResponseEntity.ok(Collections.singletonMap("success", success));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error setting parameter");
        }
    }
    /**
     * Récupère une propriété spécifique d'un customer
     */
    @PostMapping("/get-custom-prop")
    public ResponseEntity<?> getCustomProperty(@RequestBody CustomPropertyRequest input, HttpServletRequest httpRequest,
                                               @RequestHeader("Authorization") String authHeader) {
        Map<String, Object> response = new HashMap<>();
        String token = authHeader.replace("Bearer ", "");
        Map <java.lang.String, java.lang.Object> payload = JwtService.parseToken(token).getClaims();

        try {
            if (!jwtService.isAuthenticated(payload)) {
                throw new Exception("Non authentifié");
            }
            // Validation des paramètres
            if (input.getBouticid() == null) {
                throw new Exception("L'ID boutic est requis");
            }

            if (input.getProp() == null || input.getProp().isEmpty()) {
                throw new Exception("La propriété à récupérer est requise");
            }

            // Vérification pour éviter l'injection SQL
            Utils.validatePropertyName(input.getProp());

            // Création de la requête JPQL
            String jpql = "SELECT c." + input.getProp() + " FROM Customer c " +
                    "WHERE c.customid = :customid";

            Query query = entityManager.createQuery(jpql);
            query.setParameter("customid", input.getBouticid());
            query.setMaxResults(1);

            // Exécution de la requête
            Object result;
            try {
                result = query.getSingleResult();
            } catch (NoResultException e) {
                response.put("error", "Aucun customer trouvé avec l'ID " + input.getBouticid());
                return ResponseEntity.ok(response);
            } catch (IllegalArgumentException e) {
                response.put("error", "Propriété invalide: " + input.getProp());
                return ResponseEntity.badRequest().body(response);
            }

            // Construction de la réponse
            List<Object> values = new ArrayList<>();
            values.add(result);

            response.put("value", values);
            response.put("success", true);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", "Une erreur est survenue: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/set-custom-prop")
    @Transactional
    public ResponseEntity<?> setCustomProperty(@RequestBody CustomPropertyUpdateRequest input, HttpServletRequest httpRequest,
                                               @RequestHeader("Authorization") String authHeader) {
        Map<String, Object> response = new HashMap<>();
        String token = authHeader.replace("Bearer ", "");
        Map <java.lang.String, java.lang.Object> payload = JwtService.parseToken(token).getClaims();

        try {
            if (!jwtService.isAuthenticated(payload)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Non authentifié"));
            }
            // Validation des paramètres
            if (input.getBouticid() == null) {
                response.put("error", "L'ID boutic est requis");
                return ResponseEntity.badRequest().body(response);
            }

            if (input.getProp() == null || input.getProp().isEmpty()) {
                response.put("error", "La propriété à mettre à jour est requise");
                return ResponseEntity.badRequest().body(response);
            }

            if (input.getValeur() == null) {
                response.put("error", "La valeur de la propriété est requise");
                return ResponseEntity.badRequest().body(response);
            }

            // Vérification pour éviter l'injection SQL
            Utils.validatePropertyName(input.getProp());

            // Vérifier si la valeur existe déjà pour d'autres customers (pour le champ 'customer')
            if ("customer".equals(input.getProp())) {
                String checkJpql = "SELECT COUNT(c) FROM Customer c WHERE c." + input.getProp() + " = :valeur AND c.customid != :customid";

                Query checkQuery = entityManager.createQuery(checkJpql);
                checkQuery.setParameter("valeur", input.getValeur());
                checkQuery.setParameter("customid", input.getBouticid());

                Long count = (Long) checkQuery.getSingleResult();

                if (count >= 1) {
                    response.put("result", "KO");
                    return ResponseEntity.ok(response);
                }
            }

            // Mise à jour de la propriété - use parameterized query instead of string concatenation
            String updateJpql = "UPDATE Customer c SET c." + input.getProp() + " = :valeur WHERE c.customid = :customid";

            Query updateQuery = entityManager.createQuery(updateJpql);
            updateQuery.setParameter("valeur", input.getValeur());
            updateQuery.setParameter("customid", input.getBouticid());

            int updatedCount = updateQuery.executeUpdate();

            if (updatedCount > 0) {
                response.put("result", "OK");
            } else {
                response.put("result", "KO");
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("result", "KO");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Récupère une propriété spécifique d'un client associé à un customer
     */
    @PostMapping("/get-client-prop")
    public ResponseEntity<?> getClientProperty(@RequestBody ClientPropertyRequest input, HttpServletRequest httpRequest,
                                               @RequestHeader("Authorization") String authHeader) {
        Map<String, Object> response = new HashMap<>();
        String token = authHeader.replace("Bearer ", "");
        Map <java.lang.String, java.lang.Object> payload = JwtService.parseToken(token).getClaims();

        try {
            if (!jwtService.isAuthenticated(payload)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Non authentifié"));
            }
            // Validation des paramètres
            if (input.getBouticid() == null) {
                response.put("error", "L'ID boutic est requis");
                return ResponseEntity.badRequest().body(response);
            }

            if (input.getProp() == null || input.getProp().isEmpty()) {
                response.put("error", "La propriété à récupérer est requise");
                return ResponseEntity.badRequest().body(response);
            }

            // Vérification pour éviter l'injection SQL
            Utils.validatePropertyName(input.getProp());

            // Création de la requête JPQL
            String jpql = "SELECT cl." + input.getProp() + " FROM Customer c " +
                    "JOIN Client cl ON c.cltid = cl.cltid " +
                    "WHERE c.customid = :customid";

            Query query = entityManager.createQuery(jpql);
            query.setParameter("customid", input.getBouticid());
            query.setMaxResults(1);

            // Exécution de la requête
            Object result;
            try {
                result = query.getSingleResult();
            } catch (NoResultException e) {
                response.put("error", "Aucun client trouvé pour le customer avec l'ID " + input.getBouticid());
                return ResponseEntity.ok(response);
            } catch (IllegalArgumentException e) {
                response.put("error", "Propriété invalide: " + input.getProp());
                return ResponseEntity.badRequest().body(response);
            }

            // Construction de la réponse
            List<Object> values = new ArrayList<>();
            values.add(result);

            response.put("value", values);
            response.put("success", true);

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            response.put("error", "Une erreur est survenue: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Met à jour une propriété spécifique d'un client associé à un customer
     */
    @PostMapping("/set-client-prop")
    @Transactional
    public ResponseEntity<?> setClientProperty(@RequestBody ClientPropertyUpdateRequest input, HttpServletRequest httpRequest,
                                               @RequestHeader("Authorization") String authHeader) {
        Map<String, Object> response = new HashMap<>();
        String token = authHeader.replace("Bearer ", "");
        Map <java.lang.String, java.lang.Object> payload = JwtService.parseToken(token).getClaims();

        try {
            if (!jwtService.isAuthenticated(payload)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Non authentifié"));
            }
            // Validation des paramètres
            if (input.getBouticid() == null) {
                response.put("error", "L'ID boutic est requis");
                return ResponseEntity.badRequest().body(response);
            }

            if (input.getProp() == null || input.getProp().isEmpty()) {
                response.put("error", "La propriété à mettre à jour est requise");
                return ResponseEntity.badRequest().body(response);
            }

            if (input.getValeur() == null) {
                response.put("error", "La valeur de la propriété est requise");
                return ResponseEntity.badRequest().body(response);
            }

            // Vérification pour éviter l'injection SQL
            Utils.validatePropertyName(input.getProp());

            // Récupérer l'ID du client associé au customer
            String cltIdQuery = "SELECT c.client.cltid FROM Customer c WHERE c.customid = :customid";
            Query query = entityManager.createQuery(cltIdQuery);
            query.setParameter("customid", input.getBouticid());

            Integer cltid;
            try {
                cltid = (Integer) query.getSingleResult();
            } catch (NoResultException e) {
                response.put("error", "Aucun client trouvé pour le customer avec l'ID " + input.getBouticid());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // Mise à jour de la propriété
            String updateJpql = "UPDATE client c SET c." + input.getProp() + " = :valeur WHERE c.cltid = :cltid";

            Query updateQuery = entityManager.createNativeQuery(updateJpql);

            if ("pass".equals(input.getProp()) && !input.getValeur().isEmpty()) {
                // Cas spécial pour le mot de passe : hachage avant stockage
                String hashedPassword = BCrypt.hashpw(input.getValeur(), BCrypt.gensalt());
                updateQuery.setParameter("valeur", hashedPassword);
            } else {
                // Cas général
                updateQuery.setParameter("valeur", input.getValeur());
            }

            updateQuery.setParameter("cltid", cltid);
            int updatedCount = updateQuery.executeUpdate();

            if (updatedCount > 0) {
                response.put("result", "OK");
            } else {
                response.put("result", "KO");
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", "Une erreur est survenue: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Méthode pour créer une nouvelle boutique et configurer tous ses paramètres associés
     *
     * @param input   Les données requises pour créer une boutique

     * @return ResponseEntity contenant le résultat de l'opération
     */
    @PostMapping("/build-boutic")
    public ResponseEntity<?> buildBoutic(@RequestBody BuildBouticRequest input, HttpServletRequest httpRequest, @RequestHeader("Authorization") String authHeader) {
        // Vérifier si l'email est vérifié

        logger.info("Début de la création d'une nouvelle boutique");

        try {
            String token = authHeader.replace("Bearer ", "");

            Map <java.lang.String, java.lang.Object> payload = JwtService.parseToken(token).getClaims();
            Object verifyEmail = payload.get("verify_email");
            if (verifyEmail == null || verifyEmail.toString().isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error","Courriel non vérifié"));
            }

            // Exécution de la création de boutique dans une transaction
            TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
            transactionTemplate.execute(status -> {
                try {
                    // Étape 1: Création et validation du client
                    Client client = clientService.createAndSaveClient(input, token);
                    logger.debug("Client créé avec succès: ID={}", client.getCltId());

                    // Étape 2: Création de la boutique
                    Customer customer = customerService.createAndSaveCustomer(client, token);
                    logger.debug("Boutique créée avec succès: ID={}, Alias={}",
                            customer.getCustomId(), customer.getCustomer());

                    // Étape 3: Création de l'abonnement
                    Abonnement abonnement = abonnementService.createAndSaveAbonnement(client, customer, token);
                    logger.debug("Abonnement créé avec succès: ID={}", abonnement.getAboId());

                    // Étape 4: Mise à jour des métadonnées Stripe
                    stripeService.updateStripeSubscriptionMetadata(
                            payload.get("creationabonnement_stripe_subscription_id").toString(),
                            abonnement.getAboId());

                    // Étape 5: Configuration des paramètres et statuts par défaut
                    paramService.createDefaultParameters(customer.getCustomId(), token);
                    statutCmdService.createDefaultOrderStatuses(customer.getCustomId());

                    // Étape 6: Mise à jour de la session pour l'authentification
                    bouticService.updateSessionAfterBoutiqueCreation(customer, token);

                    return null;
                } catch (Exception e) {
                    // En cas d'erreur, force le rollback de la transaction
                    status.setRollbackOnly();
                    try {
                        throw e;
                    } catch (StripeException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });

            logger.info("Boutique créée avec succès");
            return ResponseEntity.ok(Map.of("result", "Boutique créée avec succès"));

        } catch (DatabaseException.InvalidSessionDataException e) {
            logger.warn("Données de session invalides: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Données de session invalides: " + e.getMessage()));
        } catch (DatabaseException.EmailAlreadyExistsException e) {
            logger.warn("Email déjà utilisé: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", e.getMessage()));
        } catch (DatabaseException.InvalidAliasException e) {
            logger.warn("Alias de boutique invalide: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (DataAccessException e) {
            logger.error("Erreur de base de données lors de la création de la boutique", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur de base de données: Une erreur est survenue"));
        } catch (Exception e) {
            logger.error("Erreur inattendue lors de la création de la boutique", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Une erreur inattendue est survenue"));
        }
    }

    /**
     * Méthode pour mettre à jour l'adresse email d'une boutique
     */
    @PostMapping("/radress-boutic")
    public ResponseEntity<?> updateBouticEmail(@RequestBody UpdateEmailRequest input, @RequestHeader("Authorization") String authHeader) {
        Map<String, Object> response = new HashMap<>();
        String token = authHeader.replace("Bearer ", "");
        Map <java.lang.String, java.lang.Object> payload = JwtService.parseToken(token).getClaims();

        try {
            // Vérification de la session
            if (payload.get("bo_id") == null || payload.get("bo_email") == null) {
                throw new IllegalStateException("Session invalide ou expirée");
            }

            Integer bouticId = Integer.parseInt(payload.get("bo_id").toString());
            String currentEmail = (String) payload.get("bo_email");

            // Vérification de l'unicité de l'email
            Long emailCount = clientRepository.countByEmail(currentEmail);
            if (emailCount > 1) {
                throw new IllegalStateException("Impossible d'avoir plusieurs fois le même courriel " + currentEmail);
            }

            // Récupération de l'ID client associé à la boutique

            Customer customer = customerRepository.findByCustomid(bouticId)
                    .orElseThrow(() -> new IllegalStateException("Boutique introuvable"));

            // Mise à jour de l'email du client
            clientRepository.updateEmailById(input.getEmail(), customer.getCltid());

            // Mise à jour de la session
            payload.put("bo_email", input.getEmail());
            String jwt = JwtService.generateToken(payload, "" );

            response.put("success", true);
            response.put("message", "Adresse email mise à jour avec succès");
            response.put("token", jwt);
            return ResponseEntity.ok(response);

        } catch (IllegalStateException e) {
            logger.error("Erreur de validation lors de la mise à jour de l'email", e);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour de l'email", e);
            response.put("error", "Une erreur est survenue: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
