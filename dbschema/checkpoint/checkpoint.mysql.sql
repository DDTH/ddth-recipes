-- MySQL Schema for Checkpoint

DROP TABLE IF EXISTS dr_checkpoint;
CREATE TABLE dr_checkpoint (
    cp_id                       VARCHAR(64),
        PRIMARY KEY (cp_id),
    cp_timestamp                DATETIME,
    cp_data                     MEDIUMTEXT
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
