package cv.ntinnovation.gateway_payment.application.core.domain.rules;

import cv.ntinnovation.gateway_payment.application.core.domain.entity.*;
import cv.ntinnovation.gateway_payment.application.core.utils.HashGenerate;
import cv.ntinnovation.gateway_payment.application.core.utils.SHA;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class FingerPrintService {

	private static final Logger logger = Logger
			.getLogger(FingerPrintService.class.getName());

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

	public FingerPrintResult validatorFingerPrintMessageTypeOutros(DataFingerPrintOutroResponse DataFingerPrintOutroResponse){

		FingerPrintResult fingerPrintResult =  FingerPrintResult.builder().build();
		String resHash=HashGenerate.HashGenerateOutros(DataFingerPrintOutroResponse);
		logger.log(Level.INFO,
				"Generate ToHash: " +resHash);
		logger.log(Level.INFO,
				"Generate SISP ToHash: " +DataFingerPrintOutroResponse.getResultFingerPrint());
		fingerPrintResult.setResult(false);
		if(resHash.equals(DataFingerPrintOutroResponse.getResultFingerPrint())){
			fingerPrintResult.setResult(true);
		}
		fingerPrintResult.setHashFingerPrint(resHash);
		return fingerPrintResult;
	}
}