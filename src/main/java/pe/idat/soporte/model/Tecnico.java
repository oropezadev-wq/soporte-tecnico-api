package pe.idat.soporte.model;

/** Personal de la empresa que atiende las solicitudes. */
public class Tecnico extends Persona {

    private Especialidad especialidad;
    private boolean activo;

    public Tecnico(String nombre, String email, String telefono, Especialidad especialidad) {
        super(nombre, email, telefono);
        this.especialidad = especialidad;
        this.activo = true;
    }

    public void actualizar(String nombre, String email, String telefono,
                           Especialidad especialidad, boolean activo) {
        actualizarContacto(nombre, email, telefono);
        this.especialidad = especialidad;
        this.activo = activo;
    }

    public Especialidad getEspecialidad() {
        return especialidad;
    }

    public boolean isActivo() {
        return activo;
    }
}
