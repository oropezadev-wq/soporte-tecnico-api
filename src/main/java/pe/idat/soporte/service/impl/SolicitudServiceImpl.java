package pe.idat.soporte.service.impl;

import org.springframework.stereotype.Service;
import pe.idat.soporte.dto.SolicitudRequest;
import pe.idat.soporte.dto.SolicitudResponse;
import pe.idat.soporte.exception.RecursoNoEncontradoException;
import pe.idat.soporte.exception.ReglaNegocioException;
import pe.idat.soporte.mapper.SoporteMapper;
import pe.idat.soporte.model.EstadoSolicitud;
import pe.idat.soporte.model.Prioridad;
import pe.idat.soporte.model.Solicitud;
import pe.idat.soporte.model.Tecnico;
import pe.idat.soporte.repository.ClienteRepositorio;
import pe.idat.soporte.repository.SolicitudRepositorio;
import pe.idat.soporte.repository.TecnicoRepositorio;
import pe.idat.soporte.service.SolicitudService;

import java.util.List;

@Service
public class SolicitudServiceImpl implements SolicitudService {

    private final SolicitudRepositorio solicitudes;
    private final ClienteRepositorio clientes;
    private final TecnicoRepositorio tecnicos;

    public SolicitudServiceImpl(SolicitudRepositorio solicitudes, ClienteRepositorio clientes,
                                TecnicoRepositorio tecnicos) {
        this.solicitudes = solicitudes;
        this.clientes = clientes;
        this.tecnicos = tecnicos;
    }

    @Override
    public SolicitudResponse registrar(SolicitudRequest request) {
        if (!clientes.existePorId(request.clienteId())) {
            throw new RecursoNoEncontradoException("Cliente", request.clienteId());
        }
        Solicitud solicitud = new Solicitud(request.titulo().trim(), request.descripcion().trim(),
                request.prioridad(), request.clienteId());
        return aResponse(solicitudes.guardar(solicitud));
    }

    @Override
    public List<SolicitudResponse> listar(EstadoSolicitud estado, Prioridad prioridad) {
        return solicitudes.buscar(estado, prioridad).stream().map(this::aResponse).toList();
    }

    @Override
    public List<SolicitudResponse> listarPorCliente(Long clienteId) {
        if (!clientes.existePorId(clienteId)) {
            throw new RecursoNoEncontradoException("Cliente", clienteId);
        }
        return solicitudes.listarPorCliente(clienteId).stream().map(this::aResponse).toList();
    }

    @Override
    public List<SolicitudResponse> listarPorTecnico(Long tecnicoId) {
        if (!tecnicos.existePorId(tecnicoId)) {
            throw new RecursoNoEncontradoException("Técnico", tecnicoId);
        }
        return solicitudes.listarPorTecnico(tecnicoId).stream().map(this::aResponse).toList();
    }

    @Override
    public SolicitudResponse obtener(Long id) {
        return aResponse(buscar(id));
    }

    @Override
    public SolicitudResponse actualizar(Long id, SolicitudRequest request) {
        Solicitud solicitud = buscar(id);
        if (!solicitud.getClienteId().equals(request.clienteId())) {
            throw new ReglaNegocioException("Una solicitud no puede cambiar de cliente");
        }
        solicitud.actualizar(request.titulo().trim(), request.descripcion().trim(), request.prioridad());
        return aResponse(solicitudes.guardar(solicitud));
    }

    @Override
    public SolicitudResponse asignarTecnico(Long id, Long tecnicoId) {
        Solicitud solicitud = buscar(id);
        Tecnico tecnico = tecnicos.buscarPorId(tecnicoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Técnico", tecnicoId));
        if (!tecnico.isActivo()) {
            throw new ReglaNegocioException("El técnico " + tecnicoId + " está inactivo");
        }
        solicitud.asignarTecnico(tecnicoId);
        return aResponse(solicitudes.guardar(solicitud));
    }

    @Override
    public SolicitudResponse cambiarEstado(Long id, EstadoSolicitud estado) {
        Solicitud solicitud = buscar(id);
        solicitud.cambiarEstado(estado);
        return aResponse(solicitudes.guardar(solicitud));
    }

    @Override
    public void eliminar(Long id) {
        Solicitud solicitud = buscar(id);
        EstadoSolicitud estado = solicitud.getEstado();
        if (estado != EstadoSolicitud.ABIERTA && estado != EstadoSolicitud.CANCELADA) {
            throw new ReglaNegocioException("Solo se pueden eliminar solicitudes ABIERTAS o CANCELADAS; esta está " + estado);
        }
        solicitudes.eliminarPorId(id);
    }

    private Solicitud buscar(Long id) {
        return solicitudes.buscarPorId(id).orElseThrow(() -> new RecursoNoEncontradoException("Solicitud", id));
    }

    private SolicitudResponse aResponse(Solicitud s) {
        return SoporteMapper.aResponse(s,
                clientes.buscarPorId(s.getClienteId()).orElse(null),
                s.getTecnicoId() == null ? null : tecnicos.buscarPorId(s.getTecnicoId()).orElse(null));
    }
}
