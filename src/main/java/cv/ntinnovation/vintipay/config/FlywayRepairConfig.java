package cv.ntinnovation.vintipay.config;

import jakarta.annotation.PostConstruct;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationVersion;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;

/**
 * Configuração para resolver problemas com migrações falhas do Flyway.
 * Esta classe é executada antes do Flyway iniciar e tenta reparar ou limpar
 * o histórico de migrações.
 */
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.flyway.repair-history", havingValue = "true", matchIfMissing = false)
public class FlywayRepairConfig {

    private static final Logger logger = LoggerFactory.getLogger(FlywayRepairConfig.class);

    private final DataSource dataSource;
    private final Environment environment;

    @Value("${spring.flyway.repair-strategy:mark-successful}")
    private String repairStrategy;

    @PostConstruct
    public void repairFlywayHistory() {
        try {
            // Verificar se estamos em ambiente de produção ou render
            String[] activeProfiles = environment.getActiveProfiles();
            boolean isProdOrRender = Arrays.asList(activeProfiles).contains("prod") || 
                                     Arrays.asList(activeProfiles).contains("render");
            
            if (!isProdOrRender) {
                logger.info("Pulando reparo do Flyway, pois não estamos em ambiente de produção ou render");
                return;
            }
            
            logger.info("Iniciando reparo do histórico de migrações do Flyway com estratégia: {}", repairStrategy);
            
            if ("mark-successful".equals(repairStrategy)) {
                markFailedMigrationsAsSuccessful();
            } else if ("delete-failed".equals(repairStrategy)) {
                deleteFailedMigrations();
            } else if ("clean-all".equals(repairStrategy)) {
                cleanAllMigrations();
            } else {
                logger.warn("Estratégia de reparo desconhecida: {}", repairStrategy);
            }
            
        } catch (Exception e) {
            logger.error("Erro ao tentar reparar histórico do Flyway: {}", e.getMessage(), e);
        }
    }
    
    private void markFailedMigrationsAsSuccessful() {
        try (Connection conn = dataSource.getConnection()) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            
            // Verifica se a tabela flyway_schema_history existe
            boolean tableExists = jdbcTemplate.queryForList(
                "SELECT table_name FROM information_schema.tables " +
                "WHERE table_schema = (SELECT database()) AND table_name = 'flyway_schema_history'",
                String.class
            ).size() > 0;
            
            if (!tableExists) {
                logger.info("Tabela flyway_schema_history não existe. Nada a reparar.");
                return;
            }
            
            // Marca migrações falhas como bem-sucedidas
            int updated = jdbcTemplate.update(
                "UPDATE flyway_schema_history SET success = 1 WHERE success = 0"
            );
            
            logger.info("Migrações falhas marcadas como bem-sucedidas: {}", updated);
            
        } catch (SQLException e) {
            logger.error("Erro ao marcar migrações falhas como bem-sucedidas: {}", e.getMessage(), e);
        }
    }
    
    private void deleteFailedMigrations() {
        try {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            
            // Verifica se a tabela flyway_schema_history existe
            boolean tableExists = jdbcTemplate.queryForList(
                "SELECT table_name FROM information_schema.tables " +
                "WHERE table_schema = (SELECT database()) AND table_name = 'flyway_schema_history'",
                String.class
            ).size() > 0;
            
            if (!tableExists) {
                logger.info("Tabela flyway_schema_history não existe. Nada a reparar.");
                return;
            }
            
            // Remove migrações falhas
            int deleted = jdbcTemplate.update(
                "DELETE FROM flyway_schema_history WHERE success = 0"
            );
            
            logger.info("Migrações falhas removidas: {}", deleted);
            
        } catch (Exception e) {
            logger.error("Erro ao remover migrações falhas: {}", e.getMessage(), e);
        }
    }
    
    private void cleanAllMigrations() {
        try {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            
            // Verifica se a tabela flyway_schema_history existe
            boolean tableExists = jdbcTemplate.queryForList(
                "SELECT table_name FROM information_schema.tables " +
                "WHERE table_schema = (SELECT database()) AND table_name = 'flyway_schema_history'",
                String.class
            ).size() > 0;
            
            if (!tableExists) {
                logger.info("Tabela flyway_schema_history não existe. Nada a limpar.");
                return;
            }
            
            // Limpa toda a tabela de histórico
            int deleted = jdbcTemplate.update("DELETE FROM flyway_schema_history");
            
            logger.info("Histórico de migrações limpo. Registros removidos: {}", deleted);
            
        } catch (Exception e) {
            logger.error("Erro ao limpar histórico de migrações: {}", e.getMessage(), e);
        }
    }
}
