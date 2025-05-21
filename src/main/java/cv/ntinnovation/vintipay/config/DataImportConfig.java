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
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
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
     * Importa os dados de localização dos arquivos SQL
     */
    private void importLocationDataFromFiles() {
        log.info("Iniciando importação de dados de localização...");

        try {
            // Caminho para os arquivos SQL usando recursos da classpath
            // Primeiro tenta buscar do classpath
            try {
                Resource countriesResource = new ClassPathResource("sql/countries.sql");
                Resource statesResource = new ClassPathResource("sql/states.sql");
                Resource citiesResource = new ClassPathResource("sql/cities.sql");
                
                if (countriesResource.exists() && statesResource.exists() && citiesResource.exists()) {
                    // Importar países
                    importCountries(countriesResource.getFile().getAbsolutePath());
                    
                    // Importar estados
                    importStates(statesResource.getFile().getAbsolutePath());
                    
                    // Importar cidades (este processo pode demorar devido ao tamanho do arquivo)
                    importCities(citiesResource.getFile().getAbsolutePath());
                    
                    log.info("Importação de dados de localização concluída com sucesso!");
                    return;
                }
            } catch (Exception e) {
                log.warn("Não foi possível encontrar arquivos SQL no classpath, tentando caminho relativo...");
            }
            
            // Se não encontrou no classpath, tenta utilizar caminhos relativos
            String basePath = System.getProperty("user.dir") + "/sql";
            log.info("Tentando importar arquivos SQL de: {}", basePath);
            
            // Verificar se os arquivos existem
            File countriesFile = new File(basePath + "/countries.sql");
            File statesFile = new File(basePath + "/states.sql");
            File citiesFile = new File(basePath + "/cities.sql");
            
            if (!countriesFile.exists() || !statesFile.exists() || !citiesFile.exists()) {
                log.error("Arquivos SQL de localização não encontrados em {}", basePath);
                log.error("Verifique se os arquivos countries.sql, states.sql e cities.sql existem neste diretório.");
                return;
            }
            
            // Importar países
            importCountries(countriesFile.getAbsolutePath());
            
            // Importar estados
            importStates(statesFile.getAbsolutePath());
            
            // Importar cidades (este processo pode demorar devido ao tamanho do arquivo)
            importCities(citiesFile.getAbsolutePath());
            
            log.info("Importação de dados de localização concluída com sucesso!");
        } catch (Exception e) {
            log.error("Erro durante a importação de dados de localização", e);
        }
    }
    
    private void importCountries(String filePath) {
        log.info("Importando países de {}", filePath);
        
        try {
            // Verificar se o arquivo existe
            File file = new File(filePath);
            if (!file.exists()) {
                log.error("Arquivo de países não encontrado: {}", filePath);
                return;
            }
            
            // Ler o arquivo e extrair os comandos INSERT
            List<String> insertStatements = extractInsertStatements(file);
            
            // Executar os comandos INSERT
            for (String insert : insertStatements) {
                try {
                    jdbcTemplate.execute(insert);
                } catch (Exception e) {
                    log.error("Erro ao executar INSERT de país: {}", e.getMessage());
                }
            }
            
            log.info("Importação de países concluída. Total de registros: {}", insertStatements.size());
        } catch (Exception e) {
            log.error("Erro durante a importação de países", e);
        }
    }
    
    private void importStates(String filePath) {
        log.info("Importando estados de {}", filePath);
        
        try {
            // Verificar se o arquivo existe
            File file = new File(filePath);
            if (!file.exists()) {
                log.error("Arquivo de estados não encontrado: {}", filePath);
                return;
            }
            
            // Ler o arquivo e extrair os comandos INSERT
            List<String> insertStatements = extractInsertStatements(file);
            
            // Executar os comandos INSERT
            for (String insert : insertStatements) {
                try {
                    jdbcTemplate.execute(insert);
                } catch (Exception e) {
                    log.error("Erro ao executar INSERT de estado: {}", e.getMessage());
                }
            }
            
            log.info("Importação de estados concluída. Total de registros: {}", insertStatements.size());
        } catch (Exception e) {
            log.error("Erro durante a importação de estados", e);
        }
    }
    
    private void importCities(String filePath) {
        log.info("Importando cidades de {}", filePath);
        
        try {
            // Verificar se o arquivo existe
            File file = new File(filePath);
            if (!file.exists()) {
                log.error("Arquivo de cidades não encontrado: {}", filePath);
                return;
            }
            
            // Como o arquivo de cidades é muito grande, vamos processar em lotes
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                StringBuilder insertBuilder = new StringBuilder();
                String line;
                int count = 0;
                boolean insideInsertBlock = false;
                
                while ((line = reader.readLine()) != null) {
                    // Identificar o início de um bloco INSERT
                    if (line.startsWith("INSERT INTO `cities`")) {
                        insideInsertBlock = true;
                        insertBuilder = new StringBuilder(line);
                    } 
                    // Continuar acumulando a instrução INSERT
                    else if (insideInsertBlock && !line.contains("/*!40000 ALTER TABLE")) {
                        insertBuilder.append(" ").append(line);
                    } 
                    // Finalizar e executar a instrução INSERT quando encontrar o fim do bloco
                    else if (insideInsertBlock) {
                        insideInsertBlock = false;
                        try {
                            String insert = insertBuilder.toString();
                            jdbcTemplate.execute(insert);
                            count++;
                            
                            if (count % 100 == 0) {
                                log.info("Importadas {} cidades até o momento", count);
                            }
                        } catch (Exception e) {
                            log.error("Erro ao executar INSERT de cidade: {}", e.getMessage());
                        }
                    }
                }
                
                log.info("Importação de cidades concluída. Total de lotes processados: {}", count);
            }
        } catch (Exception e) {
            log.error("Erro durante a importação de cidades", e);
        }
    }
    
    private List<String> extractInsertStatements(File file) throws Exception {
        return Files.lines(file.toPath())
                .filter(line -> line.startsWith("INSERT INTO"))
                .map(line -> {
                    // Remover os backticks dos nomes das tabelas para compatibilidade com MySQL
                    // No MySQL, podemos manter os backticks
                    return line;
                })
                .collect(Collectors.toList());
    }
}
