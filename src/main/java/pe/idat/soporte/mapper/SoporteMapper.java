package pe.idat.soporte.mapper;

import pe.idat.soporte.dto.ClienteRequest;
import pe.idat.soporte.dto.ClienteResponse;
import pe.idat.soporte.dto.SolicitudResponse;
import pe.idat.soporte.dto.TecnicoRequest;
import pe.idat.soporte.dto.TecnicoResponse;
import pe.idat.soporte.model.Cliente;
import pe.idat.soporte.model.Solicitud;
import pe.idat.soporte.model.Tecnico;

/** Convierte entre entidades y DTOs. Las entidades nunca salen de la capa de servicio. */
public final class SoporteMapper {

    private SoporteMapper() {
    }

    public static Cliente aEntidad(ClienteRequest r) {
        return new Cliente(r.nombre().trim(), r.email().trim(), r.telefono(), r.empresa());
    }

    public static ClienteResponse aResponse(Cliente c) {
        return new ClienteResponse(c.getId(), c.getNombre(), c.getEmail(), c.getTelefono(), c.getEmpresa());
    }

    public static Tecnico aEntidad(TecnicoRequest r) {
        Tecnico tecnico = new Tecnico(r.nombre().trim(), r.email().trim(), r.telefono(), r.especialidad());
        if (Boolean.FALSE.equals(r.activo())) {
            tecnico.actualizar(tecnico.getNombre(), tecnico.getEmail(), tecnico.getTelefono(),
                    tecnico.getEspecialidad(), false);
        }
        return tecnico;
    }

    public static TecnicoResponse aResponse(Tecnico t) {
        return new TecnicoResponse(t.getId(), t.getNombre(), t.getEmail(), t.getTelefono(),
                t.getEspecialidad(), t.isActivo());
    }

    public static SolicitudResponse aResponse(Solicitud s, Cliente cliente, Tecnico tecnico) {
        return new SolicitudResponse(
                s.getId(), s.getTitulo(), s.getDescripcion(), s.getPrioridad(), s.getEstado(),
                s.getClienteId(), cliente == null ? null : cliente.getNombre(),
                s.getTecnicoId(), tecnico == null ? null : tecnico.getNombre(),
                s.getFechaCreacion(), s.getFechaActualizacion());
    }
}
