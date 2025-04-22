package cv.ntinnovation.vintipay.application.port.out;

import java.util.Map;

public interface GeradorPaginaTemplate {
	
	public String geraHTML(String name, Object object) throws Exception;
	
	public String geraHTML(String name, Map<String, Object> object) throws Exception;

}
