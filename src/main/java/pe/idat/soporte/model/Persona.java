package pe.idat.soporte.model;

/**
 * Datos comunes de las personas que participan en una solicitud.
 * Es abstracta porque en el sistema solo existen clientes y técnicos.
 */
public abstract class Persona implements Identificable {

    private Long id;
    private String nombre;
    private String email;
    private String telefono;

    protected Persona(String nombre, String email, String telefono) {
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
    }

    /** Actualiza los datos de contacto en una sola operación. */
    public void actualizarContacto(String nombre, String email, String telefono) {
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefono() {
        return telefono;
    }
}
