package pe.idat.soporte.model;

/** Persona o empresa que reporta un problema técnico. */
public class Cliente extends Persona {

    private String empresa;

    public Cliente(String nombre, String email, String telefono, String empresa) {
        super(nombre, email, telefono);
        this.empresa = empresa;
    }

    public void actualizar(String nombre, String email, String telefono, String empresa) {
        actualizarContacto(nombre, email, telefono);
        this.empresa = empresa;
    }

    public String getEmpresa() {
        return empresa;
    }
}
