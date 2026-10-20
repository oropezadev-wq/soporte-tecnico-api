package pe.idat.soporte.dto;

import pe.idat.soporte.model.EstadoSolicitud;
import pe.idat.soporte.model.Prioridad;

import java.time.LocalDateTime;

/** Solicitud con los nombres del cliente y del técnico, para no obligar a hacer otra consulta. */
public record SolicitudResponse(
        Long id,
        String titulo,
        String descripcion,
        Prioridad prioridad,
        EstadoSolicitud estado,
        Long clienteId,
        String clienteNombre,
        Long tecnicoId,
        String tecnicoNombre,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaActualizacion
) {
}
