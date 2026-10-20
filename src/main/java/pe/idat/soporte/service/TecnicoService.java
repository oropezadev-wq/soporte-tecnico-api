package pe.idat.soporte.service;

import pe.idat.soporte.dto.TecnicoRequest;
import pe.idat.soporte.dto.TecnicoResponse;
import pe.idat.soporte.model.Especialidad;

import java.util.List;

/** Casos de uso de técnicos. */
public interface TecnicoService {

    TecnicoResponse registrar(TecnicoRequest request);

    /** Con {@code especialidad} nula devuelve todos. */
    List<TecnicoResponse> listar(Especialidad especialidad);

    TecnicoResponse obtener(Long id);

    TecnicoResponse actualizar(Long id, TecnicoRequest request);

    /** No elimina técnicos con solicitudes pendientes de atender. */
    void eliminar(Long id);
}
