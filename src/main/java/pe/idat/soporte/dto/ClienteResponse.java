package pe.idat.soporte.dto;

public record ClienteResponse(
        Long id,
        String nombre,
        String email,
        String telefono,
        String empresa
) {
}
