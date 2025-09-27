package com.ecommerce.praticboutic_backend_java.models;

import jakarta.persistence.*;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import org.hibernate.SessionFactory;

import java.lang.Class;
import java.util.Set;


@MappedSuperclass
//@Access(AccessType.FIELD)
public abstract class BaseEntity {

    public static String capitalize(String word) {
        if (word == null || word.isEmpty()) {
            return word; // Gérer les cas où le mot est null ou vide
        }
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }

    public static String getPrimaryKeyName(SessionFactory sessionFactory, EntityManager entityManager, String tableName) throws ClassNotFoundException {
        // Récupérer le type d'entité à partir du EntityManager et du nom de la table
        EntityType<?> entityType = entityManager.getMetamodel().entity(getEntityClassFromTableName(sessionFactory, tableName));

        // Parcourir les attributs de l'entité
        for (Attribute<?, ?> attribute : entityType.getAttributes()) {
            if (attribute instanceof SingularAttribute<?, ?> singularAttribute) {
                // Identifier la clé primaire
                if (singularAttribute.isId()) {
                    return singularAttribute.getName(); // Retourner le nom de la clé primaire
                }
            }
        }
        // Retourner null si aucune clé primaire n'a été trouvée
        return null;
    }

    public static Class<?> loadEntityClass(String table) throws ClassNotFoundException {
        // Construction dynamique de la classe d'entité
        String entityName = "com.ecommerce.praticboutic_backend_java.entities." + capitalize(table); // Chemin du package des entités
        //Class<?> maclasse = Class.forName(entityName);
        try {
            return Class.forName(entityName);
        } catch (ClassNotFoundException ex) {
            throw new ClassNotFoundException("L'entité spécifiée n'existe pas : " + capitalize(table));
        }
    }

    public static Class<?> getEntityClassFromTableName(SessionFactory sessionFactory, String tableName) throws ClassNotFoundException {
        // Parcourez toutes les entités gérées par Hibernate
        Set<EntityType<?>> entities = sessionFactory.getMetamodel().getEntities();
        for (EntityType<?> entity : entities) {
            // Vérifiez si le nom de la table correspond
            if (entity.getName().equalsIgnoreCase(tableName)) {
                // Retourne la classe de l'entité
                try {
                    return Class.forName("com.ecommerce.praticboutic_backend_java.entities." + entity.getName());
                } catch (ClassNotFoundException ex) {
                    throw new ClassNotFoundException("L'entité spécifiée n'existe pas : " + capitalize(tableName));
                }
            }
        }
        throw new IllegalArgumentException("Aucune entité trouvée pour la table : " + tableName);
    }

}