package cv.ntinnovation.vintipay.config;

import com.opencsv.exceptions.CsvValidationException;
import cv.ntinnovation.vintipay.adapter.out.location.CapeVerdeLocationImporter;
import cv.ntinnovation.vintipay.adapter.out.location.LocationDataImporter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class CsvDataImportService {

    private static final Logger logger = LoggerFactory.getLogger(CsvDataImportService.class);

    private final LocationDataImporter locationDataImporter;
    private final CapeVerdeLocationImporter capeVerdeLocationImporter;

    public int importRegions() throws IOException, CsvValidationException {
        return locationDataImporter.importRegions();
    }

    public int importSubRegions() throws IOException, CsvValidationException {
        return locationDataImporter.importSubRegions();
    }

    public int importCountries() throws IOException, CsvValidationException {
        return locationDataImporter.importCountries();
    }

    public int importStates() throws IOException, CsvValidationException {
        return locationDataImporter.importStates();
    }

    public int importCities() throws IOException, CsvValidationException {
        return locationDataImporter.importCities();
    }

    public int importCapeVerdeLocations() throws IOException, CsvValidationException {
        return capeVerdeLocationImporter.importLocations();
    }
}
