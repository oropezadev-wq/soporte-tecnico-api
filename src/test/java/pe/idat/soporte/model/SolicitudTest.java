package pe.idat.soporte.model;

import org.junit.jupiter.api.Test;
import pe.idat.soporte.exception.ReglaNegocioException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SolicitudTest {

    private final Cliente cliente = new Cliente("María", "maria@x.pe", "987654321", null);
    private final Tecnico tecnico = new Tecnico("Luis", "luis@x.pe", "912345678", Especialidad.REDES);

    private Solicitud nueva() {
        return new Solicitud("No enciende el monitor", "Pantalla negra al prender", Prioridad.MEDIA, null, cliente);
    }

    @Test
    void empiezaAbiertaYSinTecnico() {
        Solicitud s = nueva();

        assertThat(s.getEstado()).isEqualTo(EstadoSolicitud.ABIERTA);
        assertThat(s.getTecnico()).isNull();
        assertThat(s.getFechaCreacion()).isNotNull();
    }

    @Test
    void asignarTecnicoPasaAEnProceso() {
        Solicitud s = nueva();

        s.asignarTecnico(tecnico);

        assertThat(s.getEstado()).isEqualTo(EstadoSolicitud.EN_PROCESO);
        assertThat(s.getTecnico()).isSameAs(tecnico);
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
        s.asignarTecnico(tecnico);

        s.cambiarEstado(EstadoSolicitud.RESUELTA);
        s.cambiarEstado(EstadoSolicitud.CERRADA);

        assertThat(s.getEstado()).isEqualTo(EstadoSolicitud.CERRADA);
        assertThat(s.getEstado().esFinal()).isTrue();
    }

    @Test
    void unaSolicitudCerradaNoSeModifica() {
        Solicitud s = nueva();
        s.asignarTecnico(tecnico);
        s.cambiarEstado(EstadoSolicitud.RESUELTA);
        s.cambiarEstado(EstadoSolicitud.CERRADA);

        assertThatThrownBy(() -> s.actualizar("Otro título", "Otra descripción", Prioridad.BAJA, null))
                .isInstanceOf(ReglaNegocioException.class);
        assertThatThrownBy(() -> s.asignarTecnico(tecnico))
                .isInstanceOf(ReglaNegocioException.class);
    }
}
