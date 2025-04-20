package cv.ntinnovation.gateway_payment.application.core.utils;


import cv.ntinnovation.gateway_payment.application.core.domain.rules.DataFingerPrint6Response;
import cv.ntinnovation.gateway_payment.application.core.domain.rules.DataFingerPrint810MPResponse;
import cv.ntinnovation.gateway_payment.application.core.domain.rules.DataFingerPrintOutroResponse;

import java.util.logging.Level;
import java.util.logging.Logger;

public class HashGenerate {

	private static final Logger logger = Logger
			.getLogger(HashGenerate.class.getName());
	
	public static String HashGenerate810MP(DataFingerPrint810MPResponse dataFingerPrint810MPResponse){
		
		String merchantRespEntityCode="";
		String merchantRespReferenceNumber="";
		String merchantRespClientReceipt=dataFingerPrint810MPResponse.getMerchantRespClientReceipt();
		
		if(merchantRespClientReceipt!=null){
				merchantRespClientReceipt=merchantRespClientReceipt.replaceAll(" ","");
				merchantRespClientReceipt=merchantRespClientReceipt.trim();

		}
		try{
			merchantRespEntityCode=Long.parseLong(dataFingerPrint810MPResponse.getMerchantRespEntityCode())+"";
		}catch(Exception e){
			merchantRespEntityCode="";
		}

		try{
			merchantRespReferenceNumber=Long.parseLong(dataFingerPrint810MPResponse.getMerchantRespReferenceNumber())+"";
		}catch(Exception e){
			merchantRespReferenceNumber="";
		}
		String toHash="";
		if(dataFingerPrint810MPResponse.getMessageType().equals("8")){
			toHash= SHA.GenerateSHA512StringToBase64(dataFingerPrint810MPResponse.getPosAutCode())+
					dataFingerPrint810MPResponse.getMessageType().trim() + dataFingerPrint810MPResponse.getMerchantRespCP().trim()+
					dataFingerPrint810MPResponse.getMerchantRespTid().trim()+ dataFingerPrint810MPResponse.getMerchantRespMerchantRef().trim()+
					dataFingerPrint810MPResponse.getMerchantRespMerchantSession().trim()+ ((long)(Double.parseDouble(dataFingerPrint810MPResponse.getMerchantRespPurchaseAmount())*1000))+
					dataFingerPrint810MPResponse.getMerchantRespMessageID().trim()+dataFingerPrint810MPResponse.getMerchantRespPan().trim()+
					dataFingerPrint810MPResponse.getMerchantResp().trim() +dataFingerPrint810MPResponse.getMerchantRespTimeStamp().trim()+
					merchantRespEntityCode+ merchantRespReferenceNumber+
					merchantRespClientReceipt+
					dataFingerPrint810MPResponse.getMerchantRespAdditionalErrorMessage().trim()+
					dataFingerPrint810MPResponse.getMerchantRespReloadCode().trim();
			logger.log(Level.INFO,
					"MessageType : " +dataFingerPrint810MPResponse.getMessageType());
		}	
		else{
			
			toHash=SHA.GenerateSHA512StringToBase64(dataFingerPrint810MPResponse.getPosAutCode())+
					dataFingerPrint810MPResponse.getMessageType().trim() + dataFingerPrint810MPResponse.getMerchantRespCP().trim()+
					dataFingerPrint810MPResponse.getMerchantRespTid().trim()+ dataFingerPrint810MPResponse.getMerchantRespMerchantRef().trim()+
					dataFingerPrint810MPResponse.getMerchantRespMerchantSession().trim()+ ((long)(Double.parseDouble(dataFingerPrint810MPResponse.getMerchantRespPurchaseAmount())*1000))+
					dataFingerPrint810MPResponse.getMerchantRespMessageID().trim()+dataFingerPrint810MPResponse.getMerchantRespPan().trim()+
					dataFingerPrint810MPResponse.getMerchantResp().trim()+
					dataFingerPrint810MPResponse.getMerchantRespTimeStamp().trim()+
					merchantRespReferenceNumber+merchantRespEntityCode+
					//MerchantRespEntityCode+MerchantRespReferenceNumber +
					
					merchantRespClientReceipt+
					dataFingerPrint810MPResponse.getMerchantRespAdditionalErrorMessage().trim()+
					dataFingerPrint810MPResponse.getMerchantRespReloadCode().trim();
			logger.log(Level.INFO,
					"MessageType ALL: " +dataFingerPrint810MPResponse.getMessageType());
		}		
		
		logger.log(Level.INFO,
				"Clear Hash: " +toHash);

		return SHA.GenerateSHA512StringToBase64(toHash);
	}

	public static String HashGenerate6(DataFingerPrint6Response dataFingerPrint6Response){
		
		String toHash =SHA.GenerateSHA512StringToBase64(dataFingerPrint6Response.getPosAutCode())+
				dataFingerPrint6Response.getMessageType()+dataFingerPrint6Response.getMerchantRespMessageID()+dataFingerPrint6Response.getMerchantRespErrorCode()+
				dataFingerPrint6Response.getMerchantRespErrorDetail()+dataFingerPrint6Response.getMerchantRespErrorDescription()+
				dataFingerPrint6Response.getMerchantRespMerchantRef()+dataFingerPrint6Response.getMerchantRespMerchantSession()+
				dataFingerPrint6Response.getMerchantRespAdditionalErrorMessage()+dataFingerPrint6Response.getMerchantRespTimeStamp();
		logger.log(Level.INFO,
				"Clear Hash: " +toHash);
		return SHA.GenerateSHA512StringToBase64(toHash);
	
	}

	public static String HashGenerateOutros(DataFingerPrintOutroResponse dataFingerPrintOutroResponse){
		
		String toHash =SHA.GenerateSHA512StringToBase64(dataFingerPrintOutroResponse.getPosAutCode())+
				dataFingerPrintOutroResponse.getMessageType()+ dataFingerPrintOutroResponse.getMerchantRespMerchantRef()+
				dataFingerPrintOutroResponse.getMerchantRespMerchantSession();
		logger.log(Level.INFO,
				"Clear Hash: " +toHash);

		return SHA.GenerateSHA512StringToBase64(toHash);
	}
}
