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

### Dev

Es existieren IntelliJ Run Configurations um die benötigten Services zu starten.

1. Docker starten und eventuell in IntelliJ einbinden.
1. Run Configuration `docker-compose.dev.yml: MinIO` starten
oder alternativ `docker-compose -f docker-compose.dev.yml minio_dev` in der Konsole.
1. Run Configuration `Material1Application - dev` starten.
oder alternativ die Spring Anwendung im Profil `dev` starten.
1. Im Webbrowser kann nun zum Beispiel http://localhost:8080/material1/groups geöffnet werden.

### Prod

Es existieren IntelliJ Run Configurations um die benötigten Services zu starten.

1. Docker starten und eventuell in IntelliJ einbinden.
1. Eine Datei namens `prod.env` im Wurzelverzeichnis des Projekts nach der Vorlage der in der Datei `prod-template.env`
erstellen (also `prod-template.env` im selben Verzeichnis duplizieren und die Kopie in `prod.env` umbenennen),
Änderungen sind für das lokale Starten nicht notwendig. Diese Datei wird von `docker-compose` gelesen und ist notwendig
für das Starten des MariaDB Containers.
1. Run Configuration `docker-compose.dev.yml: MinIO` starten.
oder alternativ `docker-compose -f docker-compose.dev.yml minio_dev` in der Konsole.
Dies geht recht schnell.
1. Run Configuration `docker-compose.yml: MariaDB` starten
oder alternativ `docker-compose material1_db` in der Konsole.
Dies kann mehrere Minuten dauern.
1. Run Configuration `Material1Application - prod (local)` starten
oder alternativ die Spring Anwendung im Profil `prod` mit folgenden Umgebungsvariablen starten:
`MYSQL_ROOT_PASSWORD=password;MYSQL_DATABASE=material1;MATERIAL1_PORT=8080;MATERIAL1_DB_PORT=23306;MATERIAL1_DB_HOST=localhost;MINIO_HOST=http://localhost;MINIO_PORT=9000;MINIO_BUCKET_NAME=dev-bucket;MINIO_ACCESS_KEY=dev_minio_access_key;MINIO_SECRET_KEY=dev_minio_secret_key`.
Gelesen werden sie von Spring in den einzelnen `application.properties` für die Profile.
1. Im Webbrowser kann nun zum Beispiel http://localhost:8080/material1/groups geöffnet werden.

Es ist möglich die Werte der Umgebungsvariablen zu ändern, dann müssen sie allerdings gleichzeitig in der `prod.env`
und der Run Configuration geändert werden.

## Dokumentation

Hier kann die Dokumentation gefunden werden: [Dokumentation](https://hhu-propra2.github.io/abschlussprojekt-mopse/)

Javadoc kann hier aufgerufen werden: [Javadoc](https://hhu-propra2.github.io/abschlussprojekt-mopse/javadoc/)

Die REST API Documentation befindet sich hier: [REST API](https://hhu-propra2.github.io/abschlussprojekt-mopse/#section-system-scope-and-context)
