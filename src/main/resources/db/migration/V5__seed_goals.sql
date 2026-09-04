ALTER TABLE goals ADD CONSTRAINT goals_name_key UNIQUE (name);

INSERT INTO goals (name) VALUES ('Fitness'), ('Salud mental')
ON CONFLICT (name) DO NOTHING;
