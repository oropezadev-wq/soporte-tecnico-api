package pe.idat.soporte.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import pe.idat.soporte.model.Especialidad;

/** Datos para registrar o actualizar un técnico. {@code activo} es opcional y vale true por defecto. */
public record TecnicoRequest(
        @Schema(example = "Luis Ramírez")
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre admite como máximo 100 caracteres")
        String nombre,

        @Schema(example = "lramirez@soporte.pe")
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no tiene un formato válido")
        String email,

        @Schema(example = "912345678")
        @NotBlank(message = "El teléfono es obligatorio")
        @Pattern(regexp = "9\\d{8}", message = "El teléfono debe tener 9 dígitos y empezar con 9")
        String telefono,

        @Schema(example = "REDES")
        @NotNull(message = "La especialidad es obligatoria")
        Especialidad especialidad,

        @Schema(example = "true")
        Boolean activo
) {
}
