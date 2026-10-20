package pe.idat.soporte.model;

import pe.idat.soporte.exception.ReglaNegocioException;

import java.time.LocalDateTime;

/**
 * Pedido de atención técnica de un cliente.
 * El estado solo cambia mediante {@link #cambiarEstado} y {@link #asignarTecnico},
 * que validan las reglas del negocio; no hay setters públicos para el estado.
 */
public class Solicitud implements Identificable {

    private Long id;
    private String titulo;
    private String descripcion;
    private Prioridad prioridad;
    private EstadoSolicitud estado;
    private final Long clienteId;
    private Long tecnicoId;
    private final LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    public Solicitud(String titulo, String descripcion, Prioridad prioridad, Long clienteId) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.prioridad = prioridad;
        this.clienteId = clienteId;
        this.estado = EstadoSolicitud.ABIERTA;
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = this.fechaCreacion;
    }

    /** Modifica el contenido mientras la solicitud no esté cerrada o cancelada. */
    public void actualizar(String titulo, String descripcion, Prioridad prioridad) {
        validarQueNoEsteFinalizada();
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.prioridad = prioridad;
        marcarActualizada();
    }

    /** Asigna un técnico; si la solicitud estaba abierta, pasa a EN_PROCESO. */
    public void asignarTecnico(Long tecnicoId) {
        validarQueNoEsteFinalizada();
        this.tecnicoId = tecnicoId;
        if (estado == EstadoSolicitud.ABIERTA) {
            estado = EstadoSolicitud.EN_PROCESO;
        }
        marcarActualizada();
    }

    public void cambiarEstado(EstadoSolicitud nuevo) {
        if (!estado.puedeCambiarA(nuevo)) {
            throw new ReglaNegocioException(
                    "No se puede pasar una solicitud de " + estado + " a " + nuevo);
        }
        if (nuevo == EstadoSolicitud.EN_PROCESO && tecnicoId == null) {
            throw new ReglaNegocioException("Asigne un técnico antes de iniciar la atención");
        }
        this.estado = nuevo;
        marcarActualizada();
    }

    private void validarQueNoEsteFinalizada() {
        if (estado.esFinal()) {
            throw new ReglaNegocioException("La solicitud está " + estado + " y ya no se puede modificar");
        }
    }

    private void marcarActualizada() {
        this.fechaActualizacion = LocalDateTime.now();
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Prioridad getPrioridad() {
        return prioridad;
    }

    public EstadoSolicitud getEstado() {
        return estado;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public Long getTecnicoId() {
        return tecnicoId;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }
}
