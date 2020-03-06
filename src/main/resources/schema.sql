DROP TABLE IF EXISTS file_info;
CREATE TABLE file_info
(
    id        BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    name      TEXT NOT NULL CHECK (name NOT LIKE ''),
    directoryID BIGINT NOT NULL,
    type      TEXT NOT NULL CHECK(type NOT LIKE ''),
    size      BIGINT NOT NULL,
    owner     TEXT NOT NULL CHECK(owner NOT LIKE ''),
    FOREIGN KEY(directoryID) REFERENCES directory(id),
    UNIQUE(name, directoryID)
);

DROP TABLE IF EXISTS directory;
CREATE TABLE directory
(
    id                    BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    name                  TEXT NOT NULL,
    parentID              BIGINT,
    group_owner           INTEGER NOT NULL,
    permission_id          BIGINT NOT NULL,
    FOREIGN KEY(parentID) REFERENCES directory(id),
    FOREIGN KEY(permissionID) REFERENCES directory_permissions(id) ,
    UNIQUE(name, parentID, group_owner)
);

DROP TABLE IF EXISTS directory_permissions;
CREATE TABLE directory_permissions
(
    id        BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
);

DROP TABLE IF EXISTS directory_permission_entry;
CREATE TABLE directory_permission_entry
(
    id         BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    permission_id BIGINT NOT NULL,
    role       TEXT NOT NULL CHECK(text NOT LIKE ''),
    can_read   BOOLEAN NOT NULL,
    can_write  BOOLEAN NOT NULL,
    can_delete BOOLEAN NOT NULL,
    FOREIGN KEY(permissionID) REFERENCES directory_permissions(id);
);
