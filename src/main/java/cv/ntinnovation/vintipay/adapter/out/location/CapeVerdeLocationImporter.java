package cv.ntinnovation.vintipay.adapter.out.location;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import cv.ntinnovation.vintipay.adapter.out.location.util.CsvResourceLoader;
import cv.ntinnovation.vintipay.adapter.out.persistence.entity.CapeVerdeLocationsEntity;
import cv.ntinnovation.vintipay.adapter.out.persistence.repository.CapeVerdeLocationsRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CapeVerdeLocationImporter {

    private static final Logger logger = LoggerFactory.getLogger(CapeVerdeLocationImporter.class);

    private final CapeVerdeLocationsRepository capeVerdeLocationsRepository;
    private final CsvResourceLoader csvResourceLoader;

    @Transactional
    public int importLocations() throws IOException, CsvValidationException {
        logger.info("Iniciando importação de localidades de Cabo Verde");
        String csvContent = csvResourceLoader.loadAsString("csv/cv/codigo-lugares-cv.csv");

        if (csvContent == null || csvContent.isEmpty()) {
            throw new IOException("Arquivo de localidades de Cabo Verde não encontrado");
        }

        try (CSVReader csvReader = new CSVReader(new StringReader(csvContent))) {
            String[] headerLine = csvReader.readNext();
            if (headerLine == null) {
                throw new IOException("Arquivo CSV de localidades de Cabo Verde vazio ou inválido");
            }

            Map<String, Integer> headerMap = new HashMap<>();
            for (int i = 0; i < headerLine.length; i++) {
                headerMap.put(headerLine[i].trim().toLowerCase(), i);
            }

            Map<String, CapeVerdeLocationsEntity> existingLocationsMap = new HashMap<>();
            capeVerdeLocationsRepository.findAll().forEach(location ->
                    existingLocationsMap.put(location.getCodigo(), location));

            logger.info("Encontrados {} registros existentes de localidades de Cabo Verde", existingLocationsMap.size());

            int importedCount = 0;
            String[] line;
            while ((line = csvReader.readNext()) != null) {
                try {
                    String codigo = getValueAsString(line, headerMap, "codigo");
                    if (codigo.isEmpty()) {
                        continue;
                    }

                    Integer nivel = getValueAsInteger(line, headerMap, "nivel");
                    String pais = getValueAsString(line, headerMap, "pais");
                    String ilha = getValueAsString(line, headerMap, "ilha");
                    String concelho = getValueAsString(line, headerMap, "concelho");
                    String freguesia = getValueAsString(line, headerMap, "freguesia");
                    String zona = getValueAsString(line, headerMap, "zona");
                    String lugar = getValueAsString(line, headerMap, "lugar");
                    String nome = getValueAsString(line, headerMap, "nome");
                    String lat = getValueAsString(line, headerMap, "lat");
                    String lng = getValueAsString(line, headerMap, "lng");

                    CapeVerdeLocationsEntity location = existingLocationsMap.getOrDefault(codigo, new CapeVerdeLocationsEntity());
                    location.setCodigo(codigo);
                    location.setNivel(nivel);
                    location.setPais(pais);
                    location.setIlha(ilha);
                    location.setConcelho(concelho);
                    location.setFreguesia(freguesia);
                    location.setZona(zona);
                    location.setLugar(lugar);
                    location.setNome(nome);
                    location.setLat(lat);
                    location.setLng(lng);

                    capeVerdeLocationsRepository.save(location);
                    importedCount++;

                    if (importedCount % 100 == 0) {
                        logger.info("Importados {} registros de localidades de Cabo Verde", importedCount);
                    }
                } catch (Exception e) {
                    logger.error("Erro ao processar linha de localidade de Cabo Verde: {}", e.getMessage(), e);
                }
            }

            logger.info("Importação de localidades de Cabo Verde concluída. Total: {}", importedCount);
            return importedCount;
        }
    }

    private String getValueAsString(String[] line, Map<String, Integer> headerMap, String columnName) {
        Integer index = headerMap.get(columnName);
        if (index == null || index >= line.length || line[index] == null) {
            return "";
        }
        return line[index].trim();
    }

    private Integer getValueAsInteger(String[] line, Map<String, Integer> headerMap, String columnName) {
        String value = getValueAsString(line, headerMap, columnName);
        if (value.isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            logger.warn("Valor inválido para Integer na coluna {}: {}", columnName, value);
            return null;
        }
    }
}
