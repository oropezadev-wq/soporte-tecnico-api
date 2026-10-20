package pe.idat.soporte.service;

import pe.idat.soporte.dto.SolicitudRequest;
import pe.idat.soporte.dto.SolicitudResponse;
import pe.idat.soporte.model.EstadoSolicitud;
import pe.idat.soporte.model.Prioridad;

import java.util.List;

/** Casos de uso de las solicitudes de soporte técnico. */
public interface SolicitudService {

    SolicitudResponse registrar(SolicitudRequest request);

    /** Filtros opcionales: un valor nulo no filtra. */
    List<SolicitudResponse> listar(EstadoSolicitud estado, Prioridad prioridad);

    List<SolicitudResponse> listarPorCliente(Long clienteId);

    List<SolicitudResponse> listarPorTecnico(Long tecnicoId);

    SolicitudResponse obtener(Long id);

    SolicitudResponse actualizar(Long id, SolicitudRequest request);

    SolicitudResponse asignarTecnico(Long id, Long tecnicoId);

    SolicitudResponse cambiarEstado(Long id, EstadoSolicitud estado);

    /** Solo se eliminan solicitudes ABIERTAS o CANCELADAS; las atendidas quedan como historial. */
    void eliminar(Long id);
}
