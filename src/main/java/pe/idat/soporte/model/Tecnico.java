package pe.idat.soporte.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Personal de la empresa que atiende las solicitudes. Tabla {@code soporte.tecnicos}. */
@Entity
@Table(name = "tecnicos")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tecnico extends Persona {

    /** Se guarda como texto (REDES, HARDWARE...) para que la tabla se entienda sin el código. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Especialidad especialidad;

    @Column(nullable = false)
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
}
