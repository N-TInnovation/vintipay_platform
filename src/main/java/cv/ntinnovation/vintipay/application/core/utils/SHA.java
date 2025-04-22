package cv.ntinnovation.vintipay.application.core.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class SHA {

    public static String GenerateSHA512StringToBase64(String input) 
    { 
        try { 
            // getInstance() method is called with algorithm SHA-512 
            MessageDigest md = MessageDigest.getInstance("SHA-512"); 
            //md.update(input.getBytes(StandardCharsets.UTF_8));
            byte[] messageDigest = md.digest(input.getBytes()); 
            String encodedString = Base64.getEncoder().encodeToString(messageDigest);

            return encodedString; 
        } 
  
        // For specifying wrong message digest algorithms 
        catch (NoSuchAlgorithmException e) { 
            throw new RuntimeException(e); 
        } 
    }
    
    /**
     * Gera um hash SHA-256 a partir de uma string de entrada
     * 
     * @param input String a ser convertida em hash
     * @return Array de bytes contendo o hash SHA-256
     */
    public static byte[] GenerateSHA256(String input) 
    { 
        try { 
            // getInstance() method is called with algorithm SHA-256
            MessageDigest md = MessageDigest.getInstance("SHA-256"); 
            byte[] messageDigest = md.digest(input.getBytes()); 
            
            return messageDigest; 
        } 
        catch (NoSuchAlgorithmException e) { 
            throw new RuntimeException(e); 
        } 
    }
    
    /**
     * Gera um hash SHA-256 a partir de uma string de entrada e retorna em formato Base64
     * 
     * @param input String a ser convertida em hash
     * @return String em formato Base64 contendo o hash SHA-256
     */
    public static String GenerateSHA256StringToBase64(String input) 
    { 
        try { 
            // getInstance() method is called with algorithm SHA-256
            MessageDigest md = MessageDigest.getInstance("SHA-256"); 
            byte[] messageDigest = md.digest(input.getBytes()); 
            String encodedString = Base64.getEncoder().encodeToString(messageDigest);
            
            return encodedString; 
        } 
        catch (NoSuchAlgorithmException e) { 
            throw new RuntimeException(e); 
        } 
    }
}
