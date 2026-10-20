package pe.idat.soporte.service.impl;

import org.springframework.stereotype.Service;
import pe.idat.soporte.dto.ClienteRequest;
import pe.idat.soporte.dto.ClienteResponse;
import pe.idat.soporte.exception.RecursoNoEncontradoException;
import pe.idat.soporte.exception.ReglaNegocioException;
import pe.idat.soporte.mapper.SoporteMapper;
import pe.idat.soporte.model.Cliente;
import pe.idat.soporte.repository.ClienteRepositorio;
import pe.idat.soporte.repository.SolicitudRepositorio;
import pe.idat.soporte.service.ClienteService;

import java.util.List;

@Service
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepositorio clientes;
    private final SolicitudRepositorio solicitudes;

    public ClienteServiceImpl(ClienteRepositorio clientes, SolicitudRepositorio solicitudes) {
        this.clientes = clientes;
        this.solicitudes = solicitudes;
    }

    @Override
    public ClienteResponse registrar(ClienteRequest request) {
        validarEmailLibre(request.email(), null);
        return SoporteMapper.aResponse(clientes.guardar(SoporteMapper.aEntidad(request)));
    }

    @Override
    public List<ClienteResponse> listar() {
        return clientes.listar().stream().map(SoporteMapper::aResponse).toList();
    }

    @Override
    public ClienteResponse obtener(Long id) {
        return SoporteMapper.aResponse(buscar(id));
    }

    @Override
    public ClienteResponse actualizar(Long id, ClienteRequest request) {
        Cliente cliente = buscar(id);
        validarEmailLibre(request.email(), id);
        cliente.actualizar(request.nombre().trim(), request.email().trim(), request.telefono(), request.empresa());
        return SoporteMapper.aResponse(clientes.guardar(cliente));
    }

    @Override
    public void eliminar(Long id) {
        buscar(id);
        if (solicitudes.existenPorCliente(id)) {
            throw new ReglaNegocioException("El cliente " + id + " tiene solicitudes registradas y no se puede eliminar");
        }
        clientes.eliminarPorId(id);
    }

    private Cliente buscar(Long id) {
        return clientes.buscarPorId(id).orElseThrow(() -> new RecursoNoEncontradoException("Cliente", id));
    }

    /** El email identifica al cliente: no puede repetirse, salvo en el mismo registro. */
    private void validarEmailLibre(String email, Long idActual) {
        clientes.buscarPorEmail(email.trim())
                .filter(otro -> !otro.getId().equals(idActual))
                .ifPresent(otro -> {
                    throw new ReglaNegocioException("Ya existe un cliente con el email " + email);
                });
    }
}
