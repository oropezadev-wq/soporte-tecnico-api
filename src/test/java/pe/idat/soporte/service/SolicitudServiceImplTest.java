package pe.idat.soporte.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import pe.idat.soporte.dto.SolicitudRequest;
import pe.idat.soporte.dto.SolicitudResponse;
import pe.idat.soporte.exception.RecursoNoEncontradoException;
import pe.idat.soporte.exception.ReglaNegocioException;
import pe.idat.soporte.model.Cliente;
import pe.idat.soporte.model.Especialidad;
import pe.idat.soporte.model.EstadoSolicitud;
import pe.idat.soporte.model.Prioridad;
import pe.idat.soporte.model.Tecnico;
import pe.idat.soporte.repository.ClienteRepositorio;
import pe.idat.soporte.repository.SolicitudRepositorio;
import pe.idat.soporte.repository.TecnicoRepositorio;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Prueba la lógica del servicio contra PostgreSQL (base soporte_test).
 * {@code @Transactional} en la prueba deshace todo al terminar cada método,
 * así cada prueba parte de la misma base.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SolicitudServiceImplTest {

    @Autowired
    private ClienteRepositorio clientes;

    @Autowired
    private TecnicoRepositorio tecnicos;

    @Autowired
    private SolicitudRepositorio solicitudes;

    @Autowired
    private SolicitudService servicio;

    private Long clienteId;

    @BeforeEach
    void preparar() {
        clienteId = clientes.save(new Cliente("María", "maria@x.pe", "987654321", null)).getId();
    }

    private SolicitudRequest request(Long cliente) {
        return new SolicitudRequest("Falla de red", "No hay internet en el piso 2", Prioridad.ALTA, null, cliente);
    }

    @Test
    void registraConDatosDelClienteYCosto() {
        SolicitudResponse r = servicio.registrar(new SolicitudRequest("Falla de red", "No hay internet",
                Prioridad.ALTA, new BigDecimal("120.50"), clienteId));

        assertThat(r.id()).isNotNull();
        assertThat(r.estado()).isEqualTo(EstadoSolicitud.ABIERTA);
        assertThat(r.clienteNombre()).isEqualTo("María");
        assertThat(solicitudes.findById(r.id()).orElseThrow().getCosto()).isEqualByComparingTo("120.50");
    }

    @Test
    void rechazaClienteInexistenteSinGuardarNada() {
        long antes = solicitudes.count();

        assertThatThrownBy(() -> servicio.registrar(request(99_999L)))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessage("Cliente con id 99999 no existe");
        assertThat(solicitudes.count()).isEqualTo(antes);
    }

    @Test
    void noAsignaTecnicoInactivo() {
        Tecnico inactivo = new Tecnico("Pedro", "pedro@x.pe", "912345678", Especialidad.REDES);
        inactivo.actualizar("Pedro", "pedro@x.pe", "912345678", Especialidad.REDES, false);
        Long tecnicoId = tecnicos.save(inactivo).getId();
        Long id = servicio.registrar(request(clienteId)).id();

        assertThatThrownBy(() -> servicio.asignarTecnico(id, tecnicoId))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("inactivo");
    }

    @Test
    void filtraPorEstadoYPrioridad() {
        Long tecnicoId = tecnicos.save(new Tecnico("Ana", "ana@x.pe", "912345678", Especialidad.REDES)).getId();
        Long atendida = servicio.registrar(request(clienteId)).id();
        Long baja = servicio.registrar(new SolicitudRequest("Mouse roto", "No responde el clic",
                Prioridad.BAJA, null, clienteId)).id();
        servicio.asignarTecnico(atendida, tecnicoId);

        assertThat(servicio.listar(EstadoSolicitud.EN_PROCESO, null)).extracting(SolicitudResponse::id)
                .contains(atendida).doesNotContain(baja);
        assertThat(servicio.listar(null, Prioridad.BAJA)).extracting(SolicitudResponse::id)
                .contains(baja).doesNotContain(atendida);
        assertThat(servicio.listar(EstadoSolicitud.ABIERTA, Prioridad.ALTA)).extracting(SolicitudResponse::id)
                .doesNotContain(atendida, baja);
    }

    @Test
    void noEliminaSolicitudesAtendidas() {
        Long tecnicoId = tecnicos.save(new Tecnico("Ana", "ana@x.pe", "912345678", Especialidad.REDES)).getId();
        Long id = servicio.registrar(request(clienteId)).id();
        servicio.asignarTecnico(id, tecnicoId);

        assertThatThrownBy(() -> servicio.eliminar(id))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("EN_PROCESO");
    }

    @Test
    void noPermiteCambiarDeCliente() {
        Long otroCliente = clientes.save(new Cliente("Jorge", "jorge@x.pe", "956123478", null)).getId();
        Long id = servicio.registrar(request(clienteId)).id();

        assertThatThrownBy(() -> servicio.actualizar(id, request(otroCliente)))
                .isInstanceOf(ReglaNegocioException.class);
    }
}
