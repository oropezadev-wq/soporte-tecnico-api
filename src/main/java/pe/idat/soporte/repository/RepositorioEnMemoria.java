package pe.idat.soporte.repository;

import pe.idat.soporte.model.Identificable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;

/**
 * Simula una tabla de base de datos con un {@link Map} en memoria.
 * <ul>
 *   <li>Los ids son autoincrementales y nunca se reutilizan, aunque se elimine un registro.</li>
 *   <li>{@link ConcurrentHashMap} y {@link AtomicLong} evitan datos corruptos si llegan
 *       varias peticiones a la vez.</li>
 *   <li>Los datos se pierden al detener la aplicación.</li>
 * </ul>
 */
public abstract class RepositorioEnMemoria<T extends Identificable> implements Repositorio<T> {

    private final Map<Long, T> tabla = new ConcurrentHashMap<>();
    private final AtomicLong secuencia = new AtomicLong();

    @Override
    public T guardar(T entidad) {
        if (entidad.getId() == null) {
            entidad.setId(secuencia.incrementAndGet());
        }
        tabla.put(entidad.getId(), entidad);
        return entidad;
    }

    @Override
    public Optional<T> buscarPorId(Long id) {
        return Optional.ofNullable(tabla.get(id));
    }

    @Override
    public List<T> listar() {
        return filtrar(entidad -> true);
    }

    @Override
    public boolean existePorId(Long id) {
        return tabla.containsKey(id);
    }

    @Override
    public void eliminarPorId(Long id) {
        tabla.remove(id);
    }

    /** Devuelve los registros que cumplen la condición, ordenados por id como lo haría un SELECT con ORDER BY. */
    protected List<T> filtrar(Predicate<T> condicion) {
        return tabla.values().stream()
                .filter(condicion)
                .sorted((a, b) -> a.getId().compareTo(b.getId()))
                .toList();
    }
}
