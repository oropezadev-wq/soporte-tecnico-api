package pe.idat.soporte.service;

import pe.idat.soporte.dto.ClienteRequest;
import pe.idat.soporte.dto.ClienteResponse;

import java.util.List;

/** Casos de uso de clientes. */
public interface ClienteService {

    ClienteResponse registrar(ClienteRequest request);

    List<ClienteResponse> listar();

    ClienteResponse obtener(Long id);

    ClienteResponse actualizar(Long id, ClienteRequest request);

    /** Solo elimina clientes sin solicitudes, para no dejar solicitudes huérfanas. */
    void eliminar(Long id);
}
