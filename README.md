# GestionLoyer

Spring Boot, gestion des loyers

## 📋 Prérequis

- Docker Desktop (Windows/Mac) ou Docker Engine + Docker Compose (Linux)
- Git

## 🚀 Démarrage rapide avec Docker

### 1. Cloner le projet

```bash
git clone https://github.com/kewou/GestionLoyer.git
cd GestionLoyer
```

### 2. Lancer l'environnement complet (Backend + PostgreSQL)

```bash
docker-compose up --build
```

Cette commande va :

- Créer et démarrer un container PostgreSQL sur le port `5433`
- Builder et démarrer le backend Spring Boot sur le port `8090`
- Créer automatiquement la base de données `gestionloyer_db`

### 3. Accéder à l'application

- **API Backend** : http://localhost:8090/beezyApi
- **Swagger UI** : http://localhost:8090/beezyApi/swagger-ui.html
- **API Docs** : http://localhost:8090/beezyApi/api-docs

### 4. Arrêter l'environnement

```bash
docker-compose down
```

Pour supprimer également les volumes (données de la base) :

```bash
docker-compose down -v
```

## 🛠️ Commandes utiles

### Voir les logs

```bash
# Tous les services
docker-compose logs -f

# Seulement le backend
docker-compose logs -f backend

# Seulement PostgreSQL
docker-compose logs -f postgres
```

### Redémarrer un service

```bash
docker-compose restart backend
docker-compose restart postgres
```

### Reconstruire uniquement le backend

```bash
docker-compose up --build backend
```

### Accéder au container PostgreSQL

```bash
docker exec -it gestionloyer-postgres psql -U postgres -d gestionloyer_db
```

## 📝 Configuration

### Profils Spring disponibles

- **dev** : Développement local (connexion à PostgreSQL local sur port 5433)
- **docker** : Environnement Docker (connexion au container PostgreSQL)
- **test** : Tests unitaires

### Variables d'environnement

Le fichier `docker-compose.yml` contient les configurations par défaut :

- `POSTGRES_DB=gestionloyer_db`
- `POSTGRES_USER=postgres`
- `POSTGRES_PASSWORD=root`

**Pour personnaliser la configuration :**

1. Copiez le fichier `.env.example` en `.env`
2. Modifiez les valeurs selon vos besoins
3. Le fichier `.env` sera automatiquement utilisé par docker-compose

**Important :** N'oubliez pas de configurer les credentials mail dans `application-docker.properties` pour activer les
fonctionnalités d'email (inscription, reset password).

### Persistance des données

Les données de la base PostgreSQL sont **automatiquement persistantes** grâce au volume Docker nommé `postgres_data`.

**Important :**

- ✅ `docker-compose down` : Arrête les containers **MAIS conserve les données**
- ❌ `docker-compose down -v` : Arrête les containers **ET supprime toutes les données**

Pour vérifier les volumes Docker :

```bash
docker volume ls
```

Pour sauvegarder manuellement les données :

```bash
docker exec gestionloyer-postgres pg_dump -U postgres gestionloyer_db > backup.sql
```

Pour restaurer une sauvegarde :

```bash
docker exec -i gestionloyer-postgres psql -U postgres gestionloyer_db < backup.sql
```

## 🔧 Développement local (sans Docker)

### Prérequis

- Java 11
- Maven 3.6+
- PostgreSQL 13+

### Configuration

1. Démarrer PostgreSQL localement sur le port 5433
2. Créer la base de données :

```sql
CREATE DATABASE gestionloyer_db;
```

3. Lancer l'application :

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## 📦 Process de déploiement

### Local

```bash
mvn deploy
```

### Production

1. Merger `develop` dans `main`
2. Exécuter `mvn release`
3. Pour le local, récupérer la nouvelle version du snapshot

## 🐛 Troubleshooting

### Le backend ne démarre pas

- Vérifier que PostgreSQL est bien démarré : `docker-compose ps`
- Consulter les logs : `docker-compose logs backend`

### Erreur de connexion à la base de données

- Vérifier que le port 5433 n'est pas déjà utilisé
- Redémarrer les containers : `docker-compose restart`

### Rebuild complet

```bash
docker-compose down -v
docker-compose up --build
```

## 📚 Structure du projet

```
GestionLoyer/
├── docker-compose.yml          # Configuration Docker Compose
├── Dockerfile                  # Image Docker du backend
├── init-db.sql                # Script d'initialisation de la base
├── pom.xml                    # Configuration Maven
├── src/
│   ├── main/
│   │   ├── java/
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-dev.properties
│   │       └── application-docker.properties
│   └── test/
└── README.md
```

## 🤝 Contribution

1. Créer une branche depuis `develop`
2. Faire vos modifications
3. Tester avec Docker : `docker-compose up --build`
4. Créer une Pull Request vers `develop`
