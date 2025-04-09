CREATE TABLE paciente (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    telefone VARCHAR(20),
    cpf VARCHAR(14) UNIQUE NOT NULL,
    ativo BOOLEAN DEFAULT TRUE
);

CREATE TABLE medico (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    telefone VARCHAR(20),
    crm BIGINT UNIQUE NOT NULL,
    especialidade VARCHAR(50) NOT NULL,
    ativo BOOLEAN DEFAULT TRUE
);

CREATE TABLE endereco (
    id SERIAL PRIMARY KEY,
    medico_id INT UNIQUE NOT NULL,
    logradouro VARCHAR(100),
    numero VARCHAR(10),
    complemento VARCHAR(100),
    bairro VARCHAR(100),
    CONSTRAINT fk_endereco_medico FOREIGN KEY (medico_id) REFERENCES medico(id) ON DELETE CASCADE
);

CREATE TABLE consulta (
    id SERIAL PRIMARY KEY,
    paciente_id INT NOT NULL,
    medico_id INT NOT NULL,
    data_hora TIMESTAMP NOT NULL,
    cancelada BOOLEAN DEFAULT FALSE,
    motivo_cancelamento VARCHAR(50), 

    CONSTRAINT fk_paciente FOREIGN KEY (paciente_id) REFERENCES paciente(id) ON DELETE CASCADE,
    CONSTRAINT fk_medico FOREIGN KEY (medico_id) REFERENCES medico(id) ON DELETE CASCADE
);
