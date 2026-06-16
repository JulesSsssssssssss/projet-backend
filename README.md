# Projet Backend — Jeu de Cartes

Application Spring Boot 3.4 · Java 21 · PostgreSQL · JWT

Groupe de travail :
Jule Ruberti
Louka Lemonnier
Lukas Gaboriau

Un backend de jeu de cartes avec système gacha, inventaire et échanges entre joueurs, accompagné d'un mini front-end vanilla JS servi directement par Spring Boot.

---

## Prérequis

| Outil | Version minimale |
|-------|-----------------|
| Java (JDK) | 21+ (Java 25 supporté) |
| Maven | 3.6+ |
| PostgreSQL | 14+ |

---

## Installation via docker

**Linux (Ubuntu/Debian)**
```bash
docker compose up -d --build
```
L'application démarre sur **http://localhost:8080**

## Installation directe

### 1. Java

**macOS**
```bash
brew install openjdk@21   # ou une version supérieure
```

**Linux (Ubuntu/Debian)**
```bash
sudo apt update && sudo apt install openjdk-21-jdk
```

**Windows**
Télécharger sur [Adoptium](https://adoptium.net/) — choisir **Temurin 21 LTS** ou supérieur.

### 2. Maven

**macOS**
```bash
brew install maven
```

**Linux**
```bash
sudo apt install maven
```

**Windows** — télécharger sur [maven.apache.org](https://maven.apache.org/download.cgi) et ajouter `bin/` au PATH.

### 3. PostgreSQL

**macOS**
```bash
brew install postgresql@15
brew services start postgresql@15
```

**Linux**
```bash
sudo apt install postgresql
sudo systemctl start postgresql
```

Créer la base de données :
```bash
psql -U postgres
```
```sql
CREATE DATABASE projet_backend;
CREATE USER julesruberti WITH PASSWORD '';
GRANT ALL PRIVILEGES ON DATABASE projet_backend TO julesruberti;
\q
```

> Le nom d'utilisateur et le mot de passe sont configurables dans `src/main/resources/application.properties`.

---

## Lancer le projet

```bash
# 1. Cloner
git clone https://github.com/JulesSsssssssssss/projet-backend.git
cd projet-backend

# 2. Compiler
mvn clean package -DskipTests

# 3. Démarrer
java -jar target/projet-backend-1.0.0.jar
```

L'application démarre sur **http://localhost:8080**

Le front-end est directement accessible à cette adresse — aucun serveur séparé n'est nécessaire.

---

## Front-end

Accéder à **http://localhost:8080** dans un navigateur.

| Vue | Description |
|-----|-------------|
| Connexion / Inscription | Authentification JWT |
| Dashboard | Points du joueur + bouton Pull (100 pts / tirage) |
| Inventaire | Grille de toutes les cartes possédées |
| Échanges | Proposer et accepter des échanges entre joueurs |

---

## API

### Authentification — `/api/auth`

```
POST  /api/auth/register   Créer un compte
POST  /api/auth/login      Connexion → retourne un token JWT
POST  /api/auth/logout     Déconnexion (stateless)
```

### Cartes — `/api/cards`

```
GET   /api/cards            Liste toutes les cartes disponibles  (public)
GET   /api/cards/inventory  Inventaire de l'utilisateur connecté (auth requise)
```

### Gacha — `/api/gacha`

```
POST  /api/gacha            Tirer une carte (coûte 100 pts)      (auth requise)
GET   /api/gacha/points     Points actuels du joueur             (auth requise)
```

### Échanges — `/api/trades`

```
POST  /api/trades/propose          Proposer un échange           (auth requise)
GET   /api/trades/pending          Échanges en attente           (auth requise)
POST  /api/trades/accept/{tradeId} Accepter un échange           (auth requise)
```

Les routes protégées nécessitent le header :
```
Authorization: Bearer <token>
```

---

## Structure du projet

```
projet-backend/
├── src/main/
│   ├── java/com/example/backend/
│   │   ├── config/
│   │   │   ├── SecurityConfig.java          Configuration Spring Security + JWT
│   │   │   ├── JwtAuthenticationFilter.java Filtre de validation du token
│   │   │   └── DatabaseInitializer.java     Données initiales (raretés, cartes)
│   │   ├── controller/
│   │   │   ├── AuthController.java
│   │   │   ├── CardController.java
│   │   │   ├── GachaController.java
│   │   │   └── TradeController.java
│   │   ├── service/
│   │   │   ├── GachaService.java            Logique de tirage pondéré
│   │   │   ├── TradeService.java
│   │   │   ├── JwtService.java
│   │   │   └── UserDetailsServiceImpl.java
│   │   ├── entity/   User
│   │   ├── model/    Card, Rarity, TradeRequest, Player
│   │   ├── dto/      LoginRequest/Response, RegisterRequest, CardResponse, GachaResponse
│   │   └── repository/
│   └── resources/
│       ├── application.properties
│       └── static/                          Front-end servi par Spring Boot
│           ├── index.html
│           ├── style.css
│           └── app.js
└── pom.xml
```

---

## Configuration

Fichier : `src/main/resources/application.properties`

```properties
server.port=8080

spring.datasource.url=jdbc:postgresql://localhost:5432/projet_backend
spring.datasource.username=<utilisateur>
spring.datasource.password=<mot_de_passe>

jwt.secret=<clé_secrète_longue>
jwt.expiration=86400000   # 24h en ms
```

> En production, ne jamais stocker la clé JWT dans ce fichier.
> Utiliser une variable d'environnement : `JWT_SECRET=...`

---

## Documentation

La documentation technique est disponible sur GitHub Pages :
**https://JulesSsssssssssss.github.io/projet-backend/**

Pour la prévisualiser localement :
```bash
pip install mkdocs mkdocs-material
mkdocs serve
# → http://localhost:8000
```
