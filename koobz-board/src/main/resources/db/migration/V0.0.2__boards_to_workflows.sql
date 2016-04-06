RENAME TABLE boards TO workflows;

ALTER TABLE stages CHANGE board_id workflow_id VARCHAR(36) NOT NULL;

ALTER TABLE work_items CHANGE board_id workflow_id VARCHAR(36) NOT NULL;
