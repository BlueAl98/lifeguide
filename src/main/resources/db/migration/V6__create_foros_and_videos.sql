CREATE TABLE foros (
    id          BIGSERIAL PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    description VARCHAR(1000) NOT NULL,
    category_id BIGINT NOT NULL REFERENCES categories(id) ON DELETE CASCADE
);

CREATE TABLE videos (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    url         VARCHAR(500) NOT NULL,
    category_id BIGINT NOT NULL REFERENCES categories(id) ON DELETE CASCADE
);

-- Fake data below is for local/manual verification only, not real content.
INSERT INTO categories (name, goal_id) VALUES
    ('Cardio', (SELECT id FROM goals WHERE name = 'Fitness')),
    ('Fuerza', (SELECT id FROM goals WHERE name = 'Fitness')),
    ('Meditacion', (SELECT id FROM goals WHERE name = 'Salud mental'));

INSERT INTO foros (title, description, category_id) VALUES
    ('Rutinas de cardio para principiantes',
     'Comparte y pregunta sobre rutinas de cardio para arrancar.',
     (SELECT id FROM categories WHERE name = 'Cardio')),
    ('Entrenamiento de fuerza en casa',
     'Tips y dudas sobre entrenar fuerza sin ir al gimnasio.',
     (SELECT id FROM categories WHERE name = 'Fuerza')),
    ('Tecnicas de respiracion',
     'Discusion sobre ejercicios de respiracion para meditar.',
     (SELECT id FROM categories WHERE name = 'Meditacion'));

INSERT INTO videos (name, url, category_id) VALUES
    ('Cardio HIIT 20 minutos', 'https://example.com/videos/cardio-hiit-20',
     (SELECT id FROM categories WHERE name = 'Cardio')),
    ('Rutina de fuerza con mancuernas', 'https://example.com/videos/fuerza-mancuernas',
     (SELECT id FROM categories WHERE name = 'Fuerza')),
    ('Meditacion guiada 10 minutos', 'https://example.com/videos/meditacion-guiada-10',
     (SELECT id FROM categories WHERE name = 'Meditacion'));
