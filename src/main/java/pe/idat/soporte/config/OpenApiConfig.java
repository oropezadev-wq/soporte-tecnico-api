package pe.idat.soporte.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Datos generales que muestra Swagger UI en /swagger-ui.html. */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI documentacion() {
        return new OpenAPI().info(new Info()
                .title("API de Solicitudes de Soporte Técnico")
                .version("1.0.0")
                .description("Registra, consulta, actualiza y elimina solicitudes de soporte técnico, "
                        + "sus clientes y los técnicos que las atienden. Los datos se guardan en memoria.")
                .contact(new Contact()
                        .name("Equipo Soporte Técnico - IDAT")
                        .email("oropeza.dev@gmail.com")
                        .url("https://github.com/oropezadev-wq/soporte-tecnico-api"))
                .license(new License()
                        .name("MIT")
                        .url("https://opensource.org/licenses/MIT")));
    }
}
