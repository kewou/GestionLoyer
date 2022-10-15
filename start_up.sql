CREATE USER IF NOT EXISTS 'readUser'@'localhost' IDENTIFIED BY 'test';
GRANT SELECT ON gestionloyer.* TO 'readUser'@'localhost';
FLUSH PRIVILEGES;

