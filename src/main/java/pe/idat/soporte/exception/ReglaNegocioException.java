package pe.idat.soporte.exception;

/** Se lanza cuando una operación viola una regla del negocio. Se responde con 409. */
public class ReglaNegocioException extends RuntimeException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
