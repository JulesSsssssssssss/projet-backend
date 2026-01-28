# Projet Backend Spring Boot

Application Spring Boot avec toutes les fonctionnalités principales.

## 🚀 Fonctionnalités installées

- **Spring Boot Web** - Création d'API REST
- **Spring Boot Data JPA** - Gestion de la persistance
- **Spring Boot Security** - Sécurité de l'application
- **Spring Boot Validation** - Validation des données
- **Spring Boot Actuator** - Monitoring et métriques
- **H2 Database** - Base de données en mémoire
- **PostgreSQL** - Driver pour PostgreSQL
- **Lombok** - Réduction du code boilerplate
- **DevTools** - Rechargement automatique en développement

## 📋 Prérequis

- Java 17 ou supérieur
- Maven 3.6+

## 🔧 Installation

```bash
# Cloner le projet
git clone https://github.com/JulesSsssssssssss/projet-backend.git
cd projet-backend

# Compiler le projet
mvn clean install
```

## ▶️ Lancement

```bash
# Lancer l'application
mvn spring-boot:run
```

L'application démarre sur le port **8080**.

## 🧪 Tester les endpoints

```bash
# Endpoint Hello World
curl http://localhost:8080/api/hello

# Endpoint Status
curl http://localhost:8080/api/status

# Console H2 Database
http://localhost:8080/h2-console

# Actuator Health
curl http://localhost:8080/actuator/health
```

## 📁 Structure du projet

```
projet-backend/
├── src/
│   ├── main/
│   │   ├── java/com/example/backend/
│   │   │   ├── BackendApplication.java
│   │   │   ├── controller/
│   │   │   │   └── HelloController.java
│   │   │   └── config/
│   │   │       └── SecurityConfig.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/
├── pom.xml
└── README.md
```

## 🔐 Configuration de sécurité

La sécurité Spring est configurée pour autoriser tous les endpoints en mode développement. 
**Important:** Mettez à jour la configuration de sécurité avant de déployer en production.

## 💾 Base de données

L'application utilise H2 en mémoire par défaut. Configuration :
- URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: *(vide)*
- Console H2: http://localhost:8080/h2-console

## 📊 Monitoring

Actuator expose les endpoints suivants :
- `/actuator/health` - Santé de l'application
- `/actuator/info` - Informations sur l'application
- `/actuator/metrics` - Métriques

## 🛠️ Configuration

Modifiez `src/main/resources/application.properties` pour personnaliser :
- Port du serveur
- Configuration de la base de données
- Niveaux de logs
- Configuration JPA
