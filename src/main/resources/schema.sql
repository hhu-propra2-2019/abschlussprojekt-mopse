CREATE TABLE directory_permissions
(
    id                 BIGINT PRIMARY KEY AUTO_INCREMENT,
    creation_time      TIMESTAMP(9) NOT NULL,
    last_modified_time TIMESTAMP(9) NOT NULL
);

CREATE TABLE directory_permission_entry
(
    permissions_id BIGINT       NOT NULL,
    role           VARCHAR(255) NOT NULL CHECK (role NOT LIKE ''),
    can_read       BOOLEAN      NOT NULL,
    can_write      BOOLEAN      NOT NULL,
    can_delete     BOOLEAN      NOT NULL,
    CONSTRAINT fk_entry_perm FOREIGN KEY (permissions_id) REFERENCES directory_permissions (id)
);

CREATE TABLE directory
(
    id                 BIGINT PRIMARY KEY AUTO_INCREMENT,
    name               VARCHAR(255) NOT NULL,
    parent_id          BIGINT,
    group_owner        INTEGER      NOT NULL,
    permissions_id     BIGINT       NOT NULL,
    creation_time      TIMESTAMP(9) NOT NULL,
    last_modified_time TIMESTAMP(9) NOT NULL,
    CONSTRAINT fk_dir_dir FOREIGN KEY (parent_id) REFERENCES directory (id),
    CONSTRAINT fk_dir_perm FOREIGN KEY (permissions_id) REFERENCES directory_permissions (id),
    CONSTRAINT u_dir UNIQUE (name, parent_id, group_owner)
);

CREATE TABLE file_info
(
    id                 BIGINT PRIMARY KEY AUTO_INCREMENT,
    name               VARCHAR(255) NOT NULL CHECK (name NOT LIKE ''),
    directory_id       BIGINT       NOT NULL,
    type               VARCHAR(255) NOT NULL CHECK (type NOT LIKE ''),
    size               BIGINT       NOT NULL,
    owner              VARCHAR(255) NOT NULL CHECK (owner NOT LIKE ''),
    creation_time      TIMESTAMP(9) NOT NULL,
    last_modified_time TIMESTAMP(9) NOT NULL,
    CONSTRAINT fk_file_dir FOREIGN KEY (directory_id) REFERENCES directory (id),
    CONSTRAINT u_file UNIQUE (name, directory_id)
);

CREATE TABLE file_tag
(
    name    VARCHAR(255) NOT NULL CHECK (name NOT LIKE ''),
    file_id BIGINT       NOT NULL,
    CONSTRAINT fk_tag_file FOREIGN KEY (file_id) REFERENCES file_info (id),
    CONSTRAINT u_tag UNIQUE (name, file_id)
);
