-- Existing rows get '' so the NOT NULL constraint can be applied; the
-- default is dropped right after so new inserts must provide real values.
-- IF NOT EXISTS: some dev databases already got these columns from an
-- earlier, since-removed migration.
ALTER TABLE users
    ADD COLUMN IF NOT EXISTS first_name VARCHAR(100) NOT NULL DEFAULT '',
    ADD COLUMN IF NOT EXISTS last_name  VARCHAR(100) NOT NULL DEFAULT '',
    ADD COLUMN IF NOT EXISTS birth_date DATE;

ALTER TABLE users
    ALTER COLUMN first_name DROP DEFAULT,
    ALTER COLUMN last_name  DROP DEFAULT;
