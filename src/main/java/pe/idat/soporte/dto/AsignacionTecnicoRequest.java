package pe.idat.soporte.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AsignacionTecnicoRequest(
        @Schema(example = "1")
        @NotNull(message = "El técnico es obligatorio")
        @Positive(message = "El id del técnico debe ser positivo")
        Long tecnicoId
) {
}
