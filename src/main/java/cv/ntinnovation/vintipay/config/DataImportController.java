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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller para diagnóstico e operações de importação de dados
 */
@RestController
@RequestMapping("/api/data-import")
public class DataImportController {

    private static final Logger logger = LoggerFactory.getLogger(DataImportController.class);
    
    private final CsvLocationDataImporter csvImporter;
    private final JdbcTemplate jdbcTemplate;
    
    @Autowired
    public DataImportController(CsvLocationDataImporter csvImporter, JdbcTemplate jdbcTemplate) {
        this.csvImporter = csvImporter;
        this.jdbcTemplate = jdbcTemplate;
    }
    
    @GetMapping("/force-cv-import")
    public ResponseEntity<Map<String, Object>> forceCvImport() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Registrar informações de diagnóstico
            response.put("workingDirectory", System.getProperty("user.dir"));
            
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
                "src/main/resources/static/csv/cv/codigo-lugares-cv.csv",
                "target/classes/csv/cv/codigo-lugares-cv.csv"
            };
            
            boolean fileFound = false;
            String foundPath = null;
            long fileSize = 0;
            int lineCount = 0;
            
            for (String pathStr : possiblePaths) {
                Path path = Paths.get(System.getProperty("user.dir"), pathStr);
                File file = path.toFile();
                if (file.exists()) {
                    fileFound = true;
                    foundPath = file.getAbsolutePath();
                    fileSize = file.length();
                    lineCount = Files.readAllLines(path).size();
                    break;
                }
            }
            
            response.put("fileFound", fileFound);
            response.put("filePath", foundPath);
            response.put("fileSize", fileSize);
            response.put("lineCount", lineCount);
            
            if (!fileFound) {
                response.put("status", "error");
                response.put("message", "O arquivo de Cabo Verde não foi encontrado.");
                return ResponseEntity.ok(response);
            }
            
            // Contar registros antes da importação
            Integer beforeCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM cv_locations", Integer.class);
            response.put("recordsBeforeImport", beforeCount);
            
            // Forçar a importação
            csvImporter.forceImportCvLocationsOnly();
            
            // Contar registros após a importação
            Integer afterCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM cv_locations", Integer.class);
            response.put("recordsAfterImport", afterCount);
            response.put("recordsImported", afterCount - beforeCount);
            
            response.put("status", "success");
            response.put("message", "Importação forçada executada com sucesso");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Erro ao forçar importação de Cabo Verde: {}", e.getMessage(), e);
            response.put("status", "error");
            response.put("message", "Erro ao importar: " + e.getMessage());
            response.put("errorDetails", e.toString());
            return ResponseEntity.ok(response);
        }
    }
    
    @GetMapping("/cv-data-info")
    public ResponseEntity<Map<String, Object>> cvDataInfo() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Verificar se a tabela existe
            boolean tableExists = jdbcTemplate.queryForList(
                "SELECT table_name FROM information_schema.tables " +
                "WHERE table_schema = (SELECT database()) AND table_name = 'cv_locations'",
                String.class
            ).size() > 0;
            
            response.put("tableExists", tableExists);
            
            if (tableExists) {
                // Contar registros
                Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM cv_locations", Integer.class);
                response.put("recordCount", count);
                
                // Mostrar alguns registros para diagnóstico
                if (count > 0) {
                    response.put("sampleRecords", jdbcTemplate.queryForList(
                        "SELECT * FROM cv_locations LIMIT 5", Map.class));
                }
            }
            
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Erro ao obter informações de dados CV: {}", e.getMessage(), e);
            response.put("status", "error");
            response.put("message", "Erro: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
}
