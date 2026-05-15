ALTER TABLE nom_table RENAME COLUMN ancien_nom_colonne TO nouveau_nom_colonne;
ALTER TABLE appart ALTER COLUMN nom TYPE VARCHAR USING nom::VARCHAR;
ALTER TABLE appart DROP CONSTRAINT uk_f3hw800p6cg6f5tlof83nabja;

-- ============================================================================
-- Locataires virtuels : permettre email NULL avec unicite partielle,
-- et garantir l'unicite du code d'invitation seulement quand il est present.
-- A executer une seule fois sur la base PostgreSQL.
-- ============================================================================

-- 1. Supprimer la contrainte unique stricte sur email (le nom de la contrainte
--    est genere par Hibernate ; ajuster la commande au besoin via \d client).
DO $$
DECLARE
    cname text;
BEGIN
    SELECT conname INTO cname
    FROM pg_constraint
    WHERE conrelid = 'client'::regclass
      AND contype = 'u'
      AND pg_get_constraintdef(oid) LIKE '%(email)%'
    LIMIT 1;
    IF cname IS NOT NULL THEN
        EXECUTE format('ALTER TABLE client DROP CONSTRAINT %I', cname);
    END IF;
END$$;

-- 2. Index unique partiel : email unique seulement quand non null
CREATE UNIQUE INDEX IF NOT EXISTS ux_client_email_not_null
    ON client (email) WHERE email IS NOT NULL;

-- 3. Index unique partiel sur invitation_code (Hibernate ne sait pas creer un partiel)
CREATE UNIQUE INDEX IF NOT EXISTS ux_client_invitation_code
    ON client (invitation_code) WHERE invitation_code IS NOT NULL;
