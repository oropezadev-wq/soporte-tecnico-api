package pe.idat.soporte.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Persona o empresa que reporta un problema técnico. Tabla {@code soporte.clientes}. */
@Entity
@Table(name = "clientes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cliente extends Persona {

    @Column(length = 120)
    private String empresa;

    public Cliente(String nombre, String email, String telefono, String empresa) {
        super(nombre, email, telefono);
        this.empresa = empresa;
    }

    public void actualizar(String nombre, String email, String telefono, String empresa) {
        actualizarContacto(nombre, email, telefono);
        this.empresa = empresa;
    }
}
