ALTER TABLE nom_table RENAME COLUMN ancien_nom_colonne TO nouveau_nom_colonne;
ALTER TABLE appart ALTER COLUMN nom TYPE VARCHAR USING nom::VARCHAR;
ALTER TABLE appart DROP CONSTRAINT uk_f3hw800p6cg6f5tlof83nabja;
