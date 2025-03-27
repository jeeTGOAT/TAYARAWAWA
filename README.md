# TAYARAWAWA - Système de Gestion Aérienne

TAYARAWAWA est une application Java complète de gestion aérienne offrant un environnement intégré pour la simulation de vol, le contrôle du trafic aérien, la gestion des passagers et des réservations, ainsi que des analyses statistiques en temps réel.

## Fonctionnalités

### Menu Principal
- Interface intuitive avec animations et effets visuels
- Contrôles de musique de fond
- Barre d'état en temps réel

### Simulation de Vol
- Carte du monde interactive avec vols animés
- Affichage des trajectoires, altitudes et vitesses
- Contrôles de zoom et de vitesse de simulation
- Fonction d'ajout de nouveaux vols

### Contrôle du Trafic Aérien
- Radar avec animation de balayage réaliste
- Détection automatique des conflits aériens
- Liste des vols actifs avec informations détaillées
- Options pour contacter et dérouter les aéronefs

### Gestion du Trafic Aérien
- Tableau de bord complet des vols
- Statuts visuels (à l'heure, en vol, retardé, annulé)
- Filtres et recherche avancés
- Édition et gestion des informations de vol
- Visualisation de progression des vols

### Gestion des Passagers
- Base de données de passagers avec informations détaillées
- Historique des vols par passager
- Préférences des passagers
- Fonctions de recherche et filtrage

### Gestion des Réservations
- Système complet de réservation de billets
- Plan de siège interactif
- Statistiques visuelles sur les réservations
- Confirmation et annulation de réservations

### Statistiques en Direct
- Graphiques et visualisations en temps réel
- Taux de remplissage des vols
- Revenus par classe
- Performance des itinéraires
- Taux de rafraîchissement configurable

## Configuration Requise

- Java SE Development Kit (JDK) 8 ou version ultérieure
- Système d'exploitation : Windows, macOS, Linux

## Installation

1. Clonez ce dépôt sur votre machine locale:
   ```bash
   git clone https://github.com/votre-nom/TAYARAWAWA.git
   ```

2. Naviguez dans le répertoire du projet:
   ```bash
   cd TAYARAWAWA
   ```

3. Compilez le projet:
   ```bash
   javac Main.java
   ```

4. Exécutez l'application:
   ```bash
   java Main
   ```

## Structure du Projet

- `Main.java` - Point d'entrée de l'application
- `MainMenu.java` - Interface du menu principal et intégration des modules
- Modules spécifiques:
  - `AirTrafficControl.java` - Contrôle du trafic aérien
  - `AirTrafficManagement.java` - Gestion des vols
  - `FlightSimulation.java` - Simulation de vol
  - `LiveStatistics.java` - Statistiques en temps réel
- Modèles de données:
  - `Flight.java` - Informations sur les vols
  - `Passenger.java` - Informations sur les passagers
  - `Booking.java` - Réservations

## Captures d'écran

*À venir...*

## Développement

Le projet utilise une architecture modulaire où chaque fonctionnalité principale est implémentée dans son propre module. L'interface graphique est construite avec Java Swing et se caractérise par un design moderne et une navigation intuitive.

## Licence

Tous droits réservés. Ce logiciel est la propriété exclusive de ses auteurs.

## Auteurs

- Développé pour le projet académique de gestion aérienne

---

© 2023 TAYARAWAWA - Système de Gestion Aérienne # Java-Project-N7-initial-Part-March-2025
