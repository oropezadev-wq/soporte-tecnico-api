package pe.idat.soporte.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pe.idat.soporte.model.Cliente;

import static org.assertj.core.api.Assertions.assertThat;

class RepositorioEnMemoriaTest {

    private ClienteRepositorio repositorio;

    @BeforeEach
    void preparar() {
        repositorio = new ClienteRepositorio();
    }

    private Cliente cliente(String email) {
        return new Cliente("Cliente", email, "987654321", null);
    }

    @Test
    void asignaIdsAutoincrementales() {
        Cliente a = repositorio.guardar(cliente("a@x.pe"));
        Cliente b = repositorio.guardar(cliente("b@x.pe"));

        assertThat(a.getId()).isEqualTo(1L);
        assertThat(b.getId()).isEqualTo(2L);
        assertThat(repositorio.listar()).extracting(Cliente::getId).containsExactly(1L, 2L);
    }

    @Test
    void noReutilizaIdsEliminados() {
        repositorio.guardar(cliente("a@x.pe"));
        Cliente b = repositorio.guardar(cliente("b@x.pe"));
        repositorio.eliminarPorId(b.getId());

        Cliente c = repositorio.guardar(cliente("c@x.pe"));

        assertThat(c.getId()).isEqualTo(3L);
        assertThat(repositorio.existePorId(2L)).isFalse();
    }

    @Test
    void guardarConIdExistenteReemplazaSinDuplicar() {
        Cliente a = repositorio.guardar(cliente("a@x.pe"));
        a.actualizar("Nuevo nombre", "a@x.pe", "987654321", "Empresa");

        repositorio.guardar(a);

        assertThat(repositorio.listar()).hasSize(1);
        assertThat(repositorio.buscarPorId(a.getId())).get()
                .extracting(Cliente::getNombre).isEqualTo("Nuevo nombre");
    }

    @Test
    void buscaPorEmailSinDistinguirMayusculas() {
        repositorio.guardar(cliente("ventas@empresa.pe"));

        assertThat(repositorio.buscarPorEmail("VENTAS@empresa.pe")).isPresent();
        assertThat(repositorio.buscarPorEmail("otro@empresa.pe")).isEmpty();
    }
}
