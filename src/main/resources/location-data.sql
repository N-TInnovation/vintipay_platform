-- Inserir países
INSERT INTO countries (name, iso3, numeric_code, iso2, phonecode, capital, currency, currency_name, currency_symbol, tld, native, region, subregion, emoji, emojiU) VALUES
('Cabo Verde', 'CPV', '132', 'CV', '238', 'Praia', 'CVE', 'Escudo cabo-verdiano', 'Esc', '.cv', 'Cabo Verde', 'Africa', 'Western Africa', '🇨🇻', 'U+1F1E8 U+1F1FB'),
('Portugal', 'PRT', '620', 'PT', '351', 'Lisbon', 'EUR', 'Euro', '€', '.pt', 'Portugal', 'Europe', 'Southern Europe', '🇵🇹', 'U+1F1F5 U+1F1F9'),
('Brasil', 'BRA', '076', 'BR', '55', 'Brasília', 'BRL', 'Real brasileiro', 'R$', '.br', 'Brasil', 'Americas', 'South America', '🇧🇷', 'U+1F1E7 U+1F1F7');

-- Inserir estados para Cabo Verde
INSERT INTO states (name, country_id, country_code, state_code, latitude, longitude) VALUES
('Santiago', (SELECT id FROM countries WHERE iso2 = 'CV'), 'CV', 'SV', '15.11667', '-23.61667'),
('Sal', (SELECT id FROM countries WHERE iso2 = 'CV'), 'CV', 'SL', '16.73333', '-22.95000'),
('Boa Vista', (SELECT id FROM countries WHERE iso2 = 'CV'), 'CV', 'BV', '16.10000', '-22.83333');

-- Inserir estados para Portugal
INSERT INTO states (name, country_id, country_code, state_code, latitude, longitude) VALUES
('Lisboa', (SELECT id FROM countries WHERE iso2 = 'PT'), 'PT', 'LX', '38.70000', '-9.18333'),
('Porto', (SELECT id FROM countries WHERE iso2 = 'PT'), 'PT', 'PO', '41.14961', '-8.61099'),
('Faro', (SELECT id FROM countries WHERE iso2 = 'PT'), 'PT', 'FA', '37.01937', '-7.93223');

-- Inserir estados para Brasil
INSERT INTO states (name, country_id, country_code, state_code, latitude, longitude) VALUES
('São Paulo', (SELECT id FROM countries WHERE iso2 = 'BR'), 'BR', 'SP', '-23.55000', '-46.63333'),
('Rio de Janeiro', (SELECT id FROM countries WHERE iso2 = 'BR'), 'BR', 'RJ', '-22.90278', '-43.20694'),
('Minas Gerais', (SELECT id FROM countries WHERE iso2 = 'BR'), 'BR', 'MG', '-19.81749', '-43.95139');

-- Inserir cidades para Santiago (Cabo Verde)
INSERT INTO cities (name, state_id, country_id, country_code, state_code, latitude, longitude) VALUES
('Praia', (SELECT id FROM states WHERE country_code = 'CV' AND state_code = 'SV'), (SELECT id FROM countries WHERE iso2 = 'CV'), 'CV', 'SV', '14.93305', '-23.51333'),
('Assomada', (SELECT id FROM states WHERE country_code = 'CV' AND state_code = 'SV'), (SELECT id FROM countries WHERE iso2 = 'CV'), 'CV', 'SV', '15.10000', '-23.68333'),
('Tarrafal', (SELECT id FROM states WHERE country_code = 'CV' AND state_code = 'SV'), (SELECT id FROM countries WHERE iso2 = 'CV'), 'CV', 'SV', '15.28333', '-23.75000');

-- Inserir cidades para Sal (Cabo Verde)
INSERT INTO cities (name, state_id, country_id, country_code, state_code, latitude, longitude) VALUES
('Espargos', (SELECT id FROM states WHERE country_code = 'CV' AND state_code = 'SL'), (SELECT id FROM countries WHERE iso2 = 'CV'), 'CV', 'SL', '16.75556', '-22.94583'),
('Santa Maria', (SELECT id FROM states WHERE country_code = 'CV' AND state_code = 'SL'), (SELECT id FROM countries WHERE iso2 = 'CV'), 'CV', 'SL', '16.60000', '-22.90000');

-- Inserir cidades para Boa Vista (Cabo Verde)
INSERT INTO cities (name, state_id, country_id, country_code, state_code, latitude, longitude) VALUES
('Sal Rei', (SELECT id FROM states WHERE country_code = 'CV' AND state_code = 'BV'), (SELECT id FROM countries WHERE iso2 = 'CV'), 'CV', 'BV', '16.18333', '-22.91667');

-- Inserir cidades para Lisboa (Portugal)
INSERT INTO cities (name, state_id, country_id, country_code, state_code, latitude, longitude) VALUES
('Lisboa', (SELECT id FROM states WHERE country_code = 'PT' AND state_code = 'LX'), (SELECT id FROM countries WHERE iso2 = 'PT'), 'PT', 'LX', '38.71667', '-9.13333'),
('Sintra', (SELECT id FROM states WHERE country_code = 'PT' AND state_code = 'LX'), (SELECT id FROM countries WHERE iso2 = 'PT'), 'PT', 'LX', '38.80000', '-9.38333'),
('Cascais', (SELECT id FROM states WHERE country_code = 'PT' AND state_code = 'LX'), (SELECT id FROM countries WHERE iso2 = 'PT'), 'PT', 'LX', '38.69694', '-9.42083');

-- Inserir cidades para Porto (Portugal)
INSERT INTO cities (name, state_id, country_id, country_code, state_code, latitude, longitude) VALUES
('Porto', (SELECT id FROM states WHERE country_code = 'PT' AND state_code = 'PO'), (SELECT id FROM countries WHERE iso2 = 'PT'), 'PT', 'PO', '41.14961', '-8.61099'),
('Vila Nova de Gaia', (SELECT id FROM states WHERE country_code = 'PT' AND state_code = 'PO'), (SELECT id FROM countries WHERE iso2 = 'PT'), 'PT', 'PO', '41.13333', '-8.61667'),
('Matosinhos', (SELECT id FROM states WHERE country_code = 'PT' AND state_code = 'PO'), (SELECT id FROM countries WHERE iso2 = 'PT'), 'PT', 'PO', '41.18333', '-8.68333');

-- Inserir cidades para Faro (Portugal)
INSERT INTO cities (name, state_id, country_id, country_code, state_code, latitude, longitude) VALUES
('Faro', (SELECT id FROM states WHERE country_code = 'PT' AND state_code = 'FA'), (SELECT id FROM countries WHERE iso2 = 'PT'), 'PT', 'FA', '37.01937', '-7.93223'),
('Albufeira', (SELECT id FROM states WHERE country_code = 'PT' AND state_code = 'FA'), (SELECT id FROM countries WHERE iso2 = 'PT'), 'PT', 'FA', '37.08889', '-8.25000'),
('Lagos', (SELECT id FROM states WHERE country_code = 'PT' AND state_code = 'FA'), (SELECT id FROM countries WHERE iso2 = 'PT'), 'PT', 'FA', '37.10000', '-8.66667');

-- Inserir cidades para São Paulo (Brasil)
INSERT INTO cities (name, state_id, country_id, country_code, state_code, latitude, longitude) VALUES
('São Paulo', (SELECT id FROM states WHERE country_code = 'BR' AND state_code = 'SP'), (SELECT id FROM countries WHERE iso2 = 'BR'), 'BR', 'SP', '-23.55000', '-46.63333'),
('Campinas', (SELECT id FROM states WHERE country_code = 'BR' AND state_code = 'SP'), (SELECT id FROM countries WHERE iso2 = 'BR'), 'BR', 'SP', '-22.90556', '-47.06083'),
('Santos', (SELECT id FROM states WHERE country_code = 'BR' AND state_code = 'SP'), (SELECT id FROM countries WHERE iso2 = 'BR'), 'BR', 'SP', '-23.96083', '-46.33361');

-- Inserir cidades para Rio de Janeiro (Brasil)
INSERT INTO cities (name, state_id, country_id, country_code, state_code, latitude, longitude) VALUES
('Rio de Janeiro', (SELECT id FROM states WHERE country_code = 'BR' AND state_code = 'RJ'), (SELECT id FROM countries WHERE iso2 = 'BR'), 'BR', 'RJ', '-22.90278', '-43.20694'),
('Niterói', (SELECT id FROM states WHERE country_code = 'BR' AND state_code = 'RJ'), (SELECT id FROM countries WHERE iso2 = 'BR'), 'BR', 'RJ', '-22.88333', '-43.10000'),
('Petrópolis', (SELECT id FROM states WHERE country_code = 'BR' AND state_code = 'RJ'), (SELECT id FROM countries WHERE iso2 = 'BR'), 'BR', 'RJ', '-22.51056', '-43.18417');

-- Inserir cidades para Minas Gerais (Brasil)
INSERT INTO cities (name, state_id, country_id, country_code, state_code, latitude, longitude) VALUES
('Belo Horizonte', (SELECT id FROM states WHERE country_code = 'BR' AND state_code = 'MG'), (SELECT id FROM countries WHERE iso2 = 'BR'), 'BR', 'MG', '-19.92083', '-43.93778'),
('Uberlândia', (SELECT id FROM states WHERE country_code = 'BR' AND state_code = 'MG'), (SELECT id FROM countries WHERE iso2 = 'BR'), 'BR', 'MG', '-18.91861', '-48.27722'),
('Juiz de Fora', (SELECT id FROM states WHERE country_code = 'BR' AND state_code = 'MG'), (SELECT id FROM countries WHERE iso2 = 'BR'), 'BR', 'MG', '-21.76417', '-43.35028');
