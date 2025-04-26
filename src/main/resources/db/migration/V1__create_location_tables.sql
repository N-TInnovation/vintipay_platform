-- Criação da tabela de países
CREATE TABLE IF NOT EXISTS `countries` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `iso3` varchar(3) DEFAULT NULL,
  `numeric_code` varchar(3) DEFAULT NULL,
  `iso2` varchar(2) DEFAULT NULL,
  `phonecode` varchar(255) DEFAULT NULL,
  `capital` varchar(255) DEFAULT NULL,
  `currency` varchar(255) DEFAULT NULL,
  `currency_name` varchar(255) DEFAULT NULL,
  `currency_symbol` varchar(255) DEFAULT NULL,
  `tld` varchar(255) DEFAULT NULL,
  `native_name` varchar(255) DEFAULT NULL,
  `region` varchar(255) DEFAULT NULL,
  `subregion` varchar(255) DEFAULT NULL,
  `latitude` decimal(10,8) DEFAULT NULL,
  `longitude` decimal(11,8) DEFAULT NULL,
  `emoji` varchar(255) DEFAULT NULL,
  `emojiU` varchar(255) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `flag` bit(1) DEFAULT NULL,
  `wikiDataId` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_countries_iso2` (`iso2`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Criação da tabela de estados
CREATE TABLE IF NOT EXISTS `states` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `country_id` bigint NOT NULL,
  `country_code` varchar(2) DEFAULT NULL,
  `state_code` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `latitude` decimal(10,8) DEFAULT NULL,
  `longitude` decimal(11,8) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `flag` bit(1) DEFAULT NULL,
  `wikiDataId` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_states_country_id` (`country_id`),
  CONSTRAINT `FK_states_country_id` FOREIGN KEY (`country_id`) REFERENCES `countries` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Criação da tabela de cidades
CREATE TABLE IF NOT EXISTS `cities` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `state_id` bigint NOT NULL,
  `state_code` varchar(255) DEFAULT NULL,
  `country_id` bigint NOT NULL,
  `country_code` varchar(2) DEFAULT NULL,
  `latitude` decimal(10,8) DEFAULT NULL,
  `longitude` decimal(11,8) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `flag` bit(1) DEFAULT NULL,
  `wikiDataId` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_cities_state_id` (`state_id`),
  KEY `FK_cities_country_id` (`country_id`),
  CONSTRAINT `FK_cities_country_id` FOREIGN KEY (`country_id`) REFERENCES `countries` (`id`),
  CONSTRAINT `FK_cities_state_id` FOREIGN KEY (`state_id`) REFERENCES `states` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
