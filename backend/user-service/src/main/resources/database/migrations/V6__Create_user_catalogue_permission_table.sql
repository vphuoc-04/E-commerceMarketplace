CREATE TABLE user_catalogue_permission (
    user_catalogue_id BIGINT UNSIGNED NOT NULL,
    permission_id BIGINT NOT NULL,
    PRIMARY KEY (user_catalogue_id, permission_id),
    FOREIGN KEY (user_catalogue_id) REFERENCES user_catalogues(id),
    FOREIGN KEY (permission_id) REFERENCES permissions(id)
); 