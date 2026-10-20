package pe.idat.soporte.repository;

import pe.idat.soporte.model.Identificable;

import java.util.List;
import java.util.Optional;

/**
 * Operaciones básicas de persistencia. Los servicios dependen de esta interfaz
 * y no de la implementación, para poder cambiar la memoria por una base de datos
 * sin tocar la lógica de negocio.
 */
public interface Repositorio<T extends Identificable> {

    /** Inserta si no tiene id (y le asigna uno) o reemplaza si ya existe. */
    T guardar(T entidad);

    Optional<T> buscarPorId(Long id);

    List<T> listar();

    boolean existePorId(Long id);

    void eliminarPorId(Long id);
}
