# Configuration PostgreSQL

## Installation

PostgreSQL a été installé via Homebrew :

```bash
brew install postgresql@15
brew services start postgresql@15
```

## Configuration de la base de données

### 1. Base de données créée
```bash
createdb projet_backend
```

### 2. Configuration dans application.properties

```properties
# Base de données PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/projet_backend
spring.datasource.driverClassName=org.postgresql.Driver
spring.datasource.username=julesruberti
spring.datasource.password=

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.open-in-view=false
```

## Structure de la base de données

### Table `users`

| Colonne  | Type         | Contraintes              |
|----------|--------------|--------------------------|
| id       | BIGSERIAL    | PRIMARY KEY              |
| username | VARCHAR(255) | NOT NULL, UNIQUE         |
| password | VARCHAR(255) | NOT NULL                 |
| role     | VARCHAR(255) | NOT NULL                 |
| enabled  | BOOLEAN      | NOT NULL, DEFAULT true   |

## Entités JPA

### User.java
```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false)
    private String role;
    
    private boolean enabled;
}
```

## Repository

### UserRepository.java
```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}
```

## Initialisation des données

Au démarrage de l'application, deux utilisateurs de test sont automatiquement créés :

| Username | Password    | Role       |
|----------|-------------|------------|
| admin    | password123 | ROLE_ADMIN |
| user     | user123     | ROLE_USER  |

Ces utilisateurs sont créés par `DatabaseInitializer.java` uniquement s'ils n'existent pas déjà.

## Authentification avec la base de données

Le système d'authentification JWT utilise maintenant PostgreSQL pour stocker et récupérer les utilisateurs :

1. `UserDetailsServiceImpl` charge les utilisateurs depuis la base de données
2. Les mots de passe sont encodés avec BCrypt avant d'être stockés
3. L'authentification vérifie les credentials contre la base de données
4. Un token JWT est généré pour les utilisateurs authentifiés

## Commandes utiles PostgreSQL

### Démarrer/Arrêter le service
```bash
brew services start postgresql@15
brew services stop postgresql@15
brew services restart postgresql@15
```

### Se connecter à la base de données
```bash
/opt/homebrew/opt/postgresql@15/bin/psql -d projet_backend
```

### Commandes SQL utiles
```sql
-- Lister toutes les tables
\dt

-- Voir la structure de la table users
\d users

-- Lister tous les utilisateurs
SELECT * FROM users;

-- Supprimer tous les utilisateurs (attention !)
TRUNCATE TABLE users;
```

## Migration depuis H2

L'application utilisait précédemment H2 (base de données en mémoire). PostgreSQL offre :

- ✅ **Persistance des données** : Les données survivent au redémarrage
- ✅ **Production-ready** : PostgreSQL est adapté pour la production
- ✅ **Performances** : Meilleure gestion des transactions
- ✅ **Scalabilité** : Support de volumes de données importants

## Troubleshooting

### Port déjà utilisé
Si le port 5432 est déjà utilisé :
```bash
lsof -ti:5432 | xargs kill -9
brew services restart postgresql@15
```

### Réinitialiser la base de données
```bash
/opt/homebrew/opt/postgresql@15/bin/dropdb projet_backend
/opt/homebrew/opt/postgresql@15/bin/createdb projet_backend
```

### Vérifier la connexion
```bash
/opt/homebrew/opt/postgresql@15/bin/psql -d projet_backend -c "SELECT version();"
```
