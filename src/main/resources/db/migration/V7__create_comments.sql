CREATE TABLE comments (
    id          BIGSERIAL PRIMARY KEY,
    username    VARCHAR(255) NOT NULL,
    date        TIMESTAMP NOT NULL DEFAULT now(),
    description VARCHAR(1000) NOT NULL,
    likes       INTEGER NOT NULL DEFAULT 0,
    foro_id     BIGINT NOT NULL REFERENCES foros(id) ON DELETE CASCADE
);
