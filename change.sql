ALTER TABLE nom_table RENAME COLUMN ancien_nom_colonne TO nouveau_nom_colonne;
ALTER TABLE appart ALTER COLUMN nom TYPE VARCHAR USING nom::VARCHAR;
ALTER TABLE appart DROP CONSTRAINT uk_f3hw800p6cg6f5tlof83nabja;

-- Ajout du mode d'encaissement pour le bailleur (2026-05)
ALTER TABLE client ADD COLUMN IF NOT EXISTS mode_encaissement VARCHAR(50) NOT NULL DEFAULT 'DEUX_ETAPES';

