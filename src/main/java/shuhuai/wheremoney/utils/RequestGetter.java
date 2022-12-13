package shuhuai.wheremoney.utils;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class RequestGetter {
    public static HttpServletRequest getRequest() {
        return getRequestAttributes().getRequest();
    }

    public static HttpServletResponse getResponse() {
        return getRequestAttributes().getResponse();
    }

    public static String getRequestUrl() {
        HttpServletRequest request = RequestGetter.getRequest();
        String url = request.getScheme() + "://" + request.getServerName() + (request.getServerPort() == 80 ? "" : ":" + request.getServerPort()) + request.getContextPath()
                + request.getRequestURI() + (request.getQueryString() == null ? "" : (request.getQueryString().equals("") ? "" : "?" + request.getQueryString()));
        return java.net.URLDecoder.decode(url, StandardCharsets.UTF_8);
    }

    public static HttpSession getSession() {
        return getRequest().getSession();
    }

    public static ServletRequestAttributes getRequestAttributes() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
    }

    public static ServletContext getServletContext() {
        return Objects.requireNonNull(ContextLoader.getCurrentWebApplicationContext()).getServletContext();
    }
}