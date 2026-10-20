package pe.idat.soporte.model;

import org.junit.jupiter.api.Test;
import pe.idat.soporte.exception.ReglaNegocioException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SolicitudTest {

    private Solicitud nueva() {
        return new Solicitud("No enciende el monitor", "Pantalla negra al prender", Prioridad.MEDIA, 1L);
    }

    @Test
    void empiezaAbiertaYSinTecnico() {
        Solicitud s = nueva();

        assertThat(s.getEstado()).isEqualTo(EstadoSolicitud.ABIERTA);
        assertThat(s.getTecnicoId()).isNull();
        assertThat(s.getFechaCreacion()).isNotNull();
    }

    @Test
    void asignarTecnicoPasaAEnProceso() {
        Solicitud s = nueva();

        s.asignarTecnico(7L);

        assertThat(s.getEstado()).isEqualTo(EstadoSolicitud.EN_PROCESO);
        assertThat(s.getTecnicoId()).isEqualTo(7L);
    }

    @Test
    void noPuedeIniciarAtencionSinTecnico() {
        Solicitud s = nueva();

        assertThatThrownBy(() -> s.cambiarEstado(EstadoSolicitud.EN_PROCESO))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("técnico");
    }

    @Test
    void noPuedeSaltarDeAbiertaACerrada() {
        Solicitud s = nueva();

        assertThatThrownBy(() -> s.cambiarEstado(EstadoSolicitud.CERRADA))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("ABIERTA a CERRADA");
    }

    @Test
    void cicloCompletoHastaCerrada() {
        Solicitud s = nueva();
        s.asignarTecnico(7L);

        s.cambiarEstado(EstadoSolicitud.RESUELTA);
        s.cambiarEstado(EstadoSolicitud.CERRADA);

        assertThat(s.getEstado()).isEqualTo(EstadoSolicitud.CERRADA);
        assertThat(s.getEstado().esFinal()).isTrue();
    }

    @Test
    void unaSolicitudCerradaNoSeModifica() {
        Solicitud s = nueva();
        s.asignarTecnico(7L);
        s.cambiarEstado(EstadoSolicitud.RESUELTA);
        s.cambiarEstado(EstadoSolicitud.CERRADA);

        assertThatThrownBy(() -> s.actualizar("Otro título", "Otra descripción", Prioridad.BAJA))
                .isInstanceOf(ReglaNegocioException.class);
        assertThatThrownBy(() -> s.asignarTecnico(8L))
                .isInstanceOf(ReglaNegocioException.class);
    }
}
