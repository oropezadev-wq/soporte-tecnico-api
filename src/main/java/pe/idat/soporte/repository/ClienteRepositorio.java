package pe.idat.soporte.repository;

import org.springframework.stereotype.Repository;
import pe.idat.soporte.model.Cliente;

import java.util.Optional;

@Repository
public class ClienteRepositorio extends RepositorioEnMemoria<Cliente> {

    public Optional<Cliente> buscarPorEmail(String email) {
        return filtrar(c -> c.getEmail().equalsIgnoreCase(email)).stream().findFirst();
    }
}
