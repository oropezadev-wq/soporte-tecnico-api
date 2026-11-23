package pe.idat.soporte.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.idat.soporte.dto.SolicitudRequest;
import pe.idat.soporte.dto.SolicitudResponse;
import pe.idat.soporte.exception.RecursoNoEncontradoException;
import pe.idat.soporte.exception.ReglaNegocioException;
import pe.idat.soporte.mapper.SoporteMapper;
import pe.idat.soporte.model.Cliente;
import pe.idat.soporte.model.EstadoSolicitud;
import pe.idat.soporte.model.Prioridad;
import pe.idat.soporte.model.Solicitud;
import pe.idat.soporte.model.Tecnico;
import pe.idat.soporte.repository.ClienteRepositorio;
import pe.idat.soporte.repository.SolicitudRepositorio;
import pe.idat.soporte.repository.TecnicoRepositorio;
import pe.idat.soporte.service.SolicitudService;

import java.util.List;

/**
 * Reglas de negocio de las solicitudes.
 * Cada método que escribe corre en una transacción con rollback ante cualquier
 * RuntimeException: si una regla falla a mitad de camino, la base queda como estaba.
 * La conversión a DTO se hace dentro de la transacción para poder leer el cliente
 * y el técnico, que se cargan de forma diferida (LAZY).
 */
@Service
@Transactional(rollbackFor = RuntimeException.class)
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
        Cliente cliente = clientes.findById(request.clienteId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente", request.clienteId()));
        Solicitud solicitud = new Solicitud(request.titulo().trim(), request.descripcion().trim(),
                request.prioridad(), request.costo(), cliente);
        return SoporteMapper.aResponse(solicitudes.save(solicitud));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SolicitudResponse> listar(EstadoSolicitud estado, Prioridad prioridad) {
        return solicitudes.buscar(estado, prioridad).stream().map(SoporteMapper::aResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SolicitudResponse> listarPorCliente(Long clienteId) {
        if (!clientes.existsById(clienteId)) {
            throw new RecursoNoEncontradoException("Cliente", clienteId);
        }
        return solicitudes.listarPorCliente(clienteId).stream().map(SoporteMapper::aResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SolicitudResponse> listarPorTecnico(Long tecnicoId) {
        if (!tecnicos.existsById(tecnicoId)) {
            throw new RecursoNoEncontradoException("Técnico", tecnicoId);
        }
        return solicitudes.listarPorTecnico(tecnicoId).stream().map(SoporteMapper::aResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SolicitudResponse obtener(Long id) {
        return SoporteMapper.aResponse(buscar(id));
    }

    @Override
    public SolicitudResponse actualizar(Long id, SolicitudRequest request) {
        Solicitud solicitud = buscar(id);
        if (!solicitud.getCliente().getId().equals(request.clienteId())) {
            throw new ReglaNegocioException("Una solicitud no puede cambiar de cliente");
        }
        solicitud.actualizar(request.titulo().trim(), request.descripcion().trim(),
                request.prioridad(), request.costo());
        return SoporteMapper.aResponse(solicitudes.saveAndFlush(solicitud));
    }

    @Override
    public SolicitudResponse asignarTecnico(Long id, Long tecnicoId) {
        Solicitud solicitud = buscar(id);
        Tecnico tecnico = tecnicos.findById(tecnicoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Técnico", tecnicoId));
        if (!tecnico.isActivo()) {
            throw new ReglaNegocioException("El técnico " + tecnicoId + " está inactivo");
        }
        solicitud.asignarTecnico(tecnico);
        return SoporteMapper.aResponse(solicitudes.saveAndFlush(solicitud));
    }

    @Override
    public SolicitudResponse cambiarEstado(Long id, EstadoSolicitud estado) {
        Solicitud solicitud = buscar(id);
        solicitud.cambiarEstado(estado);
        return SoporteMapper.aResponse(solicitudes.saveAndFlush(solicitud));
    }

    @Override
    public void eliminar(Long id) {
        Solicitud solicitud = buscar(id);
        EstadoSolicitud estado = solicitud.getEstado();
        if (estado != EstadoSolicitud.ABIERTA && estado != EstadoSolicitud.CANCELADA) {
            throw new ReglaNegocioException("Solo se pueden eliminar solicitudes ABIERTAS o CANCELADAS; esta está " + estado);
        }
        solicitudes.delete(solicitud);
    }

    private Solicitud buscar(Long id) {
        return solicitudes.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Solicitud", id));
    }
}
