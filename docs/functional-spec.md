# Spécifications fonctionnelles — Application Todolist

## 1. Présentation

Application web de gestion de tâches développée en **Java Spring Boot / Thymeleaf**.  
Deux profils d'accès : visiteur (lecture seule) et administrateur (compte connecté, accès complet).

---

## 2. Acteurs

| Acteur | Description |
|---|---|
| **Visiteur** | Utilisateur non authentifié. Consultation uniquement. |
| **Administrateur** | Utilisateur connecté. CRUD complet sur les tâches. |

---

## 3. Fonctionnalités

### 3.1 Accessibles à tous (Visiteur + Admin)

| ID | Fonctionnalité | Description |
|---|---|---|
| F01 | Consulter les tâches | Affichage de la liste des tâches avec titre, statut et date d'échéance. |
| F02 | Voir le détail d'une tâche | Affichage complet d'une tâche (titre, description, statut, dates). |

### 3.2 Réservées à l'Administrateur

| ID | Fonctionnalité | Description |
|---|---|---|
| F03 | Se connecter | Authentification par username / mot de passe. |
| F04 | Se déconnecter | Invalidation de la session. |
| F05 | Créer une tâche | Formulaire : titre (obligatoire), description, date d'échéance. Statut initial : `TODO`. |
| F06 | Modifier une tâche | Modification du titre, description, statut et date d'échéance. |
| F07 | Supprimer une tâche | Suppression définitive après confirmation. |
| F08 | Changer le statut | Passage entre `TODO`, `IN_PROGRESS` et `DONE`. |

---

## 4. Règles métier

- **RM01** — Le titre d'une tâche est obligatoire (non vide, max 100 caractères).
- **RM02** — La date d'échéance ne peut pas être dans le passé à la création.
- **RM03** — Une tâche `DONE` ne peut pas être modifiée.
- **RM04** — Seul l'administrateur propriétaire d'une tâche peut la modifier ou la supprimer.
- **RM05** — Un visiteur non connecté est redirigé vers `/login` s'il tente d'accéder à une action protégée.

---

## 5. Statuts d'une tâche

```
TODO  ──►  IN_PROGRESS  ──►  DONE
```

---

## 6. Stack technique

| Couche | Technologie |
|---|---|
| Backend | Java 17, Spring Boot |
| Persistance | Spring Data JPA, Hibernate |
| Frontend | Thymeleaf, HTML/CSS |
| Base de données | MySQL |

---

## 7. Endpoints principaux

| Méthode | URL | Accès | Description |
|---|---|---|---|
| GET | `/tasks` | Tous | Liste des tâches |
| GET | `/tasks/{id}` | Tous | Détail d'une tâche |
| GET | `/tasks/new` | Admin | Formulaire de création |
| POST | `/tasks` | Admin | Créer une tâche |
| GET | `/tasks/{id}/edit` | Admin | Formulaire de modification |
| PUT | `/tasks/{id}` | Admin | Modifier une tâche |
| DELETE | `/tasks/{id}` | Admin | Supprimer une tâche |
| POST | `/login` | Tous | Authentification |
| POST | `/logout` | Admin | Déconnexion |