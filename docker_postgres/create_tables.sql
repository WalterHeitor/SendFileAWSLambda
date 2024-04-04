CREATE TABLE person (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    email VARCHAR(255),
    gender VARCHAR(255),
    ip_address VARCHAR(255)
);

CREATE TABLE contract (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255),
    description VARCHAR(255),
    start_date DATE,
    end_date DATE,
    value NUMERIC,
    status VARCHAR(255),
    person_id INTEGER REFERENCES person(id)
);

-- Inserir três pessoas
INSERT INTO person (first_name, last_name, email, gender, ip_address) VALUES
    ('João', 'Silva', 'joao@example.com', 'Male', '192.168.1.1'),
    ('Maria', 'Santos', 'maria@example.com', 'Female', '192.168.1.2'),
    ('Carlos', 'Oliveira', 'carlos@example.com', 'Male', '192.168.1.3');

-- Inserir dez contratos para a primeira pessoa (João Silva)
INSERT INTO contract (title, description, start_date, end_date, value, status, person_id) VALUES
    ('BACKEND_JAVA', 'Projeto de desenvolvimento Java 1', '2024-01-01', '2024-12-31', 1000.00, 'Active', 1),
    ('FRONTEND_ANGULAR', 'Projeto de desenvolvimento Angular 1', '2024-01-01', '2024-12-31', 1200.00, 'Active', 1),
    ('MOBILE_IOS', 'Projeto de desenvolvimento iOS 1', '2024-01-01', '2024-12-31', 1500.00, 'Active', 1),
    ('MOBILE_ANDROID', 'Projeto de desenvolvimento Android 1', '2024-01-01', '2024-12-31', 1300.00, 'Active', 1),
    ('DEVOPS', 'Projeto de DevOps 1', '2024-01-01', '2024-12-31', 1800.00, 'Active', 1),
    ('BACKEND_JAVA', 'Projeto de desenvolvimento Java 2', '2024-01-01', '2024-12-31', 1000.00, 'Active', 1),
    ('FRONTEND_ANGULAR', 'Projeto de desenvolvimento Angular 2', '2024-01-01', '2024-12-31', 1200.00, 'Active', 1),
    ('MOBILE_IOS', 'Projeto de desenvolvimento iOS 2', '2024-01-01', '2024-12-31', 1500.00, 'Active', 1),
    ('MOBILE_ANDROID', 'Projeto de desenvolvimento Android 2', '2024-01-01', '2024-12-31', 1300.00, 'Active', 1),
    ('DEVOPS', 'Projeto de DevOps 2', '2024-01-01', '2024-12-31', 1800.00, 'Active', 1);

-- Inserir dez contratos para a segunda pessoa (Maria Santos)
INSERT INTO contract (title, description, start_date, end_date, value, status, person_id) VALUES
    ('BACKEND_JAVA', 'Projeto de desenvolvimento Java 1', '2024-01-01', '2024-12-31', 1000.00, 'Active', 2),
    ('FRONTEND_ANGULAR', 'Projeto de desenvolvimento Angular 1', '2024-01-01', '2024-12-31', 1200.00, 'Active', 2),
    ('MOBILE_IOS', 'Projeto de desenvolvimento iOS 1', '2024-01-01', '2024-12-31', 1500.00, 'Active', 2),
    ('MOBILE_ANDROID', 'Projeto de desenvolvimento Android 1', '2024-01-01', '2024-12-31', 1300.00, 'Active', 2),
    ('DEVOPS', 'Projeto de DevOps 1', '2024-01-01', '2024-12-31', 1800.00, 'Active', 2),
    ('BACKEND_JAVA', 'Projeto de desenvolvimento Java 2', '2024-01-01', '2024-12-31', 1000.00, 'Active', 2),
    ('FRONTEND_ANGULAR', 'Projeto de desenvolvimento Angular 2', '2024-01-01', '2024-12-31', 1200.00, 'Active', 2),
    ('MOBILE_IOS', 'Projeto de desenvolvimento iOS 2', '2024-01-01', '2024-12-31', 1500.00, 'Active', 2),
    ('MOBILE_ANDROID', 'Projeto de desenvolvimento Android 2', '2024-01-01', '2024-12-31', 1300.00, 'Active', 2),
    ('DEVOPS', 'Projeto de DevOps 2', '2024-01-01', '2024-12-31', 1800.00, 'Active', 2);

-- Inserir dez contratos para a terceira pessoa (Carlos Oliveira)
INSERT INTO contract (title, description, start_date, end_date, value, status, person_id) VALUES
    ('BACKEND_JAVA', 'Projeto de desenvolvimento Java 1', '2024-01-01', '2024-12-31', 1000.00, 'Active', 3),
    ('FRONTEND_ANGULAR', 'Projeto de desenvolvimento Angular 1', '2024-01-01', '2024-12-31', 1200.00, 'Active', 3),
    ('MOBILE_IOS', 'Projeto de desenvolvimento iOS 1', '2024-01-01', '2024-12-31', 1500.00, 'Active', 3),
    ('MOBILE_ANDROID', 'Projeto de desenvolvimento Android 1', '2024-01-01', '2024-12-31', 1300.00, 'Active', 3),
    ('DEVOPS', 'Projeto de DevOps 1', '2024-01-01', '2024-12-31', 1800.00, 'Active', 3),
    ('BACKEND_JAVA', 'Projeto de desenvolvimento Java 2', '2024-01-01', '2024-12-31', 1000.00, 'Active', 3),
    ('FRONTEND_ANGULAR', 'Projeto de desenvolvimento Angular 2', '2024-01-01', '2024-12-31', 1200.00, 'Active', 3),
    ('MOBILE_IOS', 'Projeto de desenvolvimento iOS 2', '2024-01-01', '2024-12-31', 1500.00, 'Active', 3),
    ('MOBILE_ANDROID', 'Projeto de desenvolvimento Android 2', '2024-01-01', '2024-12-31', 1300.00, 'Active', 3),
    ('DEVOPS', 'Projeto de DevOps 2', '2024-01-01', '2024-12-31', 1800.00, 'Active', 3);


