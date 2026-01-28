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

## ⚙️ Installation des outils nécessaires

### 1. Installer Java (JDK)

**Sur macOS (avec Homebrew) :**
```bash
# Installer Homebrew si ce n'est pas déjà fait
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# Installer Java
brew install openjdk@17

# Lier Java pour le rendre accessible
sudo ln -sfn /opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-17.jdk
```

**Sur Windows :**
1. Télécharger le JDK depuis [Oracle](https://www.oracle.com/java/technologies/downloads/) ou [Adoptium](https://adoptium.net/)
2. Exécuter l'installateur
3. Ajouter JAVA_HOME aux variables d'environnement

**Sur Linux (Ubuntu/Debian) :**
```bash
sudo apt update
sudo apt install openjdk-17-jdk
```

**Vérifier l'installation :**
```bash
java -version
```

### 2. Installer Maven

**Sur macOS (avec Homebrew) :**
```bash
brew install maven
```

**Sur Windows :**
1. Télécharger Maven depuis [apache.org](https://maven.apache.org/download.cgi)
2. Extraire l'archive dans `C:\Program Files\Maven`
3. Ajouter `C:\Program Files\Maven\bin` à la variable PATH

**Sur Linux (Ubuntu/Debian) :**
```bash
sudo apt update
sudo apt install maven
```

**Vérifier l'installation :**
```bash
mvn -version
```

### 3. Installer Git (si nécessaire)

**Sur macOS :**
```bash
brew install git
```

**Sur Windows :**
Télécharger depuis [git-scm.com](https://git-scm.com/download/win)

**Sur Linux :**
```bash
sudo apt install git
```

## 🔧 Installation du projet

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
