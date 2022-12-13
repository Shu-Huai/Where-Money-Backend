package shuhuai.wheremoney.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfig {
    private static final String basePackage = "shuhuai.wheremoney.controller";

    @Bean
    public GroupedOpenApi usersGroup() {
        return GroupedOpenApi.builder()
                .group("users")
                .addOperationCustomizer((operation, handlerMethod) -> {
                    operation.addSecurityItem(new SecurityRequirement().addList("Authorization"));
                    return operation;
                })
                .packagesToScan(basePackage)
                .build();
    }

    @Bean
    public OpenAPI customOpenAPI() {
        Components components = new Components();
        components.addSecuritySchemes("Authorization",
                new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .name("Authorization")
                        .in(SecurityScheme.In.HEADER)
                        .description("请求头")
        );
        return new OpenAPI()
                .components(components)
                .info(apiInfo());
    }

    private Info apiInfo() {
        Contact contact = new Contact();
        contact.setEmail("lvzhihe_123@qq.com");
        contact.setName("殊怀丶");
        contact.setUrl("http://lvshuhuai.cn");
        return new Info()
                .title("钱去哪儿了")
                .version("1.0")
                .contact(contact)
                .description("钱去哪儿了")
                .license(new License().name("Apache 2.0").url("http://springdoc.org"));
    }
}