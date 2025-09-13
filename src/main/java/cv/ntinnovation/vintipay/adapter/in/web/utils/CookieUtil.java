package cv.ntinnovation.vintipay.adapter.in.web.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

public class CookieUtil {
    public static final String SESSION_COOKIE_NAME = "PAYMENT_SESSION_ID";
    public static final String CLIENT_ID = "CLIENT_ID";
    public static final int SESSION_COOKIE_MAX_AGE = 60 * 60; // 1 hora

    public static void setSessionCookie(HttpServletResponse response, String sessionId) {
        CookieUtil.setCookie(response, SESSION_COOKIE_NAME, sessionId, SESSION_COOKIE_MAX_AGE);
    }

    public static void setClientIdCookie(HttpServletResponse response, String clientId) {
        CookieUtil.setCookie(response, CLIENT_ID, clientId, SESSION_COOKIE_MAX_AGE);
    }

    private static void setCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAge);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        response.addCookie(cookie);
    }

    private static void clearCookie(HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, "");
        cookie.setMaxAge(0);      // remove
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    public static void clearSessionCookie(HttpServletResponse response) {
        CookieUtil.clearCookie(response, SESSION_COOKIE_NAME);
    }

    public static void clearClientIdCookie(HttpServletResponse response) {
        CookieUtil.clearCookie(response, CLIENT_ID);
    }

    public static void clearAllCookies(HttpServletResponse response) {
        CookieUtil.clearSessionCookie(response);
        CookieUtil.clearClientIdCookie(response);
    }
}
