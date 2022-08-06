INSERT INTO users  (id,name, last_name, email, role, password) VALUES (1000,'NOUMIA','joel','kewou.noumia@gmail.com','ADMIN','test');

INSERT INTO logement (id,address,description,montant_loyer,user_id) VALUES (1,'Nkomkana','Duplex',5000,1000);

INSERT INTO recap_by_month(id,date_versement,logement_id,montant_verser,solde) VALUES (1,null,1,null,null);