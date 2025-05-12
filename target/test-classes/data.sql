DROP TABLE IF EXISTS ai_validation_results;
CREATE TABLE ai_validation_results (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    aiReferenceId VARCHAR(255),
    feedback LONGTEXT,
    originalInstruction TEXT,
    safetyApproved BOOLEAN,
    scientificAccuracy BOOLEAN,
    valid BOOLEAN,
    validatedInstruction TEXT,
    validationDate DATETIME,
    validationSource VARCHAR(255),
    interaction_id BIGINT,
    CONSTRAINT fk_interaction FOREIGN KEY (interaction_id) REFERENCES ai_interactions(interaction_id) ON DELETE SET NULL
);
-- Ensure feedback column is LONGTEXT for tests
ALTER TABLE ai_validation_results MODIFY feedback LONGTEXT;
/* Remove DROP/CREATE statements - Hibernate will create tables */
DELETE FROM experiment_materials;
DELETE FROM steps;
DELETE FROM experiments;
DELETE FROM categories;
DELETE FROM users;
DELETE FROM roles;
-- Остальные INSERT...
INSERT INTO categories (category_name) VALUES ('Физические эксперименты');
INSERT IGNORE INTO roles(name) VALUES('ROLE_ADMIN');
INSERT IGNORE INTO roles(name) VALUES('ROLE_STUDENT');
INSERT IGNORE INTO roles(name) VALUES('ROLE_TEACHER');
-- Associate experiment with test user
INSERT INTO Experiments (title, short_description, slug, category_id, user_id) VALUES
('Воздушная пушка', 'Демонстрация свойств атмосферного давления',  'vozdushnaya-pushka', 1,
 (SELECT id FROM Users WHERE username = 'physics_user'));

INSERT INTO Materials (material_name) VALUES
('Пластиковая бутылка'),
('Воздушный шарик'),
('Картонная трубка'),
('Скотч');

INSERT INTO ExperimentMaterials (experiment_id, material_id, quantity) VALUES
(1, 1, '1 шт'),
(1, 2, '2 шт'),
(1, 3, '30 см'),
(1, 4, '1 рулон');

INSERT INTO Steps (experiment_id, step_number, description, required_materials) VALUES
(1, 1, 'Обрежьте дно бутылки', 'Пластиковая бутылка, Ножницы'),
(1, 2, 'Закрепите шарик на горлышке', 'Воздушный шарик, Скотч'),
(1, 3, 'Создайте пусковую установку', 'Картонная трубка, Скотч');

/* Clean up existing test data */
DELETE FROM experiment_materials;
DELETE FROM steps;
DELETE FROM experiments;
DELETE FROM categories;
DELETE FROM users;
DELETE FROM roles;

/* Clean up existing test data */
DELETE FROM experiment_materials;
DELETE FROM steps;
DELETE FROM experiments;
DELETE FROM categories;
DELETE FROM users;
DELETE FROM roles;

/* Insert test user */
INSERT IGNORE INTO users (username, email, password, first_name, last_name) 
VALUES ('physics_user', 'user@physics.ru', '$2a$10$xn3LI/AjqicFYZFruSw7.8IA5W9j8gZkpGqk60b6dqsN.7YgM6qO', 'Дмитрий', 'Иванов');

/* Insert user role mapping */
INSERT IGNORE INTO user_roles (user_id, role_id)
VALUES (
    (SELECT id FROM users WHERE username = 'physics_user'),
    (SELECT role_id FROM roles WHERE name = 'ROLE_TEACHER')
);

/* Insert category */
INSERT IGNORE INTO categories (category_name) VALUES ('Физика');

/* Insert AI test data */
INSERT IGNORE INTO ai_interactions (
    interaction_type, 
    input_text, 
    output_text,
    user_id,
    experiment_id
) VALUES (
    'VALIDATION',
    'Test steps',
    'Validation passed',
    (SELECT id FROM users WHERE username = 'physics_user'),
    1
);

INSERT IGNORE INTO ai_validation_results (
    valid,
    feedback,
    interaction_id
) VALUES (
    true,
    'Test validation feedback',
    (SELECT interaction_id FROM ai_interactions LIMIT 1)
);


CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(role_id) ON DELETE CASCADE
);

CREATE TABLE submissions (
    submission_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    experiment_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    submission_data JSON,
    submitted_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    status ENUM('SUBMITTED', 'UNDER_REVIEW', 'COMPLETED') NOT NULL,
    FOREIGN KEY (experiment_id) REFERENCES experiments(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE attachments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    file_name VARCHAR(255) NOT NULL,
    file_type VARCHAR(255) NOT NULL,
    file_path VARCHAR(255) NOT NULL,
    file_size BIGINT,
    submission_id BIGINT NOT NULL,
    FOREIGN KEY (submission_id) REFERENCES submissions(submission_id) ON DELETE CASCADE
);
