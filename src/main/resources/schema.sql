CREATE TABLE directory_permissions
(
    id                 BIGSERIAL PRIMARY KEY,
    creation_time      TIMESTAMP NOT NULL,
    last_modified_time TIMESTAMP NOT NULL
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

CREATE INDEX i_entry_perm ON directory_permission_entry (permissions_id);

CREATE TABLE directory
(
    id                 BIGSERIAL PRIMARY KEY,
    name               VARCHAR(255) NOT NULL,
    parent_id          BIGINT,
    group_owner        BIGINT       NOT NULL,
    permissions_id     BIGINT       NOT NULL,
    creation_time      TIMESTAMP    NOT NULL,
    last_modified_time TIMESTAMP    NOT NULL,
    CONSTRAINT fk_dir_dir FOREIGN KEY (parent_id) REFERENCES directory (id),
    CONSTRAINT fk_dir_perm FOREIGN KEY (permissions_id) REFERENCES directory_permissions (id),
    CONSTRAINT u_dir UNIQUE (name, parent_id, group_owner)
);

CREATE INDEX i_dir_dir ON directory (parent_id);
CREATE INDEX i_dir_perm ON directory (permissions_id);

CREATE TABLE file_info
(
    id                 BIGSERIAL PRIMARY KEY,
    name               VARCHAR(255) NOT NULL CHECK (name NOT LIKE ''),
    directory_id       BIGINT       NOT NULL,
    type               VARCHAR(255) NOT NULL CHECK (type NOT LIKE ''),
    size               BIGINT       NOT NULL,
    owner              VARCHAR(255) NOT NULL CHECK (owner NOT LIKE ''),
    creation_time      TIMESTAMP    NOT NULL,
    last_modified_time TIMESTAMP    NOT NULL,
    CONSTRAINT fk_file_dir FOREIGN KEY (directory_id) REFERENCES directory (id),
    CONSTRAINT u_file UNIQUE (name, directory_id)
);

CREATE INDEX i_file_dir ON file_info (directory_id);

CREATE TABLE file_tag
(
    name    VARCHAR(255) NOT NULL CHECK (name NOT LIKE ''),
    file_id BIGINT       NOT NULL,
    CONSTRAINT fk_tag_file FOREIGN KEY (file_id) REFERENCES file_info (id),
    CONSTRAINT u_tag UNIQUE (name, file_id)
);

CREATE INDEX i_tag_file ON file_tag (file_id);

CREATE TABLE group_table
(
    id                 BIGSERIAL PRIMARY KEY,
    group_id           UUID         NOT NULL,
    name               VARCHAR(255) NOT NULL CHECK (name NOT LIKE ''),
    creation_time      TIMESTAMP    NOT NULL,
    last_modified_time TIMESTAMP    NOT NULL
);

CREATE INDEX i_group_id ON group_table (group_id);

CREATE TABLE group_member
(
    group_id BIGINT       NOT NULL,
    name     VARCHAR(255) NOT NULL CHECK (name NOT LIKE ''),
    role     VARCHAR(255) NOT NULL CHECK (role NOT LIKE ''),
    CONSTRAINT fk_member_group FOREIGN KEY (group_id) REFERENCES group_table (id),
    CONSTRAINT u_member UNIQUE (group_id, name)
);

CREATE INDEX i_member_group ON group_member (group_id);

CREATE TABLE latest_event_id
(
    id       INT PRIMARY KEY,
    event_id BIGINT NOT NULL
);
