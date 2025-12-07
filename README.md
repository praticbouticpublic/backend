# PraticBoutic - Backend API

**PraticBoutic** est une solution de commerce électronique dédiée à la gestion de boutiques en ligne. Ce dépôt contient l'API backend développée en **Java (Spring Boot)**, connectée à une base de données **MySQL**, avec une intégration de paiements via **Stripe**.

## 🚀 Stack technique

- **Langage** : Java 17+
- **Framework** : Spring Boot
- **Base de données** : MySQL
- **Authentification** : Login/Password + Google Sign-In
- **Paiement** : Intégration Stripe API
- **CI** : GitHub Actions (avec déploiement manuel par FTP pour l’instant)
- **Hébergement** : Plesk

## 📁 Structure des environnements

Le projet utilise plusieurs fichiers `application.properties` :
- `application-local.properties`
- `application-dev.properties`
- `application-prod.properties`

La sélection du profil actif se fait via la propriété :

```properties
spring.profiles.active=local
📦 Lancer l'application en local
Prérequis
Java 17+

Maven

MySQL (avec une base configurée dans application-local.properties)

Commande
bash
Copier
Modifier
mvn spring-boot:run -Dspring-boot.run.profiles=local
🔐 Authentification
L'API propose deux méthodes d'authentification :

Par identifiants (login/mot de passe)

Par Google Sign-In (OAuth 2.0)

💳 Paiement
Intégration avec Stripe API pour gérer les paiements sécurisés.

📬 API
L’API est de type RESTful.
La documentation n’est pas encore publiée, mais les appels de test sont enregistrés dans Postman.

🚀 Déploiement
L’intégration continue se fait via GitHub Actions.

⚠️ Le déploiement est actuellement manuel via FTP, en attente d’automatisation par SSH (appleboy/scp-action).

✅ Tests
Les tests sont réalisés manuellement pour le moment.
Une couverture automatisée est prévue.

📄 Licence
Ce projet est actuellement privé. Pour plus d’informations, veuillez contacter l’auteur.

🙋‍♂️ Auteur
Développé par Frédéric Legrand.
Contact : flegrnd.info@gmail.com
