[![Gradle Checks](https://github.com/hhu-propra2-2019/abschlussprojekt-mopse/actions/workflows/gradle.yml/badge.svg)](https://github.com/hhu-propra2-2019/abschlussprojekt-mopse/actions/workflows/gradle.yml) [![Generate Documentation](https://github.com/hhu-propra2-2019/abschlussprojekt-mopse/actions/workflows/documentation.yml/badge.svg)](https://github.com/hhu-propra2-2019/abschlussprojekt-mopse/actions/workflows/documentation.yml)

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

### Development Profile (mit externer PostgreSQL Datenbank und Stub `Gruppenbildung`s-Adapter)

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
oder alternativ die Spring Anwendung mit Gradle im Spring Profil `dev` starten.
1. Im Webbrowser kann nun zum Beispiel http://localhost:8080/material1 geöffnet werden.

### Production Profile (mit externer PostgreSQL Datenbank und echtem `Gruppenbildung`s-Adapter)

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
oder alternativ die Spring Anwendung mit Gradle im Spring Profil `prod` starten.
1. Im Webbrowser kann nun zum Beispiel http://localhost:8080/material1 geöffnet werden.

### Als Komplettpaket im Dev Profile [Demo] (mit externer PostgreSQL Datenbank und Stub `Gruppenbildung`s-Adapter)

1. Docker starten
1. OPTIONAL: `update_docker_images.bat/.sh` ausführen, um die Docker Images zu aktualisieren.
1. `docker-compose -f docker-compose.yml -f docker-compose.dev.yml -f docker-compose.prod.yml -f docker-compose.demo.yml up --build material1_app`
in der Konsole ausführen. Zum Beenden in der Konsole
`docker-compose -f docker-compose.yml -f docker-compose.dev.yml -f docker-compose.prod.yml -f docker-compose.demo.yml down --volumes --remove-orphans`
ausführen. `--volumes` ist notwendig um die erstellten Docker Volumes mitzulöschen.

### In Production (mit externer PostgreSQL Datenbank, externem MinIO und echtem `Gruppenbildung`s-Adapter)

1. Docker starten
1. OPTIONAL: `update_docker_images.bat/.sh` ausführen, um die Docker Images zu aktualisieren.
1. Die Umgebungsvariablen in der Datei `prod.env` anpassen.
1. `docker-compose -f docker-compose.yml -f docker-compose.prod.yml up --build material1_app`
in der Konsole ausführen. Zum Beenden in der Konsole
`docker-compose -f docker-compose.yml -f docker-compose.prod.yml down`
ausführen. Eventuell müssen weitere Vorsichtsmaßnahmen getroffen werden, damit `docker-compose` nicht das Volume löscht,
in dem PostgreSQL seine Daten speichert.

## Dokumentation

Hier kann die Dokumentation gefunden werden: [Dokumentation](https://hhu-propra2-2019.github.io/abschlussprojekt-mopse/)

Javadoc kann hier aufgerufen werden: [Javadoc](https://hhu-propra2-2019.github.io/abschlussprojekt-mopse/javadoc/)

Die REST API Dokumentation befindet sich hier: [REST API](https://hhu-propra2-2019.github.io/abschlussprojekt-mopse/#section-system-scope-and-context)
