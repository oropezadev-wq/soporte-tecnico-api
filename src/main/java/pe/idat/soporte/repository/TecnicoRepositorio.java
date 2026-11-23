package pe.idat.soporte.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.idat.soporte.model.Especialidad;
import pe.idat.soporte.model.Tecnico;

import java.util.List;
import java.util.Optional;

/** Acceso a la tabla tecnicos; la implementación la genera Spring Data JPA. */
public interface TecnicoRepositorio extends JpaRepository<Tecnico, Long> {

    Optional<Tecnico> findByEmailIgnoreCase(String email);

    List<Tecnico> findByEspecialidadOrderByIdAsc(Especialidad especialidad);
}
