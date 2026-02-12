package shuhuai.wheremoney.config;

import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import shuhuai.wheremoney.utils.TokenValidator;

@Configuration
public class AuthWebConfig implements WebMvcConfigurer {
    @Resource
    TokenValidator tokenValidator;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenValidator)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/test/generate-token")
                .excludePathPatterns("/api/user/login")
                .excludePathPatterns("/api/user/register");
    }
}