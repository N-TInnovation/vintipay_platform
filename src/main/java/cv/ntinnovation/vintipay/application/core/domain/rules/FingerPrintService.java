package cv.ntinnovation.vintipay.application.core.domain.rules;

import cv.ntinnovation.vintipay.application.core.domain.entity.*;
import cv.ntinnovation.vintipay.application.core.domain.exception.FingerprintInvalidoException;
import cv.ntinnovation.vintipay.application.core.utils.HashGenerate;
import cv.ntinnovation.vintipay.application.core.utils.SHA;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class FingerPrintService {

	private static final Logger logger = Logger
			.getLogger(FingerPrintService.class.getName());
			
	@Value("${app.encryption.secret:chave_secreta_padrao_para_criptografia_aes256}")
	private String encryptionSecret;

	public FingerPrintResult generateFingerPrint(DataFingerPrint dataFingerPrint){

		FingerPrintResult fingerPrintResult = FingerPrintResult.builder().build();

		String toHash = SHA.GenerateSHA512StringToBase64(dataFingerPrint.getPosAutCode())+
				 dataFingerPrint.getTimestamp() + dataFingerPrint.getAmount().multiply(BigDecimal.valueOf(1000)).longValue()+
				 dataFingerPrint.getMerchantRef().trim() + dataFingerPrint.getMerchantSession().trim() + dataFingerPrint.getPosID().trim() +
				 dataFingerPrint.getCurrency().trim() + dataFingerPrint.getTransactionCode().trim();

		logger.log(Level.INFO,
		  "PosAutcodeHash: " +dataFingerPrint.getPosAutCode()
		  +"\nTimestamp: " +dataFingerPrint.getTimestamp()
		  +"\n Amount: " +dataFingerPrint.getAmount()
		  +"\nMerchantRef: " + dataFingerPrint.getMerchantRef().trim()
		  +"\nMerchantSession: " + dataFingerPrint.getMerchantSession().trim()
		  +"\nPosID: " +dataFingerPrint.getPosID().trim()
		  +"\nCurrency: " +dataFingerPrint.getCurrency().trim()
		  +"\nTransactionCode: " +dataFingerPrint.getTransactionCode().trim()
		  +"\nEntityCode: " +dataFingerPrint.getEntityCode().trim()
		  +"\nReferenceNumber: " +dataFingerPrint.getReferenceNumber().trim()
				);

		if( dataFingerPrint.getEntityCode()!=null && !dataFingerPrint.getEntityCode().isEmpty() ){
			if(!dataFingerPrint.getEntityCode().equals("null")){
				toHash+=Long.parseLong(dataFingerPrint.getEntityCode());
			}
		}
		if(dataFingerPrint.getReferenceNumber()!=null && !dataFingerPrint.getReferenceNumber().isEmpty() ){
			if(!dataFingerPrint.getReferenceNumber().equals("null")){
				toHash+=Long.parseLong(dataFingerPrint.getReferenceNumber());
			}
		}
		logger.log(Level.INFO,
				"Base64 PosAutcodeHash: " +SHA.GenerateSHA512StringToBase64(dataFingerPrint.getPosAutCode()));
		logger.log(Level.INFO,
				"ToHash: " +toHash);
		logger.log(Level.INFO,
				"Base64 ToHash: " +SHA.GenerateSHA512StringToBase64(toHash));
		fingerPrintResult.setHashFingerPrint(SHA.GenerateSHA512StringToBase64(toHash));
		fingerPrintResult.setHashposAutCode(SHA.GenerateSHA512StringToBase64(dataFingerPrint.getPosAutCode()));
		fingerPrintResult.setResult(true);

		return fingerPrintResult;
	}

	public FingerPrintResult validatorFingerPrintMessageType810MP(DataFingerPrint810MPResponse dataFingerPrint810MPResponse){

		FingerPrintResult fingerPrintResult = FingerPrintResult.builder().build();
		fingerPrintResult.setResult(validateAuthorizationCode(dataFingerPrint810MPResponse));
		if (!fingerPrintResult.getResult()) return fingerPrintResult;

		String resHash= HashGenerate.HashGenerate810MP(dataFingerPrint810MPResponse);

		logger.log(Level.INFO,
				"Generate resHash: " +resHash);

		logger.log(Level.INFO,
				"Generate SISP ToHash: " +dataFingerPrint810MPResponse.getResultFingerPrint());

		fingerPrintResult.setResult(false);

		if(resHash.equals(dataFingerPrint810MPResponse.getResultFingerPrint())){
			fingerPrintResult.setResult(true);
		}

		fingerPrintResult.setHashFingerPrint(resHash);
		return fingerPrintResult;
	}




	public FingerPrintResult validatorFingerPrintMessageType6(DataFingerPrint6Response dataFingerPrint6Response){

		FingerPrintResult fingerPrintResult = FingerPrintResult.builder().build();
		fingerPrintResult.setResult(validateAuthorizationCode(dataFingerPrint6Response));
		if (!fingerPrintResult.getResult()) return fingerPrintResult;
		String resHash=HashGenerate.HashGenerate6(dataFingerPrint6Response);
		logger.log(Level.INFO,
				"Generate ToHash: " +resHash);
		logger.log(Level.INFO,
				"Generate SISP ToHash: " +dataFingerPrint6Response.getResultFingerPrint());
		fingerPrintResult.setResult(false);
		if(resHash.equals(dataFingerPrint6Response.getResultFingerPrint())){
			fingerPrintResult.setResult(true);
		}
		fingerPrintResult.setHashFingerPrint(resHash);
		return fingerPrintResult;
	}

	public FingerPrintResult validatorFingerPrintMessageTypeOutros(DataFingerPrintOutroResponse dataFingerPrintOutroResponse){

		FingerPrintResult fingerPrintResult = FingerPrintResult.builder().build();
		fingerPrintResult.setResult(validateAuthorizationCode(dataFingerPrintOutroResponse));

		return fingerPrintResult;
	}


	/**
	 * Criptografa uma string usando AES256
	 * 
	 * @param plainText Texto a ser criptografado
	 * @return String criptografada em formato Base64
	 */
	public String encryptAES256(String plainText) {
		try {
			// Derivar uma chave de 256 bits a partir da senha
			SecretKey secretKey = deriveKeyFromPassword(encryptionSecret);
			
			// Gerar um IV aleatório
			byte[] iv = new byte[16];
			SecureRandom random = new SecureRandom();
			random.nextBytes(iv);
			IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
			
			// Configurar o cipher
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
			
			// Criptografar
			byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
			
			// Combinar IV e texto criptografado e codificar em Base64
			byte[] combined = new byte[iv.length + encrypted.length];
			System.arraycopy(iv, 0, combined, 0, iv.length);
			System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);
			
			return Base64.getEncoder().encodeToString(combined);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Erro ao criptografar: " + e.getMessage(), e);
			throw new RuntimeException("Erro ao criptografar", e);
		}
	}
	
	/**
	 * Descriptografa uma string usando AES256
	 * 
	 * @param encryptedText Texto criptografado em formato Base64
	 * @return String descriptografada
	 */
	public String decryptAES256(String encryptedText) {
		try {
			// Derivar a mesma chave de 256 bits
			SecretKey secretKey = deriveKeyFromPassword(encryptionSecret);
			
			// Decodificar o texto criptografado de Base64
			byte[] combined = Base64.getDecoder().decode(encryptedText);
			
			// Extrair o IV (primeiros 16 bytes)
			byte[] iv = new byte[16];
			System.arraycopy(combined, 0, iv, 0, iv.length);
			IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
			
			// Extrair o texto criptografado (restante dos bytes)
			byte[] encrypted = new byte[combined.length - iv.length];
			System.arraycopy(combined, iv.length, encrypted, 0, encrypted.length);
			
			// Configurar o cipher para descriptografia
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
			
			// Descriptografar
			byte[] decrypted = cipher.doFinal(encrypted);
			
			return new String(decrypted, StandardCharsets.UTF_8);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Erro ao descriptografar: " + e.getMessage(), e);
			throw new RuntimeException("Erro ao descriptografar", e);
		}
	}
	
	/**
	 * Gera uma chave AES256 aleatória e a retorna em formato Base64
	 * 
	 * @return Chave AES256 em formato Base64
	 */
	public String generateAES256Key() {
		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
			keyGenerator.init(256, new SecureRandom());
			SecretKey key = keyGenerator.generateKey();
			return Base64.getEncoder().encodeToString(key.getEncoded());
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Erro ao gerar chave AES256: " + e.getMessage(), e);
			throw new RuntimeException("Erro ao gerar chave AES256", e);
		}
	}
	
	/**
	 * Método auxiliar para derivar uma chave AES256 a partir de uma senha
	 */
	private SecretKey deriveKeyFromPassword(String password) {
		try {
			// Usar um hash SHA-256 da senha para obter uma chave de 256 bits
			byte[] keyBytes = SHA.GenerateSHA256(password);
			return new SecretKeySpec(keyBytes, "AES");
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Erro ao derivar chave: " + e.getMessage(), e);
			throw new RuntimeException("Erro ao derivar chave", e);
		}
	}
	
	/**
	 * Gera um HMAC (Hash-based Message Authentication Code) usando o algoritmo HmacSHA256
	 * 
	 * @param data Dados a serem assinados
	 * @param key Chave secreta para assinatura
	 * @return String em formato Base64 contendo o HMAC
	 */
	public String generateHmac(String data, String key) {
		try {
			// Criar a chave secreta para HMAC
			SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
			
			// Inicializar o MAC
			Mac mac = Mac.getInstance("HmacSHA256");
			mac.init(secretKeySpec);
			
			// Calcular o HMAC
			byte[] hmacBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
			
			// Converter para Base64
			return Base64.getEncoder().encodeToString(hmacBytes);
		} catch (NoSuchAlgorithmException | InvalidKeyException e) {
			logger.log(Level.SEVERE, "Erro ao gerar HMAC: " + e.getMessage(), e);
			throw new RuntimeException("Erro ao gerar HMAC", e);
		}
	}
	
	/**
	 * Gera um HMAC para os dados de resposta do checkout
	 * 
	 * @param reference Referência do pagamento
	 * @param status Status do pagamento
	 * @param message Mensagem de retorno
	 * @param amount Valor do pagamento
	 * @param currency Moeda do pagamento
	 * @param merchantSession Sessão do comerciante
	 * @param merchantRef Referência do comerciante
	 * @param timestamp Timestamp da transação
	 * @param secretKey Chave secreta para assinatura
	 * @return String em formato Base64 contendo o HMAC
	 */
	public String generateCheckoutResponseHmac(String reference, String status, String message, 
			BigDecimal amount, String currency, String merchantSession, String merchantRef, 
			String timestamp, String panMascarado, String secretKey) {
		
		// Concatenar os dados na ordem correta
		String data = reference + status + message + amount.toString() + currency + 
				merchantSession + merchantRef + timestamp + panMascarado;
		
		// Gerar e retornar o HMAC
		return generateHmac(data, secretKey);
	}
	
	/**
	 * Verifica se um HMAC é válido
	 * 
	 * @param data Dados originais
	 * @param hmac HMAC a ser verificado
	 * @param key Chave secreta
	 * @return true se o HMAC for válido, false caso contrário
	 */
	public boolean verifyHmac(String data, String hmac, String key) {
		String calculatedHmac = generateHmac(data, key);
		return calculatedHmac.equals(hmac);
	}

	private boolean validateAuthorizationCode(RetornoFingerprint retornoFingerprint) {
		try {
			var pagamento = retornoFingerprint.getPagamento();
			var data = pagamento.getIntencaoPagamento().getPosId()+"|"
					+pagamento.getIntencaoPagamento().getAmount().longValue()+"|"
					+pagamento.getIntencaoPagamento().getTransactionCode()+"|"
					+pagamento.getIntencaoPagamento().getReference()+"|"
					+pagamento.getIntencaoPagamento().getContaPagamento().getEmail()
					+"|"+pagamento.getIntencaoPagamento().getContaPagamento().getMobilePhone();
			var fingerprint = SHA.GenerateSHA512StringToBase64(data);
			var plainText = decryptAES256(retornoFingerprint.getAuthorizationCode());
			var authorizationCodeHash = SHA.GenerateSHA512StringToBase64(plainText);
			return authorizationCodeHash.equals(fingerprint);
		} catch (RuntimeException e) {
			throw new FingerprintInvalidoException("Fingerprint Invalido");
		}
	}
}