-- SQL statements to repair the failed Flyway migration
-- First delete the failed migration entry
DELETE FROM flyway_schema_history WHERE version = '32' AND success = 0;

-- Make sure the table doesn't exist (in case it was partially created)
DROP TABLE IF EXISTS preferencias_notificacao;

-- If you need to clean all migrations (only use if necessary)
-- DELETE FROM flyway_schema_history WHERE version > '0';
