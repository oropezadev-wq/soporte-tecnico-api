package pe.idat.soporte.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.idat.soporte.model.Cliente;

import java.util.Optional;

/**
 * Acceso a la tabla clientes. Spring Data JPA crea la implementación:
 * save, findById, findAll, existsById y deleteById vienen de {@link JpaRepository}.
 */
public interface ClienteRepositorio extends JpaRepository<Cliente, Long> {

    /** Consulta derivada: Spring arma el JPQL a partir del nombre del método. */
    Optional<Cliente> findByEmailIgnoreCase(String email);
}
