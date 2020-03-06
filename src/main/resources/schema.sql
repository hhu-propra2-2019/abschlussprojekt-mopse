SET foreign_key_checks = 0;

DROP TABLE IF EXISTS directory_permissions;
CREATE TABLE directory_permissions
(
    id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT
);

DROP TABLE IF EXISTS directory_permission_entry;
CREATE TABLE directory_permission_entry
(
    id             BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    permissions_id BIGINT             NOT NULL,
    role           VARCHAR(255)       NOT NULL CHECK (role NOT LIKE ''),
    can_read       BOOLEAN            NOT NULL,
    can_write      BOOLEAN            NOT NULL,
    can_delete     BOOLEAN            NOT NULL,
    FOREIGN KEY (permissions_id) REFERENCES directory_permissions (id)
);

DROP TABLE IF EXISTS directory;
CREATE TABLE directory
(
    id             BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    name           VARCHAR(255)       NOT NULL,
    parent_id      BIGINT,
    group_owner    INTEGER            NOT NULL,
    permissions_id BIGINT             NOT NULL,
    FOREIGN KEY (parent_id) REFERENCES directory (id),
    FOREIGN KEY (permissions_id) REFERENCES directory_permissions (id),
    UNIQUE (name, parent_id, group_owner)
);

DROP TABLE IF EXISTS file_info;
CREATE TABLE file_info
(
    id           BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    name         VARCHAR(255)       NOT NULL CHECK (name NOT LIKE ''),
    directory_id BIGINT             NOT NULL,
    type         VARCHAR(255)       NOT NULL CHECK (type NOT LIKE ''),
    size         BIGINT             NOT NULL,
    owner        VARCHAR(255)       NOT NULL CHECK (owner NOT LIKE ''),
    FOREIGN KEY (directory_id) REFERENCES directory (id),
    UNIQUE (name, directory_id)
);

DROP TABLE IF EXISTS file_tag;
CREATE TABLE file_tag
(
    id      BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    name    VARCHAR(255)       NOT NULL CHECK (name NOT LIKE ''),
    file_id BIGINT             NOT NULL,
    FOREIGN KEY (file_id) REFERENCES file_info (id),
    UNIQUE (name, file_id)
);

SET foreign_key_checks = 1;
