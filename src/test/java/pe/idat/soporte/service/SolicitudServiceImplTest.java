package pe.idat.soporte.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import pe.idat.soporte.service.impl.SolicitudServiceImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Prueba la lógica del servicio con repositorios reales en memoria, sin levantar Spring. */
class SolicitudServiceImplTest {

    private ClienteRepositorio clientes;
    private TecnicoRepositorio tecnicos;
    private SolicitudService servicio;
    private Long clienteId;

    @BeforeEach
    void preparar() {
        clientes = new ClienteRepositorio();
        tecnicos = new TecnicoRepositorio();
        servicio = new SolicitudServiceImpl(new SolicitudRepositorio(), clientes, tecnicos);
        clienteId = clientes.guardar(new Cliente("María", "maria@x.pe", "987654321", null)).getId();
    }

    private SolicitudRequest request(Long cliente) {
        return new SolicitudRequest("Falla de red", "No hay internet en el piso 2", Prioridad.ALTA, cliente);
    }

    @Test
    void registraConDatosDelCliente() {
        SolicitudResponse r = servicio.registrar(request(clienteId));

        assertThat(r.id()).isNotNull();
        assertThat(r.estado()).isEqualTo(EstadoSolicitud.ABIERTA);
        assertThat(r.clienteNombre()).isEqualTo("María");
    }

    @Test
    void rechazaClienteInexistente() {
        assertThatThrownBy(() -> servicio.registrar(request(99L)))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessage("Cliente con id 99 no existe");
    }

    @Test
    void noAsignaTecnicoInactivo() {
        Tecnico inactivo = new Tecnico("Pedro", "pedro@x.pe", "912345678", Especialidad.REDES);
        inactivo.actualizar("Pedro", "pedro@x.pe", "912345678", Especialidad.REDES, false);
        Long tecnicoId = tecnicos.guardar(inactivo).getId();
        Long id = servicio.registrar(request(clienteId)).id();

        assertThatThrownBy(() -> servicio.asignarTecnico(id, tecnicoId))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("inactivo");
    }

    @Test
    void filtraPorEstadoYPrioridad() {
        Long tecnicoId = tecnicos.guardar(new Tecnico("Ana", "ana@x.pe", "912345678", Especialidad.REDES)).getId();
        Long atendida = servicio.registrar(request(clienteId)).id();
        servicio.registrar(new SolicitudRequest("Mouse roto", "No responde el clic", Prioridad.BAJA, clienteId));
        servicio.asignarTecnico(atendida, tecnicoId);

        assertThat(servicio.listar(EstadoSolicitud.EN_PROCESO, null)).extracting(SolicitudResponse::id)
                .containsExactly(atendida);
        assertThat(servicio.listar(null, Prioridad.BAJA)).hasSize(1);
        assertThat(servicio.listar(EstadoSolicitud.ABIERTA, Prioridad.ALTA)).isEmpty();
    }

    @Test
    void noEliminaSolicitudesAtendidas() {
        Long tecnicoId = tecnicos.guardar(new Tecnico("Ana", "ana@x.pe", "912345678", Especialidad.REDES)).getId();
        Long id = servicio.registrar(request(clienteId)).id();
        servicio.asignarTecnico(id, tecnicoId);

        assertThatThrownBy(() -> servicio.eliminar(id))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("EN_PROCESO");
    }

    @Test
    void noPermiteCambiarDeCliente() {
        Long otroCliente = clientes.guardar(new Cliente("Jorge", "jorge@x.pe", "956123478", null)).getId();
        Long id = servicio.registrar(request(clienteId)).id();

        assertThatThrownBy(() -> servicio.actualizar(id, request(otroCliente)))
                .isInstanceOf(ReglaNegocioException.class);
    }
}
