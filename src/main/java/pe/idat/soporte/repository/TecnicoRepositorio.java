package pe.idat.soporte.repository;

import org.springframework.stereotype.Repository;
import pe.idat.soporte.model.Especialidad;
import pe.idat.soporte.model.Tecnico;

import java.util.List;
import java.util.Optional;

@Repository
public class TecnicoRepositorio extends RepositorioEnMemoria<Tecnico> {

    public Optional<Tecnico> buscarPorEmail(String email) {
        return filtrar(t -> t.getEmail().equalsIgnoreCase(email)).stream().findFirst();
    }

    public List<Tecnico> listarPorEspecialidad(Especialidad especialidad) {
        return filtrar(t -> t.getEspecialidad() == especialidad);
    }
}
