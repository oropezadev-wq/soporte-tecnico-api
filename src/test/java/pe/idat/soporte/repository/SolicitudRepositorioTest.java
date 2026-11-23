package pe.idat.soporte.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;
import pe.idat.soporte.model.Cliente;
import pe.idat.soporte.model.Especialidad;
import pe.idat.soporte.model.EstadoSolicitud;
import pe.idat.soporte.model.Prioridad;
import pe.idat.soporte.model.Solicitud;
import pe.idat.soporte.model.Tecnico;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Prueba las consultas JPQL y el bloqueo optimista contra PostgreSQL (base soporte_test). */
@SpringBootTest
@ActiveProfiles("test")
class SolicitudRepositorioTest {

    @Autowired
    private ClienteRepositorio clientes;

    @Autowired
    private TecnicoRepositorio tecnicos;

    @Autowired
    private SolicitudRepositorio solicitudes;

    @Test
    void consultasPorClienteYTecnicoTraenSoloLasSuyas() {
        Cliente cliente = clientes.save(new Cliente("Rosa", "rosa.repo@x.pe", "987654321", null));
        Tecnico tecnico = tecnicos.save(new Tecnico("Iván", "ivan.repo@x.pe", "912345678", Especialidad.SOFTWARE));
        Solicitud conTecnico = new Solicitud("Office no abre", "Error al iniciar Word", Prioridad.MEDIA, null, cliente);
        conTecnico.asignarTecnico(tecnico);
        solicitudes.save(conTecnico);
        solicitudes.save(new Solicitud("Teclado lento", "Las teclas responden tarde", Prioridad.BAJA, null, cliente));

        List<Solicitud> delCliente = solicitudes.listarPorCliente(cliente.getId());
        List<Solicitud> delTecnico = solicitudes.listarPorTecnico(tecnico.getId());

        assertThat(delCliente).hasSize(2);
        assertThat(delTecnico).extracting(Solicitud::getTitulo).containsExactly("Office no abre");
        assertThat(solicitudes.existsByTecnicoId(tecnico.getId())).isTrue();
        assertThat(solicitudes.buscar(EstadoSolicitud.EN_PROCESO, Prioridad.MEDIA))
                .extracting(Solicitud::getId).contains(conTecnico.getId());
    }

    @Test
    void guardaElCostoConDosDecimales() {
        Cliente cliente = clientes.save(new Cliente("Hugo", "hugo.repo@x.pe", "987654321", null));
        Long id = solicitudes.save(new Solicitud("Cambio de disco", "Disco lleno de sectores dañados",
                Prioridad.ALTA, new BigDecimal("349.9"), cliente)).getId();

        assertThat(solicitudes.findById(id).orElseThrow().getCosto()).isEqualTo(new BigDecimal("349.90"));
    }

    @Test
    void dosCambiosSobreLaMismaVersionNoSePisan() {
        Cliente cliente = clientes.save(new Cliente("Lucía", "lucia.repo@x.pe", "987654321", null));
        Long id = solicitudes.save(new Solicitud("Impresora sin red", "No aparece en la red",
                Prioridad.MEDIA, null, cliente)).getId();

        // Dos peticiones leen la misma fila (version 0)
        Solicitud primera = solicitudes.findById(id).orElseThrow();
        Solicitud segunda = solicitudes.findById(id).orElseThrow();

        primera.actualizar("Impresora sin red", "No aparece en la red", Prioridad.ALTA, null);
        solicitudes.save(primera); // version pasa a 1

        segunda.actualizar("Impresora sin red", "No aparece en la red", Prioridad.BAJA, null);
        assertThatThrownBy(() -> solicitudes.save(segunda))
                .isInstanceOf(ObjectOptimisticLockingFailureException.class);
        assertThat(solicitudes.findById(id).orElseThrow().getPrioridad()).isEqualTo(Prioridad.ALTA);
    }
}
