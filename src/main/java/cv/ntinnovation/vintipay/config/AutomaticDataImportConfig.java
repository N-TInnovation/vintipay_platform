package cv.ntinnovation.vintipay.config;

import com.opencsv.exceptions.CsvValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuração para importação automática de dados geográficos na inicialização
 * da aplicação usando o CsvDataImportService.
 */
@Component
public class AutomaticDataImportConfig implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger logger = LoggerFactory.getLogger(AutomaticDataImportConfig.class);
    private final Executor importExecutor = Executors.newFixedThreadPool(1);

    private final CsvDataImportService csvDataImportService;
    private final JdbcTemplate jdbcTemplate;
    private final Environment environment;

    @Value("${vintipay.auto-import.enabled:true}")
    private boolean autoImportEnabled;

    @Value("${vintipay.auto-import.skip-if-data-exists:true}")
    private boolean skipIfDataExists;

    @Autowired
    public AutomaticDataImportConfig(
            CsvDataImportService csvDataImportService,
            JdbcTemplate jdbcTemplate,
            Environment environment) {
        this.csvDataImportService = csvDataImportService;
        this.jdbcTemplate = jdbcTemplate;
        this.environment = environment;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        // Não executar a importação em testes ou se estiver desabilitada
        String[] activeProfiles = environment.getActiveProfiles();
        if (Arrays.asList(activeProfiles).contains("test") || !autoImportEnabled) {
            logger.info("Importação automática desabilitada em perfil de teste ou via configuração");
            return;
        }

        logger.info("Verificando necessidade de importação automática de dados geográficos...");

        try {
            // Verificar se as tabelas existem
            boolean tablesExist = checkTablesExist();
            if (!tablesExist) {
                logger.warn("Tabelas essenciais não existem. Pulando importação automática.");
                return;
            }

            // Verificar se já existem dados (se configurado para pular)
            if (skipIfDataExists && dataAlreadyExists()) {
                logger.info("Dados geográficos já existem. Pulando importação automática.");
                return;
            }

            // Iniciar a importação em background para não atrasar a inicialização
            CompletableFuture.runAsync(() -> {
                try {
                    // Primeiro importar regiões e sub-regiões que são pré-requisitos para países
                    logger.info("Iniciando importação automática de regiões...");
                    int regionsCount = csvDataImportService.importRegions();
                    logger.info("Importação de regiões concluída. {} registros importados.", regionsCount);

                    logger.info("Iniciando importação automática de sub-regiões...");
                    int subRegionsCount = csvDataImportService.importSubRegions();
                    logger.info("Importação de sub-regiões concluída. {} registros importados.", subRegionsCount);
                    
                    // Depois importar países, estados e cidades na ordem correta
                    logger.info("Iniciando importação automática de países...");
                    int countriesCount = csvDataImportService.importCountries();
                    logger.info("Importação de países concluída. {} registros importados.", countriesCount);

                    logger.info("Iniciando importação automática de estados...");
                    int statesCount = csvDataImportService.importStates();
                    logger.info("Importação de estados concluída. {} registros importados.", statesCount);

                    logger.info("Iniciando importação automática de cidades...");
                    int citiesCount = csvDataImportService.importCities();
                    logger.info("Importação de cidades concluída. {} registros importados.", citiesCount);
                    
                    // Por último, importar as localidades de Cabo Verde
                    logger.info("Iniciando importação automática de localidades de Cabo Verde...");
                    int cvLocationsCount = csvDataImportService.importCapeVerdeLocations();
                    logger.info("Importação de localidades de Cabo Verde concluída. {} registros importados.", cvLocationsCount);

                    logger.info("Importação automática finalizada com sucesso!");
                } catch (Exception e) {
                    logger.error("Erro durante importação automática: {}", e.getMessage(), e);
                }
            }, importExecutor);

        } catch (Exception e) {
            logger.error("Erro ao verificar necessidade de importação automática: {}", e.getMessage(), e);
        }
    }

    /**
     * Verifica se as tabelas necessárias existem
     */
    private boolean checkTablesExist() {
        try {
            Map<String, Boolean> tableExistence = new HashMap<>();
            String[] tables = {"regions", "subregions", "countries", "states", "cities", "cv_locations"};
            
            // Verificar a existência de cada tabela
            for (String table : tables) {
                try {
                    Integer count = jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM information_schema.tables " +
                        "WHERE table_schema = (SELECT database()) AND table_name = ?",
                        Integer.class, table);
                    
                    boolean exists = (count != null && count > 0);
                    tableExistence.put(table, exists);
                    
                    if (!exists) {
                        logger.warn("Tabela {} não existe no banco de dados", table);
                    }
                } catch (Exception e) {
                    tableExistence.put(table, false);
                    logger.error("Erro ao verificar existência da tabela {}: {}", table, e.getMessage());
                }
            }
            
            // Verificar se as tabelas essenciais existem
            boolean essentialTablesExist = tableExistence.getOrDefault("countries", false) && 
                                          tableExistence.getOrDefault("states", false) && 
                                          tableExistence.getOrDefault("cities", false);
                                          
            if (!essentialTablesExist) {
                logger.warn("Tabelas essenciais (countries, states, cities) não existem. Pulando importação.");
                return false;
            }
            
            return true;
        } catch (Exception e) {
            logger.error("Erro ao verificar existência das tabelas: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Verifica se já existem dados nas tabelas
     */
    private boolean dataAlreadyExists() {
        try {
            // Verificar apenas tabelas essenciais para decidir se importa ou não
            String[] essentialTables = {"countries", "states", "cities"};
            for (String table : essentialTables) {
                try {
                    Integer count = jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM " + table, Integer.class);
                    
                    if (count == null || count == 0) {
                        logger.info("Tabela {} está vazia", table);
                        return false;
                    }
                } catch (Exception e) {
                    logger.warn("Erro ao verificar dados na tabela {}: {}", table, e.getMessage());
                    // Se não conseguir verificar uma tabela, considera que precisa importar
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            logger.error("Erro ao verificar existência de dados: {}", e.getMessage(), e);
            // Em caso de erro, assume que precisa importar
            return false;
        }
    }
}
