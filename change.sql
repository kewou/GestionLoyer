ALTER TABLE nom_table RENAME COLUMN ancien_nom_colonne TO nouveau_nom_colonne;
ALTER TABLE appart ALTER COLUMN nom TYPE VARCHAR USING nom::VARCHAR;