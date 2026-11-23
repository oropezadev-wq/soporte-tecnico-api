package pe.idat.soporte.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.idat.soporte.model.EstadoSolicitud;
import pe.idat.soporte.model.Prioridad;
import pe.idat.soporte.model.Solicitud;

import java.util.List;

/**
 * Acceso a la tabla solicitudes.
 * Las búsquedas que devuelven listas usan {@code @Query} con JPQL y {@code join fetch}
 * para traer cliente y técnico en la misma consulta (sin una consulta extra por fila).
 * Las verificaciones simples usan consultas derivadas del nombre del método.
 */
public interface SolicitudRepositorio extends JpaRepository<Solicitud, Long> {

    /** Filtra por estado y prioridad; un parámetro nulo no filtra. */
    @Query("""
            select s from Solicitud s
            join fetch s.cliente
            left join fetch s.tecnico
            where (:estado is null or s.estado = :estado)
              and (:prioridad is null or s.prioridad = :prioridad)
            order by s.id
            """)
    List<Solicitud> buscar(@Param("estado") EstadoSolicitud estado, @Param("prioridad") Prioridad prioridad);

    @Query("""
            select s from Solicitud s
            join fetch s.cliente
            left join fetch s.tecnico
            where s.cliente.id = :clienteId
            order by s.id
            """)
    List<Solicitud> listarPorCliente(@Param("clienteId") Long clienteId);

    @Query("""
            select s from Solicitud s
            join fetch s.cliente
            join fetch s.tecnico t
            where t.id = :tecnicoId
            order by s.id
            """)
    List<Solicitud> listarPorTecnico(@Param("tecnicoId") Long tecnicoId);

    boolean existsByClienteId(Long clienteId);

    boolean existsByTecnicoId(Long tecnicoId);
}
