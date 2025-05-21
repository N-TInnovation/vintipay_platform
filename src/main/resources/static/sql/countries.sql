-- Sample Countries Data for VintiPay

-- Clean existing data if needed
-- DELETE FROM countries;

-- Insert sample data
INSERT INTO countries (id, name, iso3, numeric_code, iso2, phonecode, capital, currency, currency_name, currency_symbol, tld, native_name, region, subregion, latitude, longitude, emoji, emojiU, flag, wikiDataId)
VALUES 
(132, 'Cabo Verde', 'CPV', '132', 'CV', '238', 'Praia', 'CVE', 'Cape Verdean escudo', 'Esc', '.cv', 'Cabo Verde', 'Africa', 'Western Africa', '16.00000000', '-24.00000000', '🇨🇻', 'U+1F1E8 U+1F1FB', 1, 'Q1011'),
(31, 'Brazil', 'BRA', '076', 'BR', '55', 'Brasília', 'BRL', 'Brazilian real', 'R$', '.br', 'Brasil', 'Americas', 'South America', '-10.00000000', '-55.00000000', '🇧🇷', 'U+1F1E7 U+1F1F7', 1, 'Q155'),
(177, 'Portugal', 'PRT', '620', 'PT', '351', 'Lisbon', 'EUR', 'Euro', '€', '.pt', 'Portugal', 'Europe', 'Southern Europe', '39.50000000', '-8.00000000', '🇵🇹', 'U+1F1F5 U+1F1F9', 1, 'Q45');
