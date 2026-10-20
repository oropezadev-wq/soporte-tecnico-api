package pe.idat.soporte.service.impl;

import org.springframework.stereotype.Service;
import pe.idat.soporte.dto.TecnicoRequest;
import pe.idat.soporte.dto.TecnicoResponse;
import pe.idat.soporte.exception.RecursoNoEncontradoException;
import pe.idat.soporte.exception.ReglaNegocioException;
import pe.idat.soporte.mapper.SoporteMapper;
import pe.idat.soporte.model.Especialidad;
import pe.idat.soporte.model.Tecnico;
import pe.idat.soporte.repository.SolicitudRepositorio;
import pe.idat.soporte.repository.TecnicoRepositorio;
import pe.idat.soporte.service.TecnicoService;

import java.util.List;

@Service
public class TecnicoServiceImpl implements TecnicoService {

    private final TecnicoRepositorio tecnicos;
    private final SolicitudRepositorio solicitudes;

    public TecnicoServiceImpl(TecnicoRepositorio tecnicos, SolicitudRepositorio solicitudes) {
        this.tecnicos = tecnicos;
        this.solicitudes = solicitudes;
    }

    @Override
    public TecnicoResponse registrar(TecnicoRequest request) {
        validarEmailLibre(request.email(), null);
        return SoporteMapper.aResponse(tecnicos.guardar(SoporteMapper.aEntidad(request)));
    }

    @Override
    public List<TecnicoResponse> listar(Especialidad especialidad) {
        List<Tecnico> resultado = especialidad == null
                ? tecnicos.listar()
                : tecnicos.listarPorEspecialidad(especialidad);
        return resultado.stream().map(SoporteMapper::aResponse).toList();
    }

    @Override
    public TecnicoResponse obtener(Long id) {
        return SoporteMapper.aResponse(buscar(id));
    }

    @Override
    public TecnicoResponse actualizar(Long id, TecnicoRequest request) {
        Tecnico tecnico = buscar(id);
        validarEmailLibre(request.email(), id);
        boolean activo = request.activo() == null ? tecnico.isActivo() : request.activo();
        tecnico.actualizar(request.nombre().trim(), request.email().trim(), request.telefono(),
                request.especialidad(), activo);
        return SoporteMapper.aResponse(tecnicos.guardar(tecnico));
    }

    @Override
    public void eliminar(Long id) {
        buscar(id);
        if (solicitudes.existenPorTecnico(id)) {
            throw new ReglaNegocioException("El técnico " + id
                    + " tiene solicitudes registradas y no se puede eliminar; desactívelo con activo=false");
        }
        tecnicos.eliminarPorId(id);
    }

    private Tecnico buscar(Long id) {
        return tecnicos.buscarPorId(id).orElseThrow(() -> new RecursoNoEncontradoException("Técnico", id));
    }

    private void validarEmailLibre(String email, Long idActual) {
        tecnicos.buscarPorEmail(email.trim())
                .filter(otro -> !otro.getId().equals(idActual))
                .ifPresent(otro -> {
                    throw new ReglaNegocioException("Ya existe un técnico con el email " + email);
                });
    }
}
