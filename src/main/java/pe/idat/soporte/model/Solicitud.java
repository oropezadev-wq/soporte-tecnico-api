package pe.idat.soporte.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pe.idat.soporte.exception.ReglaNegocioException;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Pedido de atención técnica de un cliente. Tabla {@code soporte.solicitudes}.
 * El estado solo cambia mediante {@link #cambiarEstado} y {@link #asignarTecnico},
 * que validan las reglas del negocio; no hay setters públicos.
 */
@Entity
@Table(name = "solicitudes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Solicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String titulo;

    @Column(nullable = false, length = 1000)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Prioridad prioridad;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoSolicitud estado;

    /** Costo estimado de la atención en soles; opcional. */
    @Column(precision = 14, scale = 2)
    private BigDecimal costo;

    /** Muchas solicitudes pertenecen a un cliente (columna cliente_id). */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    /** Técnico asignado; es nulo mientras nadie la atiende. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tecnico_id")
    private Tecnico tecnico;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;

    /**
     * Bloqueo optimista: Hibernate suma 1 en cada UPDATE y, si otra petición ya
     * modificó la fila, rechaza el cambio en lugar de pisarlo.
     */
    @Version
    @Column(nullable = false)
    private Long version;

    public Solicitud(String titulo, String descripcion, Prioridad prioridad, BigDecimal costo, Cliente cliente) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.prioridad = prioridad;
        this.costo = costo;
        this.cliente = cliente;
        this.estado = EstadoSolicitud.ABIERTA;
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = this.fechaCreacion;
    }

    /** Modifica el contenido mientras la solicitud no esté cerrada o cancelada. */
    public void actualizar(String titulo, String descripcion, Prioridad prioridad, BigDecimal costo) {
        validarQueNoEsteFinalizada();
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.prioridad = prioridad;
        this.costo = costo;
        marcarActualizada();
    }

    /** Asigna un técnico; si la solicitud estaba abierta, pasa a EN_PROCESO. */
    public void asignarTecnico(Tecnico tecnico) {
        validarQueNoEsteFinalizada();
        this.tecnico = tecnico;
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
        if (nuevo == EstadoSolicitud.EN_PROCESO && tecnico == null) {
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
}
