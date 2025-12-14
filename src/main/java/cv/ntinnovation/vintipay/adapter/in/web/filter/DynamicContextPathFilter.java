package cv.ntinnovation.vintipay.adapter.in.web.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class DynamicContextPathFilter implements Filter {

    private static final String SESSION_TENANT_KEY = "tenant_selecionado";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();

        HttpSession session = httpRequest.getSession(true);
        var tenantIdentifier = (String) session.getAttribute(SESSION_TENANT_KEY);
        log.info("Tenant selecionado na sessão: {}", tenantIdentifier);

        // Determine the dynamic path (e.g., extract "/food" from "/food/search")
        String dynamicContextPath = extractDynamicPath(requestURI, tenantIdentifier);

        if (dynamicContextPath != null) {
            // Wrap the request to override getContextPath() and getServletPath()
            HttpServletRequestWrapper wrappedRequest = new HttpServletRequestWrapper(httpRequest) {
                @Override
                public String getContextPath() {
                    return dynamicContextPath; // e.g., "/food"
                }

                @Override
                public String getServletPath() {
                    // Update servlet path to only include the remainder, e.g., "/search"
                    return requestURI.substring(dynamicContextPath.length());
                }
            };
            chain.doFilter(wrappedRequest, response);
        } else {
            chain.doFilter(request, response);
        }
    }

    private String extractDynamicPath(String requestURI, String tenantIdentifier) {
        // Your logic to determine the context path dynamically
        if (requestURI.startsWith("/"+tenantIdentifier)) return "/"+tenantIdentifier;
        return null; // Use default behavior
    }
}
