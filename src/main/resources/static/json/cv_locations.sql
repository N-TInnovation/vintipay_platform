-- Cape Verde Locations Sample Data
-- This SQL script can be used instead of the Excel file

-- Clear existing data if any
DELETE FROM cv_locations;

-- Insert sample data for Cape Verde locations
INSERT INTO cv_locations (id, island, county, parish, zone, subzone, lat, lng, name) VALUES 
(1, 'Santiago', 'Praia', 'Nossa Senhora da Graça', 'Plateau', 'Centro', '14.9177', '-23.5092', 'Plateau'),
(2, 'Santiago', 'Praia', 'Nossa Senhora da Graça', 'Achada Santo António', 'Quebra Canela', '14.9040', '-23.5135', 'Quebra Canela'),
(3, 'Santiago', 'Santa Catarina', 'Santa Catarina', 'Assomada', 'Centro', '15.1000', '-23.6667', 'Assomada Centro'),
(4, 'São Vicente', 'São Vicente', 'Nossa Senhora da Luz', 'Mindelo', 'Centro', '16.8901', '-24.9804', 'Mindelo Centro'),
(5, 'São Vicente', 'São Vicente', 'Nossa Senhora da Luz', 'Mindelo', 'Laginha', '16.8967', '-24.9822', 'Praia da Laginha'),
(6, 'Santo Antão', 'Ribeira Grande', 'Nossa Senhora do Rosário', 'Ponta do Sol', 'Centro', '17.2000', '-25.0833', 'Ponta do Sol'),
(7, 'Santo Antão', 'Porto Novo', 'São João Baptista', 'Porto Novo', 'Centro', '17.0167', '-25.0667', 'Porto Novo'),
(8, 'Sal', 'Sal', 'Nossa Senhora das Dores', 'Santa Maria', 'Centro', '16.6000', '-22.9000', 'Santa Maria'),
(9, 'Sal', 'Sal', 'Nossa Senhora das Dores', 'Espargos', 'Centro', '16.7500', '-22.9444', 'Espargos'),
(10, 'Boa Vista', 'Boa Vista', 'Santa Isabel', 'Sal Rei', 'Centro', '16.1833', '-22.9167', 'Sal Rei');
