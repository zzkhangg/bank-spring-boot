package com.example.bankspringboot.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI description of the API.
 *
 * <p>Declaring the bearer scheme here is what makes the "Authorize" button in Swagger UI work: paste
 * an access token from {@code POST /api/auth/login} once and every subsequent request from the UI
 * carries it.
 */
@Configuration
public class OpenApiConfig {

  private static final String BEARER_SCHEME = "bearerAuth";

  @Bean
  public OpenAPI bankOpenAPI() {
    return new OpenAPI()
        .info(
            new Info()
                .title("Bank API")
                .version("0.0.1-SNAPSHOT")
                .description(
                    """
                    A banking backend covering customers, accounts, transfers and reporting.

                    Most endpoints require a bearer token. Obtain one from POST /api/auth/login,
                    then use the Authorize button above.
                    """)
                .license(new License().name("MIT").url("https://opensource.org/licenses/MIT")))
        .components(
            new Components()
                .addSecuritySchemes(
                    BEARER_SCHEME,
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")))
        .addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME));
  }
}
