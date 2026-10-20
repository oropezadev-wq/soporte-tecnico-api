package pe.idat.soporte.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import pe.idat.soporte.model.EstadoSolicitud;

public record CambioEstadoRequest(
        @Schema(example = "RESUELTA")
        @NotNull(message = "El estado es obligatorio")
        EstadoSolicitud estado
) {
}
