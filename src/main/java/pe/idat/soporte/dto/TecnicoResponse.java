package pe.idat.soporte.dto;

import pe.idat.soporte.model.Especialidad;

public record TecnicoResponse(
        Long id,
        String nombre,
        String email,
        String telefono,
        Especialidad especialidad,
        boolean activo
) {
}
