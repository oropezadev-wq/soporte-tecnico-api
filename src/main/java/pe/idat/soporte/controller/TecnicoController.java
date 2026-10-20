package pe.idat.soporte.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pe.idat.soporte.dto.SolicitudResponse;
import pe.idat.soporte.dto.TecnicoRequest;
import pe.idat.soporte.dto.TecnicoResponse;
import pe.idat.soporte.model.Especialidad;
import pe.idat.soporte.service.SolicitudService;
import pe.idat.soporte.service.TecnicoService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tecnicos")
@Tag(name = "Técnicos", description = "Personal que atiende las solicitudes")
public class TecnicoController {

    private final TecnicoService tecnicoService;
    private final SolicitudService solicitudService;

    public TecnicoController(TecnicoService tecnicoService, SolicitudService solicitudService) {
        this.tecnicoService = tecnicoService;
        this.solicitudService = solicitudService;
    }

    @PostMapping
    @Operation(summary = "Registrar un técnico")
    @ApiResponse(responseCode = "201", description = "Técnico creado")
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    @ApiResponse(responseCode = "409", description = "El email ya está registrado")
    public ResponseEntity<TecnicoResponse> registrar(@Valid @RequestBody TecnicoRequest request) {
        TecnicoResponse creado = tecnicoService.registrar(request);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(creado.id()).toUri();
        return ResponseEntity.created(ubicacion).body(creado);
    }

    @GetMapping
    @Operation(summary = "Listar técnicos", description = "Se puede filtrar por especialidad")
    public List<TecnicoResponse> listar(
            @Parameter(description = "HARDWARE, SOFTWARE, REDES o IMPRESORAS")
            @RequestParam(required = false) Especialidad especialidad) {
        return tecnicoService.listar(especialidad);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un técnico")
    @ApiResponse(responseCode = "200", description = "Técnico encontrado")
    @ApiResponse(responseCode = "404", description = "El técnico no existe")
    public TecnicoResponse obtener(@PathVariable Long id) {
        return tecnicoService.obtener(id);
    }

    @GetMapping("/{id}/solicitudes")
    @Operation(summary = "Listar las solicitudes asignadas a un técnico")
    @ApiResponse(responseCode = "200", description = "Solicitudes del técnico")
    @ApiResponse(responseCode = "404", description = "El técnico no existe")
    public List<SolicitudResponse> solicitudes(@PathVariable Long id) {
        return solicitudService.listarPorTecnico(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un técnico")
    @ApiResponse(responseCode = "200", description = "Técnico actualizado")
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    @ApiResponse(responseCode = "404", description = "El técnico no existe")
    @ApiResponse(responseCode = "409", description = "El email pertenece a otro técnico")
    public TecnicoResponse actualizar(@PathVariable Long id, @Valid @RequestBody TecnicoRequest request) {
        return tecnicoService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un técnico sin solicitudes")
    @ApiResponse(responseCode = "204", description = "Técnico eliminado")
    @ApiResponse(responseCode = "404", description = "El técnico no existe")
    @ApiResponse(responseCode = "409", description = "El técnico tiene solicitudes registradas; se desactiva con activo=false")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        tecnicoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
