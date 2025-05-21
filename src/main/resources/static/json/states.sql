-- Sample States Data for VintiPay

-- Clean existing data if needed
-- DELETE FROM states;

-- Insert sample states data (make sure country_id references exist in the countries table)
INSERT INTO states (id, name, country_id, country_code, state_code, type, latitude, longitude, created_at, updated_at, flag, wikiDataId)
VALUES 
(3446, 'Santiago', 132, 'CV', 'ST', 'Island', '15.05530890', '-23.61309380', NOW(), NOW(), 1, 'Q4798'),
(3447, 'São Vicente', 132, 'CV', 'SV', 'Island', '16.85556000', '-24.98167000', NOW(), NOW(), 1, 'Q4805'),
(3450, 'Lisboa', 177, 'PT', '11', 'District', '38.72554600', '-9.13632500', NOW(), NOW(), 1, 'Q597'),
(3448, 'São Paulo', 31, 'BR', 'SP', 'State', '-23.55052500', '-46.63331300', NOW(), NOW(), 1, 'Q175');
