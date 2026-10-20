package pe.idat.soporte.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import pe.idat.soporte.model.Prioridad;

/**
 * Datos para registrar una solicitud. Al actualizar, {@code clienteId} debe coincidir
 * con el original porque una solicitud no cambia de cliente.
 */
public record SolicitudRequest(
        @Schema(example = "La impresora del área contable no imprime")
        @NotBlank(message = "El título es obligatorio")
        @Size(min = 5, max = 120, message = "El título debe tener entre 5 y 120 caracteres")
        String titulo,

        @Schema(example = "Desde ayer muestra el error de papel atascado aunque la bandeja está vacía.")
        @NotBlank(message = "La descripción es obligatoria")
        @Size(max = 1000, message = "La descripción admite como máximo 1000 caracteres")
        String descripcion,

        @Schema(example = "ALTA")
        @NotNull(message = "La prioridad es obligatoria")
        Prioridad prioridad,

        @Schema(example = "1")
        @NotNull(message = "El cliente es obligatorio")
        @Positive(message = "El id del cliente debe ser positivo")
        Long clienteId
) {
}
