package cv.ntinnovation.gateway_payment.application.core.utils;

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
}
