package cv.ntinnovation.vintipay.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.ResourceUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.event.EventListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;

/**
 * Configuração para importar dados de localização (países, estados, cidades)
 * dos arquivos SQL para o banco de dados.
 */
@Slf4j
@Configuration
public class DataImportConfig {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Bean para importar dados de localização após a inicialização da aplicação
     */
    @EventListener(ApplicationReadyEvent.class)
    public void importLocationData() {
        log.info("Verificando e importando dados de localização...");
        
        try {
            // Verificando e criando tabelas primeiro
            createLocationTables();
            
            // Removendo verificação que pode impedir a importação
            log.info("Iniciando importação de dados de localização...");
            
            // A importação real é feita pelos CommandLineRunners no LocationDataImporter
        } catch (Exception e) {
            log.error("Erro ao importar dados de localização: {}", e.getMessage(), e);
        }
    }

    /**
     * Verifica se os dados de localização já foram importados
     */
    private boolean isDataAlreadyImported() {
        try {
            Integer countCountries = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM countries", Integer.class);
            log.info("Verificação de dados existentes: {} países encontrados no banco", countCountries);
            return countCountries != null && countCountries > 0;
        } catch (Exception e) {
            // Se a tabela não existir ainda, retorna false
            log.warn("Erro ao verificar se dados já foram importados: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Verifica se um recurso existe no classpath
     */
    private boolean resourceExists(String path) {
        try {
            Resource resource = new ClassPathResource(path);
            return resource.exists();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Tenta carregar um recurso do classpath ou do sistema de arquivos,
     * verificando várias localizações possíveis
     */
    private InputStream getResourceAsStream(String filename) {
        List<String> classpathLocations = Arrays.asList(
            filename,                    // Diretamente na raiz do classpath
            "sql/" + filename,           // Na pasta sql do classpath
            "app/sql/" + filename,       // Na pasta app/sql do classpath
            "static/sql/" + filename,    // Na pasta static/sql do classpath
            "resources/sql/" + filename  // Na pasta resources/sql do classpath
        );
        
        List<String> fileSystemLocations = Arrays.asList(
            System.getProperty("user.dir") + "/" + filename,                 // Na raiz do projeto
            System.getProperty("user.dir") + "/sql/" + filename,             // Na pasta sql do projeto
            System.getProperty("user.dir") + "/app/sql/" + filename,         // Na pasta app/sql do projeto
            System.getProperty("user.home") + "/sql/" + filename,            // Na pasta sql da home do usuário
            "/app/" + filename,                                              // Na pasta app (Docker)
            "/app/sql/" + filename,                                          // Na pasta app/sql (Docker)
            "/app/resources/sql/" + filename                                 // Na pasta app/resources/sql (Docker)
        );
        
        // Primeiro, tenta todas as localizações do classpath
        for (String location : classpathLocations) {
            try {
                if (resourceExists(location)) {
                    log.info("Recurso SQL encontrado no classpath: {}", location);
                    return new ClassPathResource(location).getInputStream();
                }
            } catch (Exception e) {
                // Ignora e tenta a próxima localização
            }
        }
        
        // Em seguida, tenta todas as localizações do sistema de arquivos
        for (String location : fileSystemLocations) {
            try {
                File file = new File(location);
                if (file.exists()) {
                    log.info("Recurso SQL encontrado no sistema de arquivos: {}", location);
                    return new FileInputStream(file);
                }
            } catch (Exception e) {
                // Ignora e tenta a próxima localização
            }
        }
        
        log.warn("Recurso SQL '{}' não encontrado em nenhuma localização.", filename);
        return null;
    }
    
    /**
     * Importa os dados de localização dos arquivos SQL
     */
    private void importLocationDataFromFiles() {
        log.info("Iniciando importação de dados de localização...");

        try {
            // Verificar se os arquivos existem e importar
            boolean countriesImported = importCountriesFromSQL();
            boolean statesImported = importStatesFromSQL();
            boolean citiesImported = importCitiesFromSQL();
            
            if (!countriesImported || !statesImported || !citiesImported) {
                log.warn("Alguns arquivos SQL de localização não foram encontrados. " +
                      "A aplicação continuará sem dados completos de localização.");
            } else {
                log.info("Importação de dados de localização concluída com sucesso!");
            }
        } catch (Exception e) {
            log.error("Erro durante a importação de dados de localização", e);
            // Não interromper a inicialização da aplicação mesmo em caso de erro
        }
    }
    
    /**
     * Importa países do arquivo SQL
     */
    private boolean importCountriesFromSQL() {
        try {
            log.info("Verificando se é necessário importar países do SQL...");
            
            // Verificar se já existem países no banco de dados
            try {
                int countryCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM countries", Integer.class);
                if (countryCount > 0) {
                    log.info("Países já existem no banco de dados. Pulando importação SQL.");
                    return true;
                }
            } catch (Exception e) {
                log.warn("Erro ao verificar tabela countries. A tabela pode não existir ainda: {}", e.getMessage());
                return false; // Agora temos o método verifyAndCreateLocationTables para criar as tabelas
            }
            
            String countriesSqlPath = getSqlFilePath("countries.sql");
            if (countriesSqlPath == null) {
                log.warn("Arquivo countries.sql não encontrado. Pulando importação SQL de países.");
                return false;
            }
            
            log.info("Iniciando importação de países do SQL...");
            File countriesSqlFile = new File(countriesSqlPath);
            
            try (BufferedReader reader = new BufferedReader(new FileReader(countriesSqlFile))) {
                String line;
                StringBuilder sqlCommand = new StringBuilder();
                
                while ((line = reader.readLine()) != null) {
                    // Ignora linhas de comentário e diretivas específicas do MySQL
                    if (line.startsWith("--") || line.startsWith("/*") || line.startsWith("/*!") || line.trim().isEmpty()) {
                        continue;
                    }
                    
                    sqlCommand.append(line);
                    
                    // Se a linha termina com ponto e vírgula, é o fim de um comando SQL
                    if (line.trim().endsWith(";")) {
                        String sql = sqlCommand.toString();
                        try {
                            jdbcTemplate.execute(sql);
                        } catch (Exception e) {
                            log.warn("Erro ao executar comando SQL: {}", sql, e);
                        }
                        sqlCommand = new StringBuilder();
                    }
                }
                
                log.info("Importação de países do SQL concluída com sucesso.");
                return true;
            } catch (Exception e) {
                log.error("Erro ao importar países do SQL", e);
                return false;
            }
        } catch (Exception e) {
            log.error("Erro geral durante a importação de países do SQL", e);
            return false;
        }
    }
    
    /**
     * Importa estados do arquivo SQL
     */
    private boolean importStatesFromSQL() {
        try {
            log.info("Verificando se é necessário importar estados do SQL...");
            
            // Verificar se já existem estados no banco de dados
            try {
                int stateCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM states", Integer.class);
                if (stateCount > 0) {
                    log.info("Estados já existem no banco de dados. Pulando importação SQL.");
                    return true;
                }
            } catch (Exception e) {
                log.warn("Erro ao verificar tabela states. A tabela pode não existir ainda: {}", e.getMessage());
                return false; // Agora temos o método verifyAndCreateLocationTables para criar as tabelas
            }
            
            String statesSqlPath = getSqlFilePath("states.sql");
            if (statesSqlPath == null) {
                log.warn("Arquivo states.sql não encontrado. Pulando importação SQL de estados.");
                return false;
            }
            
            log.info("Iniciando importação de estados do SQL...");
            File statesSqlFile = new File(statesSqlPath);
            
            try (BufferedReader reader = new BufferedReader(new FileReader(statesSqlFile))) {
                String line;
                StringBuilder sqlCommand = new StringBuilder();
                
                while ((line = reader.readLine()) != null) {
                    // Ignora linhas de comentário e diretivas específicas do MySQL
                    if (line.startsWith("--") || line.startsWith("/*") || line.startsWith("/*!") || line.trim().isEmpty()) {
                        continue;
                    }
                    
                    sqlCommand.append(line);
                    
                    // Se a linha termina com ponto e vírgula, é o fim de um comando SQL
                    if (line.trim().endsWith(";")) {
                        String sql = sqlCommand.toString();
                        try {
                            jdbcTemplate.execute(sql);
                        } catch (Exception e) {
                            log.warn("Erro ao executar comando SQL: {}", sql, e);
                        }
                        sqlCommand = new StringBuilder();
                    }
                }
                
                log.info("Importação de estados do SQL concluída com sucesso.");
                return true;
            } catch (Exception e) {
                log.error("Erro ao importar estados do SQL", e);
                return false;
            }
        } catch (Exception e) {
            log.error("Erro geral durante a importação de estados do SQL", e);
            return false;
        }
    }
    
    /**
     * Importa cidades do arquivo SQL
     */
    private boolean importCitiesFromSQL() {
        try {
            log.info("Verificando se é necessário importar cidades do SQL...");
            
            // Verificar se já existem cidades no banco de dados
            try {
                int cityCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM cities", Integer.class);
                if (cityCount > 0) {
                    log.info("Cidades já existem no banco de dados. Pulando importação SQL.");
                    return true;
                }
            } catch (Exception e) {
                log.warn("Erro ao verificar tabela cities. A tabela pode não existir ainda: {}", e.getMessage());
                log.info("Tentando criar a tabela cities através do Flyway...");
                
                // Tentar executar novamente a migração do Flyway para criar a tabela
                try {
                    // Executar o script SQL de criação da tabela cities diretamente
                    jdbcTemplate.execute(
                        "CREATE TABLE IF NOT EXISTS cities (" +
                        "  id bigint NOT NULL AUTO_INCREMENT," +
                        "  name varchar(255) NOT NULL," +
                        "  state_id bigint NOT NULL," +
                        "  state_code varchar(255) DEFAULT NULL," +
                        "  country_id bigint NOT NULL," +
                        "  country_code varchar(2) DEFAULT NULL," +
                        "  latitude decimal(10,8) DEFAULT NULL," +
                        "  longitude decimal(11,8) DEFAULT NULL," +
                        "  created_at datetime DEFAULT NULL," +
                        "  updated_at datetime DEFAULT NULL," +
                        "  flag bit(1) DEFAULT NULL," +
                        "  wikiDataId varchar(255) DEFAULT NULL," +
                        "  PRIMARY KEY (id)," +
                        "  CONSTRAINT FK_cities_state_id FOREIGN KEY (state_id) REFERENCES states (id)," +
                        "  CONSTRAINT FK_cities_country_id FOREIGN KEY (country_id) REFERENCES countries (id)" +
                        ")"
                    );
                    log.info("Tabela cities criada com sucesso.");
                } catch (Exception ex) {
                    log.error("Erro ao criar tabela cities manualmente: {}", ex.getMessage());
                    // Se não conseguir criar a tabela, retornar sem tentar importar
                    return false;
                }
            }
            
            String citiesSqlPath = getSqlFilePath("cities.sql");
            if (citiesSqlPath == null) {
                log.warn("Arquivo cities.sql não encontrado. Pulando importação SQL de cidades.");
                return false;
            }
            
            log.info("Iniciando importação de cidades do SQL...");
            File citiesSqlFile = new File(citiesSqlPath);
            
            try (BufferedReader reader = new BufferedReader(new FileReader(citiesSqlFile))) {
                String line;
                StringBuilder sqlCommand = new StringBuilder();
                
                while ((line = reader.readLine()) != null) {
                    // Ignora linhas de comentário e diretivas específicas do MySQL
                    if (line.startsWith("--") || line.startsWith("/*") || line.startsWith("/*!") || line.trim().isEmpty()) {
                        continue;
                    }
                    
                    sqlCommand.append(line);
                    
                    // Se a linha termina com ponto e vírgula, é o fim de um comando SQL
                    if (line.trim().endsWith(";")) {
                        String sql = sqlCommand.toString();
                        try {
                            jdbcTemplate.execute(sql);
                        } catch (Exception e) {
                            log.warn("Erro ao executar comando SQL: {}", sql, e);
                        }
                        sqlCommand = new StringBuilder();
                    }
                }
                
                log.info("Importação de cidades do SQL concluída com sucesso.");
                return true;
            } catch (Exception e) {
                log.error("Erro ao importar cidades do SQL", e);
                return false;
            }
        } catch (Exception e) {
            log.error("Erro geral durante a importação de cidades do SQL", e);
            return false;
        }
    }

    private String getSqlFilePath(String filename) {
        List<String> fileSystemLocations = Arrays.asList(
            System.getProperty("user.dir") + "/" + filename,                 // Na raiz do projeto
            System.getProperty("user.dir") + "/sql/" + filename,             // Na pasta sql do projeto
            System.getProperty("user.dir") + "/app/sql/" + filename,         // Na pasta app/sql do projeto
            System.getProperty("user.home") + "/sql/" + filename,            // Na pasta sql da home do usuário
            "/app/" + filename,                                              // Na pasta app (Docker)
            "/app/sql/" + filename,                                          // Na pasta app/sql (Docker)
            "/app/resources/sql/" + filename                                 // Na pasta app/resources/sql (Docker)
        );
        
        for (String location : fileSystemLocations) {
            try {
                File file = new File(location);
                if (file.exists()) {
                    log.info("Recurso SQL encontrado no sistema de arquivos: {}", location);
                    return location;
                }
            } catch (Exception e) {
                // Ignora e tenta a próxima localização
            }
        }
        
        log.warn("Recurso SQL '{}' não encontrado em nenhuma localização.", filename);
        return null;
    }

    /**
     * Cria as tabelas de localização no banco de dados se elas não existirem
     */
    public void createLocationTables() {
        log.info("Verificando existência das tabelas de localização...");
        
        try {
            // Verificar e criar tabela de países
            try {
                jdbcTemplate.queryForObject("SELECT COUNT(*) FROM countries", Integer.class);
                log.info("Tabela 'countries' já existe.");
            } catch (Exception e) {
                log.info("Tabela 'countries' não encontrada. Criando...");
                try {
                    String createCountriesTable = 
                        "CREATE TABLE IF NOT EXISTS countries (" +
                        "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                        "name VARCHAR(255) NOT NULL, " +
                        "iso3 VARCHAR(3), " +
                        "iso2 VARCHAR(2), " +
                        "numeric_code VARCHAR(10), " +
                        "phone_code VARCHAR(255), " +
                        "capital VARCHAR(255), " +
                        "currency VARCHAR(255), " +
                        "currency_name VARCHAR(255), " +
                        "currency_symbol VARCHAR(255), " +
                        "tld VARCHAR(255), " +
                        "native_name VARCHAR(255), " +
                        "region VARCHAR(255), " +
                        "subregion VARCHAR(255), " +
                        "nationality VARCHAR(255), " +
                        "timezones TEXT, " +
                        "translations TEXT, " +
                        "latitude VARCHAR(50), " +
                        "longitude VARCHAR(50), " +
                        "emoji VARCHAR(255), " +
                        "emojiU VARCHAR(255), " +
                        "created_at DATETIME, " +
                        "updated_at DATETIME, " +
                        "flag TINYINT(1), " +
                        "wikiDataId VARCHAR(255)" +
                        ")";
                    jdbcTemplate.execute(createCountriesTable);
                    log.info("Tabela 'countries' criada com sucesso.");
                } catch (Exception ex) {
                    log.error("Erro ao criar tabela 'countries': {}", ex.getMessage(), ex);
                    throw ex;
                }
            }
            
            // Verificar e criar tabela de estados
            try {
                jdbcTemplate.queryForObject("SELECT COUNT(*) FROM states", Integer.class);
                log.info("Tabela 'states' já existe.");
            } catch (Exception e) {
                log.info("Tabela 'states' não encontrada. Criando...");
                try {
                    String createStatesTable = 
                        "CREATE TABLE IF NOT EXISTS states (" +
                        "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                        "name VARCHAR(255) NOT NULL, " +
                        "country_id BIGINT NOT NULL, " +
                        "country_code VARCHAR(2), " +
                        "fips_code VARCHAR(255), " +
                        "iso2 VARCHAR(255), " +
                        "state_code VARCHAR(255), " +
                        "type VARCHAR(255), " +
                        "latitude VARCHAR(50), " +
                        "longitude VARCHAR(50), " +
                        "created_at DATETIME, " +
                        "updated_at DATETIME, " +
                        "flag TINYINT(1), " +
                        "wikiDataId VARCHAR(255), " +
                        "FOREIGN KEY (country_id) REFERENCES countries(id)" +
                        ")";
                    jdbcTemplate.execute(createStatesTable);
                    log.info("Tabela 'states' criada com sucesso.");
                } catch (Exception ex) {
                    log.error("Erro ao criar tabela 'states': {}", ex.getMessage(), ex);
                    throw ex;
                }
            }
            
            // Verificar e criar tabela de cidades
            try {
                jdbcTemplate.queryForObject("SELECT COUNT(*) FROM cities", Integer.class);
                log.info("Tabela 'cities' já existe.");
            } catch (Exception e) {
                log.info("Tabela 'cities' não encontrada. Criando...");
                try {
                    String createCitiesTable = 
                        "CREATE TABLE IF NOT EXISTS cities (" +
                        "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                        "name VARCHAR(255) NOT NULL, " +
                        "state_id BIGINT NOT NULL, " +
                        "state_code VARCHAR(255), " +
                        "country_id BIGINT NOT NULL, " +
                        "country_code VARCHAR(2), " +
                        "latitude VARCHAR(50), " +
                        "longitude VARCHAR(50), " +
                        "created_at DATETIME, " +
                        "updated_at DATETIME, " +
                        "flag TINYINT(1), " +
                        "wikiDataId VARCHAR(255), " +
                        "FOREIGN KEY (state_id) REFERENCES states(id), " +
                        "FOREIGN KEY (country_id) REFERENCES countries(id)" +
                        ")";
                    jdbcTemplate.execute(createCitiesTable);
                    log.info("Tabela 'cities' criada com sucesso.");
                } catch (Exception ex) {
                    log.error("Erro ao criar tabela 'cities': {}", ex.getMessage(), ex);
                    throw ex;
                }
            }
            
            log.info("Verificação e criação das tabelas de localização concluídas com sucesso.");
        } catch (Exception e) {
            log.error("Erro ao verificar/criar tabelas de localização: {}", e.getMessage(), e);
            throw e;
        }
    }
}
