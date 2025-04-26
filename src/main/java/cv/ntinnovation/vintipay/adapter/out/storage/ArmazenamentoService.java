package cv.ntinnovation.vintipay.adapter.out.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class ArmazenamentoService {

    @Value("${app.upload.dir:${user.home}/vintipay/uploads}")
    private String uploadDir;

    /**
     * Armazena um arquivo e retorna o caminho relativo para acesso
     *
     * @param file Arquivo a ser armazenado
     * @param subDir Subdiretório opcional para organizar os arquivos
     * @return Caminho relativo do arquivo armazenado
     * @throws IOException Se ocorrer um erro ao armazenar o arquivo
     */
    public String armazenarArquivo(MultipartFile file, String subDir) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Arquivo vazio");
        }

        // Criar diretório base se não existir
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Adicionar subdiretório se especificado
        if (subDir != null && !subDir.isEmpty()) {
            uploadPath = uploadPath.resolve(subDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
        }

        // Gerar nome único para o arquivo
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String filename = UUID.randomUUID().toString() + fileExtension;

        // Salvar o arquivo
        try (InputStream inputStream = file.getInputStream()) {
            Path filePath = uploadPath.resolve(filename);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            
            // Retornar caminho relativo
            String relativePath = "/uploads";
            if (subDir != null && !subDir.isEmpty()) {
                relativePath += "/" + subDir;
            }
            return relativePath + "/" + filename;
        } catch (IOException e) {
            throw new IOException("Erro ao armazenar arquivo: " + e.getMessage(), e);
        }
    }
}
