package pe.idat.soporte.model;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Datos comunes de las personas que participan en una solicitud.
 * Es abstracta porque en el sistema solo existen clientes y técnicos;
 * {@code @MappedSuperclass} hace que sus columnas se repitan en la tabla de cada hija.
 */
@MappedSuperclass
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 120, unique = true)
    private String email;

    @Column(nullable = false, length = 15)
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
}
