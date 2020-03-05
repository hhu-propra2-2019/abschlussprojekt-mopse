DROP TABLE IF EXISTS file_info;
CREATE TABLE file_info
(
    id        integer PRIMARY KEY AUTO_INCREMENT,
    name      text,
    directory integer,
    type      text,
    size      integer,
    owner     text
);

DROP TABLE IF EXISTS directory;
CREATE TABLE directory
(
    id                    integer PRIMARY KEY AUTO_INCREMENT,
    name                  text,
    parent                integer,
    group_owner           integer
);

DROP TABLE IF EXISTS directory_permissions;
CREATE TABLE directory_permissions
(
    id        integer PRIMARY KEY AUTO_INCREMENT,
    directory integer
);

DROP TABLE IF EXISTS directory_permission_entry;
CREATE TABLE directory_permission_entry
(
    id         integer PRIMARY KEY AUTO_INCREMENT,
    permission integer,
    role       text,
    can_read   bool,
    can_write  bool,
    can_delete bool
);
