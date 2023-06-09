package cart.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(servers = {
        @Server(url = "http://localhost:8080", description = "로컬 서버"),
        @Server(url = "https://dev-king-ethan.n-e.kr", description = "운영 서버")
})
@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        final Info info = new Info()
                .version("v1.0.0")
                .title("장바구니 API")
                .description("장바구니 API 명세");

        final SecurityScheme basicAuth = new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("basic");

        return new OpenAPI()
                .components(new Components().addSecuritySchemes("basicAuth", basicAuth))
                .info(info);
    }
}
