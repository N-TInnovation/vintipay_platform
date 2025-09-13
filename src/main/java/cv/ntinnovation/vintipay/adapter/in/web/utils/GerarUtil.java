package cv.ntinnovation.vintipay.adapter.in.web.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class GerarUtil {

    public static String geraMerchantRef(){
        LocalDateTime now = LocalDateTime.now();
        // Formato compacto: R + yMMddHHmm (1+9 caracteres) + 2 alfanuméricos aleatórios = 15 caracteres
        String timeComponent = now.format(DateTimeFormatter.ofPattern("yMMddHHmm"));
        String randomComponent = generateRandomAlphanumeric(2);
        return "R" + timeComponent + randomComponent;
    }

    private static String generateRandomAlphanumeric(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    public static String geraMerchantSession(){
        LocalDateTime now = LocalDateTime.now();
        // Formato compacto: R + yMMddHHmm (1+9 caracteres) + 2 alfanuméricos aleatórios = 15 caracteres
        String timeComponent = now.format(DateTimeFormatter.ofPattern("yMMddHHmm"));
        String randomComponent = generateRandomAlphanumeric(2);
        return "S" + timeComponent + randomComponent;
    }
}
