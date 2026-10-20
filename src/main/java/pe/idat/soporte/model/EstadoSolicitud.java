package pe.idat.soporte.model;

import java.util.Set;

/**
 * Ciclo de vida de una solicitud. Cada estado conoce a qué estados puede pasar,
 * así la regla vive en un solo lugar y no se repite en los servicios.
 */
public enum EstadoSolicitud {
    ABIERTA,
    EN_PROCESO,
    RESUELTA,
    CERRADA,
    CANCELADA;

    public boolean puedeCambiarA(EstadoSolicitud destino) {
        return siguientes().contains(destino);
    }

    public boolean esFinal() {
        return siguientes().isEmpty();
    }

    private Set<EstadoSolicitud> siguientes() {
        return switch (this) {
            case ABIERTA -> Set.of(EN_PROCESO, CANCELADA);
            case EN_PROCESO -> Set.of(RESUELTA, CANCELADA);
            case RESUELTA -> Set.of(CERRADA, EN_PROCESO);
            case CERRADA, CANCELADA -> Set.of();
        };
    }
}
