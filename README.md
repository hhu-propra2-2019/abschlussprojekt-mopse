![Gradle Checks](https://github.com/hhu-propra2/abschlussprojekt-mopse/workflows/Gradle%20Checks/badge.svg) ![Generate Documentation](https://github.com/hhu-propra2/abschlussprojekt-mopse/workflows/Generate%20Documentation/badge.svg)

# Materialsammlung

## material1

Eine Verwaltungssoftware für Material für Kurse und Lerngruppen.

## Management Summary

Es ist aufwendig für Lerngruppen oder Kurse Dateien miteinander zu teilen, wenn man die aktuelle Unisoftware oder gar externe Anwendungen benutzen muss.

### Aufgabenstellung

- Bereitstellung eines gruppenbasierten Dateisystems.

### Features

- Ordner- und gruppenbasiertes Dateisystem
- Rollenbasierte Berechtigungen
- Vorschaufunktion für verschiedene Dateiformate

## Lokales Starten der Anwendung

### Development Profile (mit externer PostgreSQL Datenbank und Stub `Gruppenfindung`s-Adapter)

Es existieren IntelliJ Run Configurations um die benötigten Services zu starten.

1. Docker starten und eventuell in IntelliJ einbinden.
1. OPTIONAL: `update_docker_images.bat/.sh` ausführen, um die Docker Images zu aktualisieren.
1. Run Configuration `docker-compose.yml: PostgreSQL & MinIO` starten
oder alternativ `docker-compose -f docker-compose.yml -f docker-compose.dev.yml up material1_db material1_minio` in der
Konsole ausführen. Das MinIO Webinterface ist dann über http://localhost:9000 erreichbar.
Zum Beenden in IntelliJ einfach auf den Knopf drücken, der die Konfiguration `Compose: docker-compose.yml` beendet
oder alternativ in der Konsole
`docker-compose -f docker-compose.yml -f docker-compose.dev.yml down --volumes --remove-orphans` ausführen.
`--volumes` ist notwendig um das erstellte Docker Volume mitzulöschen.
1. Run Configuration `Material1Application - dev` starten
oder alternativ die Spring Anwendung im Spring Profil `dev` starten.
1. Im Webbrowser kann nun zum Beispiel http://localhost:8080/material1 geöffnet werden.

### Production Profile (mit externer PostgreSQL Datenbank und echtem `Gruppenfindung`s-Adapter)

Es existieren IntelliJ Run Configurations um die benötigten Services zu starten.

1. Docker starten und eventuell in IntelliJ einbinden.
1. OPTIONAL: `update_docker_images.bat/.sh` ausführen, um die Docker Images zu aktualisieren.
1. Run Configuration `docker-compose.yml: PostgreSQL & MinIO` starten
oder alternativ `docker-compose -f docker-compose.yml -f docker-compose.dev.yml up material1_db material1_minio` in der
Konsole ausführen. Das MinIO Webinterface ist dann über http://localhost:9000 erreichbar.
Zum Beenden in IntelliJ einfach auf den Knopf drücken, der die Konfiguration `Compose: docker-compose.yml` beendet
oder alternativ in der Konsole
`docker-compose -f docker-compose.yml -f docker-compose.dev.yml down --volumes --remove-orphans` ausführen.
`--volumes` ist notwendig um das erstellte Docker Volume mitzulöschen.
1. Run Configuration `Material1Application - prod` starten
oder alternativ die Spring Anwendung im Spring Profil `prod` mit der Umgebungsvariablen `MATERIAL1_DB_HOST=localhost`
starten.
1. Im Webbrowser kann nun zum Beispiel http://localhost:8080/material1/groups geöffnet werden.

### Als Komplettpaket im Dev Profile (mit externer PostgreSQL Datenbank und Stub `Gruppenfindung`s-Adapter)

1. Docker starten
1. OPTIONAL: `update_docker_images.bat/.sh` ausführen, um die Docker Images zu aktualisieren.
1. `docker-compose -f docker-compose.yml -f docker-compose.dev.yml -f docker-compose.prod.yml -f docker-compose.demo.yml up --build material1_app`
in der Konsole ausführen. Zum Beenden in der Konsole
`docker-compose -f docker-compose.yml -f docker-compose.dev.yml -f docker-compose.prod.yml -f docker-compose.demo.yml down --volumes --remove-orphans`
ausführen. `--volumes` ist notwendig um die erstellten Docker Volumes mitzulöschen.

## Ausloggen

Man kann sich in dev nicht über den gegebenen Logout-Link ausloggen. Man kann aber links unten auf den Namen klicken und dann auf der Keycloak-Seite oben rechts auf `Logout` klicken, dann ist man ausgeloggt.

## Dokumentation

Hier kann die Dokumentation gefunden werden: [Dokumentation](https://hhu-propra2.github.io/abschlussprojekt-mopse/)

Javadoc kann hier aufgerufen werden: [Javadoc](https://hhu-propra2.github.io/abschlussprojekt-mopse/javadoc/)

Die REST API Dokumentation befindet sich hier: [REST API](https://hhu-propra2.github.io/abschlussprojekt-mopse/#section-system-scope-and-context)
