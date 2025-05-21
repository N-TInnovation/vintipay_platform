-- Sample Cities Data for VintiPay

-- Clean existing data if needed
-- DELETE FROM cities;

-- Insert sample cities data (make sure state_id and country_id references exist in their respective tables)
INSERT INTO cities (id, name, state_id, state_code, country_id, country_code, latitude, longitude, created_at, updated_at, flag, wikiDataId)
VALUES 
(47745, 'Praia', 3446, 'ST', 132, 'CV', '14.93305000', '-23.51333000', NOW(), NOW(), 1, 'Q3751'),
(47746, 'Mindelo', 3447, 'SV', 132, 'CV', '16.88778000', '-24.98167000', NOW(), NOW(), 1, 'Q153467'),
(47748, 'Lisboa', 3450, '11', 177, 'PT', '38.72554600', '-9.13632500', NOW(), NOW(), 1, 'Q597'),
(47747, 'São Paulo', 3448, 'SP', 31, 'BR', '-23.55052500', '-46.63331300', NOW(), NOW(), 1, 'Q174');
