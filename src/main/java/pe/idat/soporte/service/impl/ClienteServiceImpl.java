package pe.idat.soporte.service.impl;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

/**
 * Las operaciones que escriben corren en una transacción: si se lanza una
 * RuntimeException (por ejemplo ReglaNegocioException), se hace rollback y no
 * queda nada a medias en la base. Las lecturas usan readOnly = true.
 */
@Service
@Transactional
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
        return SoporteMapper.aResponse(clientes.save(SoporteMapper.aEntidad(request)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteResponse> listar() {
        return clientes.findAll(Sort.by("id")).stream().map(SoporteMapper::aResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponse obtener(Long id) {
        return SoporteMapper.aResponse(buscar(id));
    }

    @Override
    public ClienteResponse actualizar(Long id, ClienteRequest request) {
        Cliente cliente = buscar(id);
        validarEmailLibre(request.email(), id);
        cliente.actualizar(request.nombre().trim(), request.email().trim(), request.telefono(), request.empresa());
        return SoporteMapper.aResponse(clientes.save(cliente));
    }

    @Override
    public void eliminar(Long id) {
        buscar(id);
        if (solicitudes.existsByClienteId(id)) {
            throw new ReglaNegocioException("El cliente " + id + " tiene solicitudes registradas y no se puede eliminar");
        }
        clientes.deleteById(id);
    }

    private Cliente buscar(Long id) {
        return clientes.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Cliente", id));
    }

    /** El email identifica al cliente: no puede repetirse, salvo en el mismo registro. */
    private void validarEmailLibre(String email, Long idActual) {
        clientes.findByEmailIgnoreCase(email.trim())
                .filter(otro -> !otro.getId().equals(idActual))
                .ifPresent(otro -> {
                    throw new ReglaNegocioException("Ya existe un cliente con el email " + email);
                });
    }
}
