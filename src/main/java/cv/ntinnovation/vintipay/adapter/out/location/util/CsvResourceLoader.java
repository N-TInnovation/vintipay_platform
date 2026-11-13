package cv.ntinnovation.vintipay.adapter.out.location.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class CsvResourceLoader {

    private static final Logger logger = LoggerFactory.getLogger(CsvResourceLoader.class);

    public String loadAsString(String resourcePath) throws IOException {
        String[] possiblePaths = {
            resourcePath,
            "static/" + resourcePath,
            "src/main/resources/" + resourcePath,
            "target/classes/" + resourcePath,
            System.getProperty("user.dir") + "/" + resourcePath
        };

        for (String path : possiblePaths) {
            try {
                Resource resource = new ClassPathResource(path);
                if (resource.exists() && resource.isReadable()) {
                    logger.debug("Recurso encontrado no classpath: {}", path);
                    return readResourceContent(resource);
                }

                File file = ResourceUtils.getFile("file:" + path);
                if (file.exists() && file.canRead()) {
                    logger.debug("Recurso encontrado no sistema de arquivos: {}", file.getAbsolutePath());
                    return Files.readString(file.toPath(), StandardCharsets.UTF_8);
                }

                Path filePath = Paths.get(path);
                if (Files.exists(filePath) && Files.isReadable(filePath)) {
                    logger.debug("Recurso encontrado como caminho absoluto: {}", filePath.toAbsolutePath());
                    return Files.readString(filePath, StandardCharsets.UTF_8);
                }
            } catch (Exception e) {
                logger.trace("Erro ao tentar carregar de {}: {}", path, e.getMessage());
            }
        }

        throw new IOException("Não foi possível encontrar o arquivo em nenhum local: " + resourcePath);
    }

    private String readResourceContent(Resource resource) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            return content.toString();
        }
    }
}
