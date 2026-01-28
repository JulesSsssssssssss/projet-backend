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

---

## Structure de la base de données

### Table `users`

| Colonne    | Type      | Contraintes                              |
| ---------- | --------- | ---------------------------------------- |
| id         | BIGSERIAL | PRIMARY KEY                              |
| username   | VARCHAR   | NOT NULL, UNIQUE                         |
| password   | VARCHAR   | NOT NULL                                 |
| role       | VARCHAR   | NOT NULL                                 |
| enabled    | BOOLEAN   | NOT NULL, DEFAULT true                   |
| last_gacha | TIMESTAMP | NULLABLE – dernière utilisation du gacha |

---

### Table `rarity`

| Colonne | Type      | Contraintes      |
| ------- | --------- | ---------------- |
| id      | BIGSERIAL | PRIMARY KEY      |
| name    | VARCHAR   | NOT NULL, UNIQUE |

**Valeurs initiales recommandées :**

* COMMON
* RARE
* EPIC
* LEGENDARY

---

### Table `cards`

| Colonne   | Type      | Contraintes     |
| --------- | --------- | --------------- |
| id        | BIGSERIAL | PRIMARY KEY     |
| name      | VARCHAR   | NOT NULL        |
| rarity_id | BIGINT    | FK → rarity(id) |
| attack    | INTEGER   | NOT NULL        |
| defense   | INTEGER   | NOT NULL        |
| image_url | VARCHAR   | NULLABLE        |

---

### Table `user_cards` (pivot)

| Colonne     | Type      | Contraintes                    |
| ----------- | --------- | ------------------------------ |
| user_id     | BIGINT    | FK → users(id), PK (composite) |
| card_id     | BIGINT    | FK → cards(id), PK (composite) |
| obtained_at | TIMESTAMP | NOT NULL                       |

Cette table représente les cartes possédées par les utilisateurs.

---

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

    private boolean enabled = true;

    @Column(name = "last_gacha")
    private LocalDateTime lastGacha;

    @ManyToMany
    @JoinTable(
        name = "user_cards",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "card_id")
    )
    private Set<Card> cards = new HashSet<>();
}
```

---

### Card.java

```java
@Entity
@Table(name = "cards")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private int attack;
    private int defense;

    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "rarity_id")
    private Rarity rarity;
}
```

---

### Rarity.java

```java
@Entity
@Table(name = "rarity")
public class Rarity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;
}
```

---

## Gacha Logic (Concept)

* Lors de l'appel à `GET /api/cards/gacha` :

  * Une carte aléatoire est tirée (pondérée par rareté plus tard)
  * La carte est ajoutée à `user_cards`
  * Le champ `users.last_gacha` est mis à jour avec `LocalDateTime.now()`

Ce champ permet :

* cooldown journalier
* pity system
* statistiques utilisateur

---

## Repository (exemples)

```java
public interface CardRepository extends JpaRepository<Card, Long> {}
public interface RarityRepository extends JpaRepository<Rarity, Long> {
    Optional<Rarity> findByName(String name);
}
```

---

## Notes importantes

* `ddl-auto=update` créera automatiquement les nouvelles tables
* Le pivot `user_cards` est géré par JPA
* `last_gacha` est nullable pour les anciens utilisateurs

---

## Commandes SQL utiles

```sql
\dt
\d users
\d cards
\d rarity
\d user_cards
```

---
