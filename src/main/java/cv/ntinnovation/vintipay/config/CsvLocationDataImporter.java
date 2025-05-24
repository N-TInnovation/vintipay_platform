package cv.ntinnovation.vintipay.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;

import javax.sql.DataSource;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Importador de dados geográficos a partir de arquivos CSV.
 * Esta classe é executada após as migrações do Flyway para garantir que
 * todos os dados necessários sejam carregados.
 */
@Configuration
@Order(1)
public class CsvLocationDataImporter implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(CsvLocationDataImporter.class);
    private static final int BATCH_SIZE = 500; // Reduzido para evitar problemas de memória

    private final JdbcTemplate jdbcTemplate;
    private final DataSource dataSource;
    private final ResourceLoader resourceLoader;

    @Autowired
    public CsvLocationDataImporter(JdbcTemplate jdbcTemplate, DataSource dataSource, ResourceLoader resourceLoader) {
        this.jdbcTemplate = jdbcTemplate;
        this.dataSource = dataSource;
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void run(ApplicationArguments args) {
        // O método run é chamado muito cedo no ciclo de vida, pode ser que as tabelas ainda não existam
        // Vamos usar um event listener para garantir que a aplicação esteja completamente inicializada
        logger.info("CsvLocationDataImporter inicializado com sucesso, aguardando evento ApplicationReady...");
    }
    
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        logger.info("Evento ApplicationReady recebido, iniciando importação de dados geográficos...");
        importLocationData();
    }

    /**
     * Método principal para importação de dados
     */
    private void importLocationData() {
        try {
            // Verificar se o banco de dados já contém dados
            if (isDatabasePopulated()) {
                logger.info("Banco de dados já está populado com dados geográficos. Pulando importação.");
                return;
            }
            
            // Verifica se as tabelas existem antes de prosseguir
            if (!tablesExist()) {
                logger.error("As tabelas de localização não existem no banco de dados. Execute as migrações Flyway primeiro.");
                return;
            }

            // Primeiro vamos obter os dados dos arquivos CSV para verificar integridade
            String countriesCsv = getResourceAsString("csv/countries.csv");
            String statesCsv = getResourceAsString("csv/states.csv");
            String citiesCsv = getResourceAsString("csv/cities.csv");
            
            if (countriesCsv == null || statesCsv == null || citiesCsv == null) {
                logger.error("Não foi possível carregar todos os arquivos CSV necessários.");
                return;
            }
            
            // Processar cada arquivo em sua própria transação
            try (Connection conn = dataSource.getConnection()) {
                // Desabilitar auto-commit
                conn.setAutoCommit(false);
                
                try {
                    // Preparar o banco para importação
                    clearExistingData(conn);
                    conn.commit();
                    logger.info("Dados anteriores removidos e alterações confirmadas.");
                    
                    // Importar países
                    importCountries(conn, countriesCsv);
                    conn.commit();
                    logger.info("Importação de países concluída e confirmada no banco.");
                    
                    // Importar estados
                    importStates(conn, statesCsv);
                    conn.commit();
                    logger.info("Importação de estados concluída e confirmada no banco.");
                    
                    // Importar cidades
                    importCities(conn, citiesCsv);
                    conn.commit();
                    logger.info("Importação de cidades concluída e confirmada no banco.");
                    
                    logger.info("Importação de dados geográficos concluída com sucesso!");
                } catch (Exception e) {
                    // Rollback em caso de erro
                    try {
                        conn.rollback();
                    } catch (SQLException rollbackEx) {
                        logger.error("Erro ao fazer rollback: {}", rollbackEx.getMessage());
                    }
                    throw e;
                }
            }
        } catch (Exception e) {
            logger.error("Erro ao importar dados geográficos: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Limpa os dados existentes nas tabelas
     */
    private void clearExistingData(Connection conn) throws SQLException {
        logger.info("Limpando dados existentes nas tabelas...");
        
        try (PreparedStatement stmt = conn.prepareStatement("SET FOREIGN_KEY_CHECKS = 0")) {
            stmt.execute();
        }
        
        try (PreparedStatement stmt = conn.prepareStatement("TRUNCATE TABLE cities")) {
            stmt.execute();
        }
        
        try (PreparedStatement stmt = conn.prepareStatement("TRUNCATE TABLE states")) {
            stmt.execute();
        }
        
        try (PreparedStatement stmt = conn.prepareStatement("TRUNCATE TABLE countries")) {
            stmt.execute();
        }
        
        try (PreparedStatement stmt = conn.prepareStatement("SET FOREIGN_KEY_CHECKS = 1")) {
            stmt.execute();
        }
        
        logger.info("Tabelas limpas com sucesso.");
    }
    
    /**
     * Verifica se as tabelas necessárias existem no banco de dados
     */
    private boolean tablesExist() {
        try {
            // Verifica a existência das tabelas no schema do banco de dados
            // em vez de tentar buscar dados delas
            List<String> tables = jdbcTemplate.queryForList(
                "SELECT table_name FROM information_schema.tables " +
                "WHERE table_schema = (SELECT database()) " +
                "AND table_name IN ('countries', 'states', 'cities')",
                String.class
            );
            
            boolean hasAllTables = tables.size() == 3;
            if (!hasAllTables) {
                logger.warn("Nem todas as tabelas necessárias existem. Encontradas: {}", tables);
            }
            return hasAllTables;
        } catch (Exception e) {
            logger.error("Erro ao verificar existência das tabelas: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Importa todos os países do arquivo CSV
     */
    private void importCountries(Connection conn, String csvContent) throws IOException, SQLException {
        logger.info("Importando países...");
        if (csvContent == null) {
            logger.error("Conteúdo do arquivo countries.csv é nulo");
            return;
        }
        
        String[] lines = csvContent.split("\n");
        if (lines.length <= 1) {
            logger.warn("Arquivo countries.csv vazio ou só com cabeçalho");
            return;
        }
        
        String sql = "INSERT INTO countries (id, name, iso3, iso2, numeric_code, phonecode, " +
                "capital, currency, currency_name, currency_symbol, tld, native_name, " +
                "region, subregion, latitude, longitude, emoji, emojiU, flag) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 1)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            int count = 0;
            int processed = 0;
            
            // Pular cabeçalho
            for (int i = 1; i < lines.length; i++) {
                String line = lines[i].trim();
                if (line.isEmpty()) continue;
                
                String[] fields = parseCsvLine(line);
                if (fields.length < 22) {
                    logger.warn("Linha com formato inválido (país): {}", line);
                    continue;
                }
                
                processed++;
                try {
                    stmt.setLong(1, parseLong(fields[0])); // id
                    stmt.setString(2, fields[1]); // name
                    stmt.setString(3, fields[2]); // iso3
                    stmt.setString(4, fields[3]); // iso2
                    stmt.setString(5, fields[4]); // numeric_code
                    stmt.setString(6, fields[5]); // phonecode
                    stmt.setString(7, fields[6]); // capital
                    stmt.setString(8, fields[7]); // currency
                    stmt.setString(9, fields[8]); // currency_name
                    stmt.setString(10, fields[9]); // currency_symbol
                    stmt.setString(11, fields[10]); // tld
                    stmt.setString(12, fields[11]); // native_name
                    stmt.setString(13, fields[12]); // region
                    stmt.setString(14, fields[14]); // subregion
                    stmt.setString(15, fields[18]); // latitude
                    stmt.setString(16, fields[19]); // longitude
                    stmt.setString(17, fields[20]); // emoji
                    stmt.setString(18, fields[21]); // emojiU
                    
                    stmt.addBatch();
                    count++;
                    
                    if (count % BATCH_SIZE == 0) {
                        executeAndLogBatch(stmt, "países", count);
                    }
                } catch (Exception e) {
                    logger.error("Erro ao processar país (ID: {}): {}", fields[0], e.getMessage());
                }
            }
            
            if (count % BATCH_SIZE != 0) {
                executeAndLogBatch(stmt, "países", count);
            }
            
            logger.info("Importação de países concluída. Total: {} processados, {} importados com sucesso", 
                    processed, count);
        }
    }
    
    /**
     * Importa todos os estados do arquivo CSV
     */
    private void importStates(Connection conn, String csvContent) throws IOException, SQLException {
        logger.info("Importando estados...");
        if (csvContent == null) {
            logger.error("Conteúdo do arquivo states.csv é nulo");
            return;
        }
        
        String[] lines = csvContent.split("\n");
        if (lines.length <= 1) {
            logger.warn("Arquivo states.csv vazio ou só com cabeçalho");
            return;
        }
        
        // Primeiro, vamos obter a lista de países para verificar referências
        List<Long> existingCountryIds = getExistingCountryIds(conn);
        logger.info("Encontrados {} países no banco para validação de referências", existingCountryIds.size());
        
        String sql = "INSERT INTO states (id, name, country_id, country_code, iso2, type, " +
                "latitude, longitude, flag) VALUES (?, ?, ?, ?, ?, ?, ?, ?, 1)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            int count = 0;
            int processed = 0;
            int skipped = 0;
            
            // Pular cabeçalho
            for (int i = 1; i < lines.length; i++) {
                String line = lines[i].trim();
                if (line.isEmpty()) continue;
                
                String[] fields = parseCsvLine(line);
                if (fields.length < 8) {
                    logger.warn("Linha com formato inválido (estado): {}", line);
                    continue;
                }
                
                processed++;
                try {
                    long countryId = parseLong(fields[2]);
                    
                    // Verificar se o país existe
                    if (!existingCountryIds.contains(countryId)) {
                        logger.warn("Estado {} (ID: {}) referencia um país inexistente (ID: {}). Ignorando.", 
                                fields[1], fields[0], countryId);
                        skipped++;
                        continue;
                    }
                    
                    stmt.setLong(1, parseLong(fields[0])); // id
                    stmt.setString(2, fields[1]); // name
                    stmt.setLong(3, countryId); // country_id
                    stmt.setString(4, fields[3]); // country_code
                    stmt.setString(5, fields[4]); // state_code/iso2
                    stmt.setString(6, fields[5]); // type
                    stmt.setString(7, fields[6]); // latitude
                    stmt.setString(8, fields[7]); // longitude
                    
                    stmt.addBatch();
                    count++;
                    
                    if (count % BATCH_SIZE == 0) {
                        executeAndLogBatch(stmt, "estados", count);
                    }
                } catch (Exception e) {
                    logger.error("Erro ao processar estado (ID: {}): {}", fields[0], e.getMessage());
                }
            }
            
            if (count % BATCH_SIZE != 0) {
                executeAndLogBatch(stmt, "estados", count);
            }
            
            logger.info("Importação de estados concluída. Total: {} processados, {} importados, {} ignorados", 
                    processed, count, skipped);
        }
    }
    
    /**
     * Importa todas as cidades do arquivo CSV
     */
    private void importCities(Connection conn, String csvContent) throws IOException, SQLException {
        logger.info("Importando cidades...");
        if (csvContent == null) {
            logger.error("Conteúdo do arquivo cities.csv é nulo");
            return;
        }
        
        String[] lines = csvContent.split("\n");
        if (lines.length <= 1) {
            logger.warn("Arquivo cities.csv vazio ou só com cabeçalho");
            return;
        }
        
        // Obter listas de IDs existentes para validação
        List<Long> existingCountryIds = getExistingCountryIds(conn);
        List<Long> existingStateIds = getExistingStateIds(conn);
        
        logger.info("Encontrados {} países e {} estados no banco para validação de referências", 
                existingCountryIds.size(), existingStateIds.size());
        
        String sql = "INSERT INTO cities (id, name, state_id, state_code, country_id, " +
                "country_code, latitude, longitude, flag) VALUES (?, ?, ?, ?, ?, ?, ?, ?, 1)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            int count = 0;
            int processed = 0;
            int skipped = 0;
            int totalLines = lines.length - 1; // menos o cabeçalho
            
            // Pular cabeçalho
            for (int i = 1; i < lines.length; i++) {
                String line = lines[i].trim();
                if (line.isEmpty()) continue;
                
                String[] fields = parseCsvLine(line);
                if (fields.length < 8) {
                    logger.warn("Linha com formato inválido (cidade): {}", line);
                    continue;
                }
                
                processed++;
                try {
                    long stateId = parseLong(fields[2]);
                    long countryId = parseLong(fields[4]);
                    
                    // Verificar se o país e o estado existem
                    boolean countryExists = existingCountryIds.contains(countryId);
                    boolean stateExists = existingStateIds.contains(stateId);
                    
                    if (!countryExists || !stateExists) {
                        if (processed % 10000 == 0) { // Log apenas a cada 10.000 para não sobrecarregar
                            logger.warn("Cidade {} (ID: {}) referencia país/estado inexistente. " +
                                        "País: {} (existe: {}), Estado: {} (existe: {}). Ignorando.",
                                    fields[1], fields[0], countryId, countryExists, stateId, stateExists);
                        }
                        skipped++;
                        continue;
                    }
                    
                    stmt.setLong(1, parseLong(fields[0])); // id
                    stmt.setString(2, fields[1]); // name
                    stmt.setLong(3, stateId); // state_id
                    stmt.setString(4, fields[3]); // state_code
                    stmt.setLong(5, countryId); // country_id
                    stmt.setString(6, fields[5]); // country_code
                    stmt.setString(7, fields[6]); // latitude
                    stmt.setString(8, fields[7]); // longitude
                    
                    stmt.addBatch();
                    count++;
                    
                    if (count % BATCH_SIZE == 0) {
                        executeAndLogBatch(stmt, "cidades", count);
                        
                        if (count % (BATCH_SIZE * 5) == 0) {
                            logger.info("Importadas {} cidades de {} ({:.2f}%)...", 
                                    count, totalLines, (count * 100.0 / totalLines));
                        }
                    }
                } catch (Exception e) {
                    if (processed % 10000 == 0) { // Log apenas a cada 10.000 para não sobrecarregar
                        logger.error("Erro ao processar cidade (ID: {}): {}", fields[0], e.getMessage());
                    }
                }
            }
            
            if (count % BATCH_SIZE != 0) {
                executeAndLogBatch(stmt, "cidades", count);
            }
            
            logger.info("Importação de cidades concluída. Total: {} processados, {} importados, {} ignorados", 
                    processed, count, skipped);
        }
    }
    
    /**
     * Executa um batch e faz log dos resultados
     */
    private void executeAndLogBatch(PreparedStatement stmt, String entityType, int count) {
        try {
            int[] results = stmt.executeBatch();
            int successCount = 0;
            for (int result : results) {
                if (result >= 0) {
                    successCount++;
                }
            }
            logger.debug("Batch de {} executado: {}/{} registros processados com sucesso", 
                    entityType, successCount, results.length);
        } catch (SQLException e) {
            logger.error("Erro ao executar batch de {}: {}", entityType, e.getMessage());
            throw new RuntimeException("Erro ao executar batch de " + entityType, e);
        }
    }
    
    /**
     * Obtém a lista de IDs de países existentes no banco
     */
    private List<Long> getExistingCountryIds(Connection conn) throws SQLException {
        List<Long> result = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement("SELECT id FROM countries");
             var rs = stmt.executeQuery()) {
            while (rs.next()) {
                result.add(rs.getLong("id"));
            }
        }
        return result;
    }
    
    /**
     * Obtém a lista de IDs de estados existentes no banco
     */
    private List<Long> getExistingStateIds(Connection conn) throws SQLException {
        List<Long> result = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement("SELECT id FROM states");
             var rs = stmt.executeQuery()) {
            while (rs.next()) {
                result.add(rs.getLong("id"));
            }
        }
        return result;
    }
    
    /**
     * Verifica se o banco de dados já contém dados geográficos
     */
    private boolean isDatabasePopulated() {
        try {
            Integer countCountries = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM countries", Integer.class);
            
            // Se tiver pelo menos 50 países, consideramos que os dados já estão importados
            boolean isPopulated = (countCountries != null && countCountries > 50);
            logger.info("Verificação de banco populado: {} países encontrados. Considerado populado: {}", 
                    countCountries, isPopulated);
            return isPopulated;
        } catch (Exception e) {
            logger.error("Erro ao verificar se banco de dados está populado: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Obtém o conteúdo de um recurso como string
     */
    private String getResourceAsString(String resourcePath) {
        logger.info("Tentando carregar recurso: {}", resourcePath);
        try {
            // Primeiro tenta do classpath com ClassPathResource
            Resource resource = new ClassPathResource(resourcePath);
            if (resource.exists()) {
                logger.info("Recurso encontrado no classpath: {}", resourcePath);
                return readResourceAsString(resource);
            }
            
            // Depois tenta com ResourceLoader
            resource = resourceLoader.getResource("classpath:" + resourcePath);
            if (resource.exists()) {
                logger.info("Recurso encontrado via ResourceLoader: {}", resourcePath);
                return readResourceAsString(resource);
            }
            
            // Tenta buscar no diretório do projeto
            String filePath = System.getProperty("user.dir") + "/src/main/resources/" + resourcePath;
            File file = new File(filePath);
            if (file.exists()) {
                logger.info("Recurso encontrado no sistema de arquivos: {}", filePath);
                return new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
            }
            
            // Última tentativa: diretamente no /src/main/resources
            filePath = System.getProperty("user.dir") + "/src/main/resources/csv/" + resourcePath.substring(resourcePath.lastIndexOf('/') + 1);
            file = new File(filePath);
            if (file.exists()) {
                logger.info("Recurso encontrado em caminho alternativo: {}", filePath);
                return new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
            }
            
            logger.error("Recurso não encontrado após várias tentativas: {}", resourcePath);
            return null;
        } catch (Exception e) {
            logger.error("Erro ao ler recurso {}: {}", resourcePath, e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Lê um recurso Spring como string
     */
    private String readResourceAsString(Resource resource) throws IOException {
        try (InputStream inputStream = resource.getInputStream()) {
            return new String(FileCopyUtils.copyToByteArray(inputStream), StandardCharsets.UTF_8);
        }
    }
    
    /**
     * Faz o parsing de uma linha CSV considerando campos entre aspas
     */
    private String[] parseCsvLine(String line) {
        List<String> tokens = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                // Toggle the inQuotes flag
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                // If we reach a comma and we're not in quotes, add the current token and reset
                tokens.add(sb.toString());
                sb = new StringBuilder();
            } else {
                // Otherwise, append the current character to the token
                sb.append(c);
            }
        }
        
        // Add the last token
        tokens.add(sb.toString());
        
        return tokens.toArray(new String[0]);
    }
    
    /**
     * Converte string para long com tratamento de erros
     */
    private long parseLong(String value) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
