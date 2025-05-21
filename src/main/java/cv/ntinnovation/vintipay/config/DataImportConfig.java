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
    @Bean
    public CommandLineRunner importLocationData() {
        return args -> {
            // Verificar se já existem dados nas tabelas
            if (isDataAlreadyImported()) {
                log.info("Dados de localização já foram importados anteriormente. Pulando importação.");
                return;
            }

            importLocationDataFromFiles();
        };
    }

    /**
     * Verifica se os dados de localização já foram importados
     */
    private boolean isDataAlreadyImported() {
        try {
            Integer countCountries = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM countries", Integer.class);
            return countCountries != null && countCountries > 0;
        } catch (Exception e) {
            // Se a tabela não existir ainda, retorna false
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
            boolean countriesImported = importCountriesFromSQL("countries.sql");
            boolean statesImported = importStatesFromSQL("states.sql");
            boolean citiesImported = importCitiesFromSQL("cities.sql");
            
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
    private boolean importCountriesFromSQL(String filename) {
        try (InputStream is = getResourceAsStream(filename)) {
            if (is == null) {
                log.warn("Arquivo SQL de países não encontrado. Pulando importação de países.");
                return false;
            }
            
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                String sql = reader.lines().collect(Collectors.joining("\n"));
                if (!sql.trim().isEmpty()) {
                    jdbcTemplate.execute(sql);
                    log.info("Importação de países do arquivo SQL concluída com sucesso.");
                    return true;
                } else {
                    log.warn("Arquivo SQL de países está vazio.");
                    return false;
                }
            }
        } catch (Exception e) {
            log.error("Erro ao importar países do arquivo SQL", e);
            return false;
        }
    }
    
    /**
     * Importa estados do arquivo SQL
     */
    private boolean importStatesFromSQL(String filename) {
        try (InputStream is = getResourceAsStream(filename)) {
            if (is == null) {
                log.warn("Arquivo SQL de estados não encontrado. Pulando importação de estados.");
                return false;
            }
            
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                String sql = reader.lines().collect(Collectors.joining("\n"));
                if (!sql.trim().isEmpty()) {
                    jdbcTemplate.execute(sql);
                    log.info("Importação de estados do arquivo SQL concluída com sucesso.");
                    return true;
                } else {
                    log.warn("Arquivo SQL de estados está vazio.");
                    return false;
                }
            }
        } catch (Exception e) {
            log.error("Erro ao importar estados do arquivo SQL", e);
            return false;
        }
    }
    
    /**
     * Importa cidades do arquivo SQL
     */
    private boolean importCitiesFromSQL(String filename) {
        try (InputStream is = getResourceAsStream(filename)) {
            if (is == null) {
                log.warn("Arquivo SQL de cidades não encontrado. Pulando importação de cidades.");
                return false;
            }
            
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                String sql = reader.lines().collect(Collectors.joining("\n"));
                if (!sql.trim().isEmpty()) {
                    jdbcTemplate.execute(sql);
                    log.info("Importação de cidades do arquivo SQL concluída com sucesso.");
                    return true;
                } else {
                    log.warn("Arquivo SQL de cidades está vazio.");
                    return false;
                }
            }
        } catch (Exception e) {
            log.error("Erro ao importar cidades do arquivo SQL", e);
            return false;
        }
    }
}
