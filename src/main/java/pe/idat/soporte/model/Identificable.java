package pe.idat.soporte.model;

/**
 * Contrato de toda entidad que se guarda en un repositorio.
 * El repositorio asigna el identificador al guardar por primera vez.
 */
public interface Identificable {

    Long getId();

    void setId(Long id);
}
