package pe.idat.soporte.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import pe.idat.soporte.model.Cliente;
import pe.idat.soporte.model.Especialidad;
import pe.idat.soporte.model.Prioridad;
import pe.idat.soporte.model.Solicitud;
import pe.idat.soporte.model.Tecnico;
import pe.idat.soporte.repository.ClienteRepositorio;
import pe.idat.soporte.repository.SolicitudRepositorio;
import pe.idat.soporte.repository.TecnicoRepositorio;

/**
 * Carga datos de ejemplo al iniciar para poder probar la API sin registrar todo a mano.
 * No se ejecuta con el perfil "test", así las pruebas parten de repositorios vacíos.
 */
@Component
@Profile("!test")
public class DatosIniciales implements CommandLineRunner {

    private final ClienteRepositorio clientes;
    private final TecnicoRepositorio tecnicos;
    private final SolicitudRepositorio solicitudes;

    public DatosIniciales(ClienteRepositorio clientes, TecnicoRepositorio tecnicos, SolicitudRepositorio solicitudes) {
        this.clientes = clientes;
        this.tecnicos = tecnicos;
        this.solicitudes = solicitudes;
    }

    @Override
    public void run(String... args) {
        Cliente andina = clientes.guardar(new Cliente("María Quispe", "mquispe@andina.pe", "987654321",
                "Distribuidora Andina SAC"));
        Cliente torres = clientes.guardar(new Cliente("Jorge Torres", "jtorres@gmail.com", "956123478", null));

        Tecnico redes = tecnicos.guardar(new Tecnico("Luis Ramírez", "lramirez@soporte.pe", "912345678",
                Especialidad.REDES));
        tecnicos.guardar(new Tecnico("Ana Salazar", "asalazar@soporte.pe", "923456789", Especialidad.HARDWARE));

        solicitudes.guardar(new Solicitud("La impresora del área contable no imprime",
                "Muestra el error de papel atascado aunque la bandeja está vacía.", Prioridad.MEDIA, andina.getId()));

        Solicitud sinInternet = new Solicitud("Sin conexión a internet en la oficina",
                "Ninguna computadora del segundo piso tiene internet desde las 9:00.", Prioridad.CRITICA, andina.getId());
        sinInternet.asignarTecnico(redes.getId());
        solicitudes.guardar(sinInternet);

        solicitudes.guardar(new Solicitud("La laptop se apaga sola",
                "Se apaga a los 10 minutos de uso y el ventilador hace ruido.", Prioridad.ALTA, torres.getId()));
    }
}
