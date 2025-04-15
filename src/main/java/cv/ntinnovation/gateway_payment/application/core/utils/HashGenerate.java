package cv.ntinnovation.gateway_payment.application.core.utils;


import cv.ntinnovation.gateway_payment.application.core.domain.entity.DataFingerPrint6Response;
import cv.ntinnovation.gateway_payment.application.core.domain.entity.DataFingerPrint810MPResponse;
import cv.ntinnovation.gateway_payment.application.core.domain.entity.DataFingerPrintOutroResponse;

import java.util.logging.Level;
import java.util.logging.Logger;

public class HashGenerate {

	private static final Logger logger = Logger
			.getLogger(HashGenerate.class.getName());
	
	public static String HashGenerate810MP(DataFingerPrint810MPResponse dataFingerPrint810MPResponse){
		
		String MerchantRespEntityCode="";
		String MerchantRespReferenceNumber="";
		String MerchantRespClientReceipt=dataFingerPrint810MPResponse.getMerchantRespClientReceipt();
		
		if(MerchantRespClientReceipt!=null){
				MerchantRespClientReceipt=MerchantRespClientReceipt.replaceAll(" ","");
				MerchantRespClientReceipt=MerchantRespClientReceipt.trim();

		}
		try{
			MerchantRespEntityCode=Long.parseLong(dataFingerPrint810MPResponse.getMerchantRespEntityCode())+"";
		}catch(Exception e){
			MerchantRespEntityCode="";
		}

		try{
			MerchantRespReferenceNumber=Long.parseLong(dataFingerPrint810MPResponse.getMerchantRespReferenceNumber())+"";
		}catch(Exception e){
			MerchantRespReferenceNumber="";
		}
		String toHash="";
		if(dataFingerPrint810MPResponse.getMessageType().equals("8")){
			toHash= SHA.GenerateSHA512StringToBase64(dataFingerPrint810MPResponse.getPosAutCode())+
					dataFingerPrint810MPResponse.getMessageType().trim() + dataFingerPrint810MPResponse.getMerchantRespCP().trim()+
					dataFingerPrint810MPResponse.getMerchantRespTid().trim()+ dataFingerPrint810MPResponse.getMerchantRespMerchantRef().trim()+
					dataFingerPrint810MPResponse.getMerchantRespMerchantSession().trim()+ ((long)(Double.parseDouble(dataFingerPrint810MPResponse.getMerchantRespPurchaseAmount())*1000))+
					dataFingerPrint810MPResponse.getMerchantRespMessageID().trim()+dataFingerPrint810MPResponse.getMerchantRespPan().trim()+
					dataFingerPrint810MPResponse.getMerchantResp().trim() +dataFingerPrint810MPResponse.getMerchantRespTimeStamp().trim()+
					MerchantRespEntityCode+ MerchantRespReferenceNumber+
					MerchantRespClientReceipt+
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
					MerchantRespReferenceNumber+MerchantRespEntityCode+
					//MerchantRespEntityCode+MerchantRespReferenceNumber +
					
					MerchantRespClientReceipt+
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
				dataFingerPrint6Response.getMessageType()+dataFingerPrint6Response.getMerchantRespErrorCode()+
				dataFingerPrint6Response.getMerchantRespErrorDetail()+dataFingerPrint6Response.getMerchantRespErrorDescription()+
				dataFingerPrint6Response.getMerchantRespMerchantRef()+dataFingerPrint6Response.getMerchantRespMerchantSession()+
				dataFingerPrint6Response.getMerchantRespAdditionalErrorMessage();
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
