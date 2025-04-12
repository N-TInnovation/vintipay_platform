package cv.ntinnovation.gateway_payment;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TesteHash {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String secret = "secret_90000304";
        String hashSalvo = "$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG";
        
        boolean matches = encoder.matches(secret, hashSalvo);
        System.out.println("Secret: " + secret);
        System.out.println("Hash salvo: " + hashSalvo);
        System.out.println("Matches: " + matches);
        
        // Gerar um novo hash para comparar
        String novoHash = encoder.encode(secret);
        System.out.println("Novo hash gerado: " + novoHash);
        System.out.println("Novo hash matches: " + encoder.matches(secret, novoHash));
    }
}
