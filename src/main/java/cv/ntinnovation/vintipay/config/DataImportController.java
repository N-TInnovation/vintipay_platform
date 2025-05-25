package cv.ntinnovation.vintipay.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Controller para diagnóstico e operações de importação de dados geográficos
 */
@RestController
@RequestMapping("/api/data-import")
public class DataImportController {

    private static final Logger logger = LoggerFactory.getLogger(DataImportController.class);
    private final Executor importExecutor = Executors.newFixedThreadPool(1);

    private final JdbcTemplate jdbcTemplate;
    private final CsvDataImportService csvDataImportService;

    @Autowired
    public DataImportController(JdbcTemplate jdbcTemplate, CsvDataImportService csvDataImportService) {
        this.jdbcTemplate = jdbcTemplate;
        this.csvDataImportService = csvDataImportService;
    }

    @GetMapping("/import-status")
    public ResponseEntity<Map<String, Object>> getImportStatus() {
        Map<String, Object> response = new HashMap<>();

        try {
            Map<String, Integer> tableCounts = new HashMap<>();

            String[] tables = {"regions", "subregions", "countries", "states", "cities", "cv_locations"};
            for (String table : tables) {
                try {
                    Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + table, Integer.class);
                    tableCounts.put(table, count);
                } catch (Exception e) {
                    tableCounts.put(table, -1); // Tabela não existe ou erro
                    logger.warn("Erro ao contar registros na tabela {}: {}", table, e.getMessage());
                }
            }

            response.put("tableCounts", tableCounts);
            response.put("status", "success");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Erro ao verificar status de importação: {}", e.getMessage(), e);
            response.put("status", "error");
            response.put("message", "Erro: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/import-countries")
    public ResponseEntity<Map<String, Object>> importCountries() {
        Map<String, Object> response = new HashMap<>();

        try {
            // Verificar se a tabela existe
            boolean tableExists = jdbcTemplate.queryForList(
                "SELECT table_name FROM information_schema.tables " +
                "WHERE table_schema = (SELECT database()) AND table_name = 'countries'",
                String.class
            ).size() > 0;

            response.put("tableExists", tableExists);

            if (!tableExists) {
                response.put("status", "error");
                response.put("message", "A tabela countries não existe no banco de dados.");
                return ResponseEntity.ok(response);
            }

            // Verificar se o arquivo existe
            String[] possiblePaths = {
                "src/main/resources/csv/countries.csv",
                "target/classes/csv/countries.csv"
            };

            boolean fileFound = false;
            String foundPath = null;
            long fileSize = 0;

            for (String pathStr : possiblePaths) {
                Path path = Paths.get(System.getProperty("user.dir"), pathStr);
                File file = path.toFile();
                if (file.exists()) {
                    fileFound = true;
                    foundPath = file.getAbsolutePath();
                    fileSize = file.length();
                    break;
                }
            }

            response.put("fileFound", fileFound);
            response.put("filePath", foundPath);
            response.put("fileSize", fileSize);

            if (!fileFound) {
                response.put("status", "error");
                response.put("message", "O arquivo de países não foi encontrado.");
                return ResponseEntity.ok(response);
            }

            // Contar registros antes da importação
            Integer beforeCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM countries", Integer.class);
            response.put("recordsBeforeImport", beforeCount);

            // Forçar a importação
            int importedCount = csvDataImportService.importCountries();

            // Contar registros após a importação
            Integer afterCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM countries", Integer.class);
            response.put("recordsAfterImport", afterCount);
            response.put("recordsImported", importedCount);

            // Listar alguns registros para verificação
            List<Map<String, Object>> sampleRecords = jdbcTemplate.queryForList(
                "SELECT * FROM countries LIMIT 5"
            );
            response.put("sampleRecords", sampleRecords);

            response.put("status", "success");
            response.put("message", "Importação de países executada com sucesso");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Erro ao importar países: {}", e.getMessage(), e);
            response.put("status", "error");
            response.put("message", "Erro ao importar: " + e.getMessage());
            response.put("errorDetails", e.toString());
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/import-states")
    public ResponseEntity<Map<String, Object>> importStates() {
        Map<String, Object> response = new HashMap<>();

        try {
            // Verificar se a tabela de países existe e tem dados
            Integer countryCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM countries", Integer.class);
            response.put("countriesCount", countryCount);

            if (countryCount == 0) {
                response.put("status", "error");
                response.put("message", "Não existem países no banco de dados. Importe os países primeiro.");
                return ResponseEntity.ok(response);
            }

            // Verificar se a tabela existe
            boolean tableExists = jdbcTemplate.queryForList(
                "SELECT table_name FROM information_schema.tables " +
                "WHERE table_schema = (SELECT database()) AND table_name = 'states'",
                String.class
            ).size() > 0;

            response.put("tableExists", tableExists);

            if (!tableExists) {
                response.put("status", "error");
                response.put("message", "A tabela states não existe no banco de dados.");
                return ResponseEntity.ok(response);
            }

            // Verificar se o arquivo existe
            String[] possiblePaths = {
                "src/main/resources/csv/states.csv",
                "target/classes/csv/states.csv"
            };

            boolean fileFound = false;
            String foundPath = null;
            long fileSize = 0;

            for (String pathStr : possiblePaths) {
                Path path = Paths.get(System.getProperty("user.dir"), pathStr);
                File file = path.toFile();
                if (file.exists()) {
                    fileFound = true;
                    foundPath = file.getAbsolutePath();
                    fileSize = file.length();
                    break;
                }
            }

            response.put("fileFound", fileFound);
            response.put("filePath", foundPath);
            response.put("fileSize", fileSize);

            if (!fileFound) {
                response.put("status", "error");
                response.put("message", "O arquivo de estados não foi encontrado.");
                return ResponseEntity.ok(response);
            }

            // Contar registros antes da importação
            Integer beforeCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM states", Integer.class);
            response.put("recordsBeforeImport", beforeCount);

            // Forçar a importação
            int importedCount = csvDataImportService.importStates();

            // Contar registros após a importação
            Integer afterCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM states", Integer.class);
            response.put("recordsAfterImport", afterCount);
            response.put("recordsImported", importedCount);

            // Listar alguns registros para verificação
            List<Map<String, Object>> sampleRecords = jdbcTemplate.queryForList(
                "SELECT * FROM states LIMIT 5"
            );
            response.put("sampleRecords", sampleRecords);

            response.put("status", "success");
            response.put("message", "Importação de estados executada com sucesso");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Erro ao importar estados: {}", e.getMessage(), e);
            response.put("status", "error");
            response.put("message", "Erro ao importar: " + e.getMessage());
            response.put("errorDetails", e.toString());
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/import-cities-info")
    public ResponseEntity<Map<String, Object>> importCitiesInfo() {
        Map<String, Object> response = new HashMap<>();

        try {
            // Verificar tabelas relacionadas
            Map<String, Integer> tableCounts = new HashMap<>();

            String[] tables = {"countries", "states", "cities"};
            for (String table : tables) {
                Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + table, Integer.class);
                tableCounts.put(table, count);
            }
            
            response.put("tableCounts", tableCounts);
            
            // Verificar pré-requisitos
            if (tableCounts.get("countries") == 0 || tableCounts.get("states") == 0) {
                String message = "Pré-requisitos não atendidos: ";
                if (tableCounts.get("countries") == 0) message += "Importe os países primeiro. ";
                if (tableCounts.get("states") == 0) message += "Importe os estados primeiro.";
                
                response.put("status", "error");
                response.put("message", message);
                return ResponseEntity.ok(response);
            }

            // Verificar se o arquivo existe
            String[] possiblePaths = {
                "src/main/resources/csv/cities.csv",
                "target/classes/csv/cities.csv"
            };

            boolean fileFound = false;
            String foundPath = null;
            long fileSize = 0;

            for (String pathStr : possiblePaths) {
                Path path = Paths.get(System.getProperty("user.dir"), pathStr);
                File file = path.toFile();
                if (file.exists()) {
                    fileFound = true;
                    foundPath = file.getAbsolutePath();
                    fileSize = file.length();
                    break;
                }
            }

            response.put("fileFound", fileFound);
            response.put("filePath", foundPath);
            response.put("fileSize", fileSize);

            if (!fileFound) {
                response.put("status", "error");
                response.put("message", "O arquivo de cidades não foi encontrado.");
                return ResponseEntity.ok(response);
            }

            // Devido ao tamanho do arquivo, sugerimos usar o endpoint start-cities-import
            response.put("status", "info");
            response.put("message", "Devido ao tamanho do arquivo, a importação deve ser iniciada em background");
            response.put("nextSteps", "Execute o endpoint /api/data-import/start-cities-import para iniciar a importação");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Erro ao verificar informações para importação de cidades: {}", e.getMessage(), e);
            response.put("status", "error");
            response.put("message", "Erro: " + e.getMessage());
            response.put("errorDetails", e.toString());
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/start-cities-import")
    public ResponseEntity<Map<String, Object>> startCitiesImport() {
        Map<String, Object> response = new HashMap<>();

        try {
            // Verificar pré-requisitos
            Integer countryCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM countries", Integer.class);
            Integer stateCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM states", Integer.class);
            
            response.put("countriesCount", countryCount);
            response.put("statesCount", stateCount);
            
            if (countryCount == 0 || stateCount == 0) {
                String message = "Pré-requisitos não atendidos: ";
                if (countryCount == 0) message += "Importe os países primeiro. ";
                if (stateCount == 0) message += "Importe os estados primeiro.";
                
                response.put("status", "error");
                response.put("message", message);
                return ResponseEntity.ok(response);
            }
            
            // Verificar contagens antes da importação
            Integer citiesBeforeCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM cities", Integer.class);
            response.put("citiesBeforeCount", citiesBeforeCount);

            // Iniciar a importação em um CompletableFuture para não bloquear a thread atual
            CompletableFuture.runAsync(() -> {
                try {
                    logger.info("Iniciando importação de cidades em thread separado...");
                    int importedCount = csvDataImportService.importCities();
                    logger.info("Importação de cidades concluída. {} registros importados.", importedCount);
                } catch (Exception e) {
                    logger.error("Erro durante importação de cidades: {}", e.getMessage(), e);
                }
            }, importExecutor);

            response.put("status", "started");
            response.put("message", "Importação de cidades iniciada em background. Verifique os logs para acompanhar o progresso.");
            response.put("checkStatusEndpoint", "/api/data-import/import-status");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Erro ao iniciar importação de cidades: {}", e.getMessage(), e);
            response.put("status", "error");
            response.put("message", "Erro: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/import-regions")
    public ResponseEntity<Map<String, Object>> importRegions() {
        Map<String, Object> response = new HashMap<>();

        try {
            // Verificar se a tabela existe
            boolean tableExists = jdbcTemplate.queryForList(
                "SELECT table_name FROM information_schema.tables " +
                "WHERE table_schema = (SELECT database()) AND table_name = 'regions'",
                String.class
            ).size() > 0;

            response.put("tableExists", tableExists);

            if (!tableExists) {
                response.put("status", "error");
                response.put("message", "A tabela regions não existe no banco de dados.");
                return ResponseEntity.ok(response);
            }

            // Verificar se o arquivo existe
            String[] possiblePaths = {
                "src/main/resources/csv/regions.csv",
                "target/classes/csv/regions.csv"
            };

            boolean fileFound = false;
            String foundPath = null;
            long fileSize = 0;

            for (String pathStr : possiblePaths) {
                Path path = Paths.get(System.getProperty("user.dir"), pathStr);
                File file = path.toFile();
                if (file.exists()) {
                    fileFound = true;
                    foundPath = file.getAbsolutePath();
                    fileSize = file.length();
                    break;
                }
            }

            response.put("fileFound", fileFound);
            response.put("filePath", foundPath);
            response.put("fileSize", fileSize);

            if (!fileFound) {
                response.put("status", "error");
                response.put("message", "O arquivo de regiões não foi encontrado.");
                return ResponseEntity.ok(response);
            }

            // Contar registros antes da importação
            Integer beforeCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM regions", Integer.class);
            response.put("recordsBeforeImport", beforeCount);

            // Forçar a importação
            int importedCount = csvDataImportService.importRegions();

            // Contar registros após a importação
            Integer afterCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM regions", Integer.class);
            response.put("recordsAfterImport", afterCount);
            response.put("recordsImported", importedCount);

            // Listar alguns registros para verificação
            List<Map<String, Object>> sampleRecords = jdbcTemplate.queryForList(
                "SELECT * FROM regions LIMIT 5"
            );
            response.put("sampleRecords", sampleRecords);

            response.put("status", "success");
            response.put("message", "Importação de regiões executada com sucesso");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Erro ao importar regiões: {}", e.getMessage(), e);
            response.put("status", "error");
            response.put("message", "Erro ao importar: " + e.getMessage());
            response.put("errorDetails", e.toString());
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/import-subregions")
    public ResponseEntity<Map<String, Object>> importSubRegions() {
        Map<String, Object> response = new HashMap<>();

        try {
            // Verificar se a tabela de regiões existe e tem dados
            Integer regionCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM regions", Integer.class);
            response.put("regionsCount", regionCount);

            if (regionCount == 0) {
                response.put("status", "error");
                response.put("message", "Não existem regiões no banco de dados. Importe as regiões primeiro.");
                return ResponseEntity.ok(response);
            }

            // Verificar se a tabela existe
            boolean tableExists = jdbcTemplate.queryForList(
                "SELECT table_name FROM information_schema.tables " +
                "WHERE table_schema = (SELECT database()) AND table_name = 'subregions'",
                String.class
            ).size() > 0;

            response.put("tableExists", tableExists);

            if (!tableExists) {
                response.put("status", "error");
                response.put("message", "A tabela subregions não existe no banco de dados.");
                return ResponseEntity.ok(response);
            }

            // Verificar se o arquivo existe
            String[] possiblePaths = {
                "src/main/resources/csv/subregions.csv",
                "target/classes/csv/subregions.csv"
            };

            boolean fileFound = false;
            String foundPath = null;
            long fileSize = 0;

            for (String pathStr : possiblePaths) {
                Path path = Paths.get(System.getProperty("user.dir"), pathStr);
                File file = path.toFile();
                if (file.exists()) {
                    fileFound = true;
                    foundPath = file.getAbsolutePath();
                    fileSize = file.length();
                    break;
                }
            }

            response.put("fileFound", fileFound);
            response.put("filePath", foundPath);
            response.put("fileSize", fileSize);

            if (!fileFound) {
                response.put("status", "error");
                response.put("message", "O arquivo de sub-regiões não foi encontrado.");
                return ResponseEntity.ok(response);
            }

            // Contar registros antes da importação
            Integer beforeCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM subregions", Integer.class);
            response.put("recordsBeforeImport", beforeCount);

            // Forçar a importação
            int importedCount = csvDataImportService.importSubRegions();

            // Contar registros após a importação
            Integer afterCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM subregions", Integer.class);
            response.put("recordsAfterImport", afterCount);
            response.put("recordsImported", importedCount);

            // Listar alguns registros para verificação
            List<Map<String, Object>> sampleRecords = jdbcTemplate.queryForList(
                "SELECT * FROM subregions LIMIT 5"
            );
            response.put("sampleRecords", sampleRecords);

            response.put("status", "success");
            response.put("message", "Importação de sub-regiões executada com sucesso");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Erro ao importar sub-regiões: {}", e.getMessage(), e);
            response.put("status", "error");
            response.put("message", "Erro ao importar: " + e.getMessage());
            response.put("errorDetails", e.toString());
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/import-cv-locations")
    public ResponseEntity<Map<String, Object>> importCvLocations() {
        Map<String, Object> response = new HashMap<>();

        try {
            // Verificar se a tabela existe
            boolean tableExists = jdbcTemplate.queryForList(
                "SELECT table_name FROM information_schema.tables " +
                "WHERE table_schema = (SELECT database()) AND table_name = 'cv_locations'",
                String.class
            ).size() > 0;

            response.put("tableExists", tableExists);

            if (!tableExists) {
                response.put("status", "error");
                response.put("message", "A tabela cv_locations não existe no banco de dados.");
                return ResponseEntity.ok(response);
            }

            // Verificar se o arquivo existe
            String[] possiblePaths = {
                "src/main/resources/csv/cv/codigo-lugares-cv.csv",
                "target/classes/csv/cv/codigo-lugares-cv.csv",
                System.getProperty("user.dir") + "/src/main/resources/csv/cv/codigo-lugares-cv.csv",
                System.getProperty("user.dir") + "/target/classes/csv/cv/codigo-lugares-cv.csv"
            };

            boolean fileFound = false;
            String foundPath = null;
            long fileSize = 0;

            for (String pathStr : possiblePaths) {
                File file = new File(pathStr);
                if (file.exists()) {
                    fileFound = true;
                    foundPath = file.getAbsolutePath();
                    fileSize = file.length();
                    break;
                }
            }

            response.put("fileFound", fileFound);
            response.put("filePath", foundPath);
            response.put("fileSize", fileSize);

            if (!fileFound) {
                response.put("status", "error");
                response.put("message", "O arquivo de localidades de Cabo Verde não foi encontrado.");
                return ResponseEntity.ok(response);
            }

            // Contar registros antes da importação
            Integer beforeCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM cv_locations", Integer.class);
            response.put("recordsBeforeImport", beforeCount);

            // Forçar a importação
            int importedCount = csvDataImportService.importCapeVerdeLocations();

            // Contar registros após a importação
            Integer afterCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM cv_locations", Integer.class);
            response.put("recordsAfterImport", afterCount);
            response.put("recordsImported", importedCount);

            // Listar alguns registros para verificação
            List<Map<String, Object>> sampleRecords = jdbcTemplate.queryForList(
                "SELECT * FROM cv_locations LIMIT 5"
            );
            response.put("sampleRecords", sampleRecords);

            response.put("status", "success");
            response.put("message", "Importação de localidades de Cabo Verde executada com sucesso");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Erro ao importar localidades de Cabo Verde: {}", e.getMessage(), e);
            response.put("status", "error");
            response.put("message", "Erro ao importar: " + e.getMessage());
            response.put("errorDetails", e.toString());
            return ResponseEntity.ok(response);
        }
    }
}
