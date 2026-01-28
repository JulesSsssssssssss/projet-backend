# 🔐 Documentation - Authentification JWT

## Comment ça fonctionne ?

### 1️⃣ Architecture JWT

```
Client                          Serveur
  |                                |
  |------- POST /api/auth/login --|
  |     (username + password)      |
  |                                |
  |                                | ✅ Vérifie les identifiants
  |                                | 🔑 Génère un token JWT
  |                                |
  |<------ Token JWT --------------|
  |                                |
  | Stocke le token                |
  |                                |
  |------- GET /api/protected -----|
  |  Header: Authorization:        |
  |  Bearer {token}                |
  |                                | ✅ Valide le token
  |<------ Données protégées ------|
```

### 2️⃣ Composants créés

- **LoginRequest.java** - DTO pour recevoir les identifiants
- **LoginResponse.java** - DTO pour renvoyer le token
- **JwtService.java** - Service pour générer et valider les tokens
- **AuthController.java** - Contrôleur avec la route `/api/auth/login`
- **JwtAuthenticationFilter.java** - Filtre pour valider les tokens sur chaque requête
- **SecurityConfig.java** - Configuration Spring Security avec JWT

## 🧪 Comment tester

### Étape 1 : Lancer l'application
```bash
mvn spring-boot:run
```

### Étape 2 : Se connecter et obtenir un token

**Requête :**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "password123"
  }'
```

**Réponse :**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTcwNjQ...",
  "type": "Bearer",
  "username": "admin",
  "message": "Connexion réussie"
}
```

### Étape 3 : Utiliser le token pour accéder aux routes protégées

```bash
curl -X GET http://localhost:8080/api/hello \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTcwNjQ..."
```

## 👥 Utilisateurs de test

Deux utilisateurs sont créés en mémoire pour tester :

| Username | Password    | Rôles       |
|----------|-------------|-------------|
| admin    | password123 | USER, ADMIN |
| user     | user123     | USER        |

## 📝 Routes disponibles

### Routes publiques (pas de token requis)
- `POST /api/auth/login` - Connexion
- `GET /api/hello` - Test simple
- `GET /api/status` - Statut de l'application
- `GET /h2-console` - Console H2
- `GET /actuator/**` - Endpoints Actuator

### Routes protégées (token requis)
Toutes les autres routes nécessitent un token JWT valide dans le header `Authorization: Bearer {token}`

## 🔧 Configuration

Dans `application.properties` :

```properties
# Clé secrète pour signer les tokens (à changer en production !)
jwt.secret=monSuperSecretKeyPourJWTQuiDoitEtreTresLonguePourEtreSecurisee123456789

# Durée de validité du token en millisecondes (24 heures)
jwt.expiration=86400000
```

## 🚀 Exemple avec Postman

### 1. Login
- **Méthode**: POST
- **URL**: `http://localhost:8080/api/auth/login`
- **Headers**: `Content-Type: application/json`
- **Body** (raw JSON):
```json
{
  "username": "admin",
  "password": "password123"
}
```

### 2. Copier le token de la réponse

### 3. Utiliser le token
- **Méthode**: GET
- **URL**: `http://localhost:8080/api/hello`
- **Headers**: `Authorization: Bearer {votre_token_ici}`

## 🔒 Sécurité

⚠️ **Important pour la production** :
1. Changez la clé secrète JWT dans `application.properties`
2. Utilisez une vraie base de données pour stocker les utilisateurs
3. Hachez toujours les mots de passe avec BCrypt
4. Utilisez HTTPS en production
5. Ajoutez un système de refresh token
6. Implémentez la révocation des tokens

## ❓ Questions fréquentes

**Q: Combien de temps le token est-il valide ?**
R: 24 heures par défaut (configurable dans `application.properties`)

**Q: Que faire si le token expire ?**
R: Il faut se reconnecter pour obtenir un nouveau token

**Q: Comment ajouter des utilisateurs ?**
R: Pour le moment, les utilisateurs sont en mémoire. En production, créez une entité User avec JPA et stockez-la en base de données.
