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
1. `update_docker_images.bat/.sh` ausführen, um die Docker Images zu aktualisieren.
1. Eine Datei namens `prod.env` im Wurzelverzeichnis des Projekts nach der Vorlage in der Datei `template.env`
erstellen (also `template.env` im selben Verzeichnis duplizieren und die Kopie in `prod.env` umbenennen),
Änderungen sind für das lokale Starten nicht notwendig. Diese Datei wird von `docker-compose` gelesen und ist notwendig
für das Starten des PostgreSQL Containers.
1. Eine Datei namens `dev.env` im Wurzelverzeichnis des Projekts nach der Vorlage in der Datei `template.env`
erstellen (also `template.env` im selben Verzeichnis duplizieren und die Kopie in `dev.env` umbenennen),
Änderungen sind für das lokale Starten nicht notwendig. Diese Datei wird von `docker-compose` gelesen und ist notwendig
für das Starten des MinIO Containers.
1. Run Configuration `docker-compose.dev.yml: MinIO` starten
oder alternativ `docker-compose -f docker-compose.dev.yml up minio_dev` in der Konsole ausführen.
Dies geht recht schnell. Das MinIO Webinterface ist dann über http://localhost:9000 erreichbar.
Zum Beenden in IntelliJ einfach auf den Knopf drücken, der den `minio_dev`-Service beendet
oder alternativ in der Konsole `docker-compose -f docker-compose.dev.yml down --volumes` ausführen. 
`--volumes` ist notwendig um das erstellte Docker Volume mitzulöschen.
1. Run Configuration `docker-compose.yml: PostgreSQL` starten
oder alternativ `docker-compose up material1_db` in der Konsole ausführen.
Dies kann mehrere Minuten dauern.
Zum Beenden in IntelliJ einfach auf den Knopf drücken, der den `material1_db`-Service beendet
oder alternativ in der Konsole `docker-compose down --volumes` ausführen. 
`--volumes` ist notwendig um das erstellte Docker Volume mitzulöschen.
1. Run Configuration `Material1Application - dev` starten
oder alternativ die Spring Anwendung im Spring Profil `dev` starten.
1. Im Webbrowser kann nun zum Beispiel http://localhost:8080/material1/groups geöffnet werden.

### Production Profile (mit externer PostgreSQL Datenbank und echtem `Gruppenfindung`s-Adapter)

Es existieren IntelliJ Run Configurations um die benötigten Services zu starten.

1. Docker starten und eventuell in IntelliJ einbinden.
1. `update_docker_images.bat/.sh` ausführen, um die Docker Images zu aktualisieren.
1. Eine Datei namens `prod.env` im Wurzelverzeichnis des Projekts nach der Vorlage in der Datei `template.env`
erstellen (also `template.env` im selben Verzeichnis duplizieren und die Kopie in `prod.env` umbenennen),
Änderungen sind für das lokale Starten nicht notwendig. Diese Datei wird von `docker-compose` gelesen und ist notwendig
für das Starten  des PostgreSQL Containers.
1. Eine Datei namens `dev.env` im Wurzelverzeichnis des Projekts nach der Vorlage in der Datei `template.env`
erstellen (also `template.env` im selben Verzeichnis duplizieren und die Kopie in `dev.env` umbenennen),
Änderungen sind für das lokale Starten nicht notwendig. Diese Datei wird von `docker-compose` gelesen und ist notwendig
für das Starten des MinIO Containers.
1. Run Configuration `docker-compose.dev.yml: MinIO` starten
oder alternativ `docker-compose -f docker-compose.dev.yml up minio_dev` in der Konsole ausführen.
Dies geht recht schnell. Das MinIO Webinterface ist dann über http://localhost:9000 erreichbar.
Zum Beenden in IntelliJ einfach auf den Knopf drücken, der den `minio_dev`-Service beendet
oder alternativ in der Konsole `docker-compose -f docker-compose.dev.yml down --volumes` ausführen. 
`--volumes` ist notwendig um das erstellte Docker Volume mitzulöschen.
1. Run Configuration `docker-compose.yml: PostgreSQL` starten
oder alternativ `docker-compose up material1_db` in der Konsole ausführen.
Dies kann mehrere Minuten dauern.
Zum Beenden in IntelliJ einfach auf den Knopf drücken, der den `material1_db`-Service beendet
oder alternativ in der Konsole `docker-compose down --volumes` ausführen. 
`--volumes` ist notwendig um das erstellte Docker Volume mitzulöschen.
1. Run Configuration `Material1Application - prod (local)` starten
oder alternativ die Spring Anwendung im Spring Profil `prod` mit folgenden Umgebungsvariablen starten:
`MATERIAL1_PORT=8080;MATERIAL1_DB_HOST=localhost;MATERIAL1_DB_PORT=5432;POSTGRES_USER=postgres;POSTGRES_PASSWORD=password;POSTGRES_DB=material1;MATERIAL1_MINIO_HOST=http://localhost;MATERIAL1_MINIO_PORT=9000;MATERIAL1_MINIO_BUCKET_NAME=dev-bucket;MINIO_ACCESS_KEY=dev_minio_access_key;MINIO_SECRET_KEY=dev_minio_secret_key;MATERIAL1_GRUPPENFINDUNG_URL=https://mops.hhu.de/gruppe1;MATERIAL1_ADMIN_ROLE=admin;MATERIAL_MAX_FOLDER_PER_GROUP=200`.
Gelesen werden sie von Spring in den einzelnen `application.properties`-Dateien für die Profile.
1. Im Webbrowser kann nun zum Beispiel http://localhost:8080/material1/groups geöffnet werden.

Es ist möglich die Werte der Umgebungsvariablen anzupassen, dann müssen sie allerdings gleichzeitig in der `prod.env`
und der Run Configuration geändert werden.

## Dokumentation

Hier kann die Dokumentation gefunden werden: [Dokumentation](https://hhu-propra2.github.io/abschlussprojekt-mopse/)

Javadoc kann hier aufgerufen werden: [Javadoc](https://hhu-propra2.github.io/abschlussprojekt-mopse/javadoc/)

Die REST API Dokumentation befindet sich hier: [REST API](https://hhu-propra2.github.io/abschlussprojekt-mopse/#section-system-scope-and-context)
