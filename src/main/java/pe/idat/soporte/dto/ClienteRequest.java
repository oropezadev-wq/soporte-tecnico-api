package pe.idat.soporte.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Datos para registrar o actualizar un cliente. */
public record ClienteRequest(
        @Schema(example = "María Quispe")
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre admite como máximo 100 caracteres")
        String nombre,

        @Schema(example = "mquispe@andina.pe")
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no tiene un formato válido")
        String email,

        @Schema(example = "987654321")
        @NotBlank(message = "El teléfono es obligatorio")
        @Pattern(regexp = "9\\d{8}", message = "El teléfono debe tener 9 dígitos y empezar con 9")
        String telefono,

        @Schema(example = "Distribuidora Andina SAC")
        @Size(max = 120, message = "La empresa admite como máximo 120 caracteres")
        String empresa
) {
}
