package pe.idat.soporte.repository;

import org.springframework.stereotype.Repository;
import pe.idat.soporte.model.EstadoSolicitud;
import pe.idat.soporte.model.Prioridad;
import pe.idat.soporte.model.Solicitud;

import java.util.List;

@Repository
public class SolicitudRepositorio extends RepositorioEnMemoria<Solicitud> {

    /** Filtra por estado y prioridad; un parámetro nulo no filtra. */
    public List<Solicitud> buscar(EstadoSolicitud estado, Prioridad prioridad) {
        return filtrar(s -> (estado == null || s.getEstado() == estado)
                && (prioridad == null || s.getPrioridad() == prioridad));
    }

    public List<Solicitud> listarPorCliente(Long clienteId) {
        return filtrar(s -> s.getClienteId().equals(clienteId));
    }

    public List<Solicitud> listarPorTecnico(Long tecnicoId) {
        return filtrar(s -> tecnicoId.equals(s.getTecnicoId()));
    }

    public boolean existenPorCliente(Long clienteId) {
        return !listarPorCliente(clienteId).isEmpty();
    }

    public boolean existenPorTecnico(Long tecnicoId) {
        return !listarPorTecnico(tecnicoId).isEmpty();
    }
}
