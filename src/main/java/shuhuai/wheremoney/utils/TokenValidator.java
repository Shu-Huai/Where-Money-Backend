package shuhuai.wheremoney.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import shuhuai.wheremoney.service.excep.common.TokenExpireException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Component
public class TokenValidator implements HandlerInterceptor {
    private final static ThreadLocal<Map<String, String>> threadLocal = new ThreadLocal<>();
    @Value("${token.privateKey}")
    private String privateKey;
    @Value("${token.youngToken}")
    private Long youngToken;
    @Value("${token.oldToken}")
    private Long oldToken;

    public static Map<String, String> getUser() {
        return threadLocal.get();
    }

    public static void setUser(Map<String, String> userIdentify) {
        threadLocal.set(userIdentify);
    }

    public static void removeUser() {
        threadLocal.remove();
    }

    public String getToken(String userName) {
        return JWT.create().withClaim("userName", userName).withClaim("timeStamp", System.currentTimeMillis()).sign(Algorithm.HMAC256(privateKey));
    }

    public Map<String, String> parseToken(String token) {
        HashMap<String, String> map = new HashMap<>();
        DecodedJWT decodedjwt = JWT.require(Algorithm.HMAC256(privateKey)).build().verify(token);
        Claim userName = decodedjwt.getClaim("userName");
        Claim timeStamp = decodedjwt.getClaim("timeStamp");
        map.put("userName", userName.asString());
        map.put("timeStamp", timeStamp.asLong().toString());
        return map;
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest httpServletRequest, @NonNull HttpServletResponse httpServletResponse, @NonNull Object object) {
        if (!(object instanceof HandlerMethod)) {
            return true;
        }
        String token = httpServletRequest.getHeader("Authorization");
        if (null == token || "".equals(token.trim())) {
            throw new TokenExpireException("token无效");
        }
        Map<String, String> map;
        try {
            token = token.split(" ")[1];
            map = parseToken(token);
        } catch (Exception e) {
            throw new TokenExpireException("token无效");
        }
        String userName = map.get("userName");
        long timeOfUse = System.currentTimeMillis() - Long.parseLong(map.get("timeStamp"));
        if (timeOfUse >= youngToken && timeOfUse < oldToken) {
            httpServletResponse.setHeader("Authorization", "Bearer " + getToken(userName));
        } else if (timeOfUse >= oldToken) {
            throw new TokenExpireException("token过期");
        }
        setUser(map);
        return true;
    }
}