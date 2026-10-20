package pe.idat.soporte.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
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
                        + "sus clientes y los técnicos que las atienden. Los datos se guardan en memoria."));
    }
}
