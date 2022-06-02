package shuhuai.wheremoney.config;

import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.List;

@EnableOpenApi
@Configuration
public class SwaggerConfig {
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.OAS_30).
                apiInfo(apiInfo())
                .securitySchemes(List.of(tokenScheme()))
                .securityContexts(List.of(tokenContext()))
                .select()
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("钱去哪儿了")
                .description("钱去哪儿了接口文档")
                .contact(new Contact("殊怀丶。", "http://lvshuhuai.cn", "lvzhihe_123@qq.com"))
                .version("1.0")
                .build();
    }

    private HttpAuthenticationScheme tokenScheme() {
        return HttpAuthenticationScheme.JWT_BEARER_BUILDER.name("Authorization").build();
    }

    private SecurityContext tokenContext() {
        return SecurityContext.builder()
                .securityReferences(List.of(SecurityReference.builder()
                        .scopes(new AuthorizationScope[0])
                        .reference("Authorization")
                        .build()))
                .operationSelector(o -> o.requestMappingPattern().matches("/.*"))
                .build();
    }
}