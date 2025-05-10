-- Criação da tabela de localizações de Cabo Verde
CREATE TABLE IF NOT EXISTS cv_locations (
  id bigint NOT NULL AUTO_INCREMENT,
  island varchar(255) DEFAULT NULL,
  county varchar(255) DEFAULT NULL,
  parish varchar(255) DEFAULT NULL,
  zone varchar(255) DEFAULT NULL,
  subzone varchar(255) DEFAULT NULL,
  lat varchar(255) DEFAULT NULL,
  lng varchar(255) DEFAULT NULL,
  name varchar(255) DEFAULT NULL,
  PRIMARY KEY (id)
);
