/**
 * Initial schema creation
 */

CREATE TABLE boards (
    id VARCHAR(36) NOT NULL,
    name VARCHAR(255) NOT NULL,
    PRIMARY KEY(id)
)ENGINE=INNODB;

CREATE TABLE stages (
    id VARCHAR(36) NOT NULL,
    name VARCHAR(255) NOT NULL,
    position INTEGER NOT NULL,
    board_id VARCHAR(36) NOT NULL,
    PRIMARY KEY(id),
    CONSTRAINT fk_stages_boards FOREIGN KEY(board_id) REFERENCES boards(id)
)ENGINE=INNODB;

CREATE TABLE work_items (
    id VARCHAR(36) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    position INTEGER NOT NULL,
    board_id VARCHAR(36) NOT NULL,
    stage_id VARCHAR(36) NOT NULL,
    PRIMARY KEY(id),
    CONSTRAINT fk_work_items_boards FOREIGN KEY(board_id) REFERENCES boards(id),
    CONSTRAINT fk_work_items_stages FOREIGN KEY(stage_id) REFERENCES stages(id)
)ENGINE=INNODB;
