package shuhuai.wheremoney.utils;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import shuhuai.wheremoney.response.Response;

@ControllerAdvice
@Slf4j
public class Logger implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(@NotNull MethodParameter methodParameter, @NotNull Class<? extends HttpMessageConverter<?>> aClass) {
        return true;
    }

    @Nullable
    @Override
    public Object beforeBodyWrite(@Nullable Object body, @NotNull MethodParameter methodParameter, @NotNull MediaType mediaType,
                                  @NotNull Class<? extends HttpMessageConverter<?>> aClass, @NotNull ServerHttpRequest serverHttpRequest,
                                  @NotNull ServerHttpResponse serverHttpResponse) {
        if (body instanceof Response<?> response) {
            if (response.getCode() == 200) {
                log.info(RequestGetter.getRequestUrl() + "：" + response.getMessage());
            } else {
                log.error(RequestGetter.getRequestUrl() + "：" + response.getMessage());
            }
        }
        return body;
    }
}