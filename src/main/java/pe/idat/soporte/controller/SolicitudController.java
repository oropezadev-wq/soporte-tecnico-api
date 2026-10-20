package pe.idat.soporte.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pe.idat.soporte.dto.AsignacionTecnicoRequest;
import pe.idat.soporte.dto.CambioEstadoRequest;
import pe.idat.soporte.dto.SolicitudRequest;
import pe.idat.soporte.dto.SolicitudResponse;
import pe.idat.soporte.model.EstadoSolicitud;
import pe.idat.soporte.model.Prioridad;
import pe.idat.soporte.service.SolicitudService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/solicitudes")
@Tag(name = "Solicitudes", description = "Registro y atención de solicitudes de soporte técnico")
public class SolicitudController {

    private final SolicitudService solicitudService;

    public SolicitudController(SolicitudService solicitudService) {
        this.solicitudService = solicitudService;
    }

    @PostMapping
    @Operation(summary = "Registrar una solicitud", description = "Toda solicitud nueva empieza en estado ABIERTA")
    @ApiResponse(responseCode = "201", description = "Solicitud creada")
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    @ApiResponse(responseCode = "404", description = "El cliente no existe")
    public ResponseEntity<SolicitudResponse> registrar(@Valid @RequestBody SolicitudRequest request) {
        SolicitudResponse creada = solicitudService.registrar(request);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(creada.id()).toUri();
        return ResponseEntity.created(ubicacion).body(creada);
    }

    @GetMapping
    @Operation(summary = "Listar solicitudes", description = "Se puede filtrar por estado y por prioridad")
    public List<SolicitudResponse> listar(
            @Parameter(description = "ABIERTA, EN_PROCESO, RESUELTA, CERRADA o CANCELADA")
            @RequestParam(required = false) EstadoSolicitud estado,
            @Parameter(description = "BAJA, MEDIA, ALTA o CRITICA")
            @RequestParam(required = false) Prioridad prioridad) {
        return solicitudService.listar(estado, prioridad);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una solicitud")
    @ApiResponse(responseCode = "200", description = "Solicitud encontrada")
    @ApiResponse(responseCode = "404", description = "La solicitud no existe")
    public SolicitudResponse obtener(@PathVariable Long id) {
        return solicitudService.obtener(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar título, descripción y prioridad")
    @ApiResponse(responseCode = "200", description = "Solicitud actualizada")
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    @ApiResponse(responseCode = "404", description = "La solicitud no existe")
    @ApiResponse(responseCode = "409", description = "La solicitud está cerrada o cambia de cliente")
    public SolicitudResponse actualizar(@PathVariable Long id, @Valid @RequestBody SolicitudRequest request) {
        return solicitudService.actualizar(id, request);
    }

    @PatchMapping("/{id}/tecnico")
    @Operation(summary = "Asignar un técnico", description = "Si la solicitud está ABIERTA pasa a EN_PROCESO")
    @ApiResponse(responseCode = "200", description = "Técnico asignado")
    @ApiResponse(responseCode = "404", description = "La solicitud o el técnico no existen")
    @ApiResponse(responseCode = "409", description = "Técnico inactivo o solicitud finalizada")
    public SolicitudResponse asignarTecnico(@PathVariable Long id,
                                            @Valid @RequestBody AsignacionTecnicoRequest request) {
        return solicitudService.asignarTecnico(id, request.tecnicoId());
    }

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Cambiar el estado",
            description = "ABIERTA → EN_PROCESO o CANCELADA; EN_PROCESO → RESUELTA o CANCELADA; RESUELTA → CERRADA o EN_PROCESO")
    @ApiResponse(responseCode = "200", description = "Estado cambiado")
    @ApiResponse(responseCode = "404", description = "La solicitud no existe")
    @ApiResponse(responseCode = "409", description = "Transición de estado no permitida")
    public SolicitudResponse cambiarEstado(@PathVariable Long id, @Valid @RequestBody CambioEstadoRequest request) {
        return solicitudService.cambiarEstado(id, request.estado());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una solicitud ABIERTA o CANCELADA")
    @ApiResponse(responseCode = "204", description = "Solicitud eliminada")
    @ApiResponse(responseCode = "404", description = "La solicitud no existe")
    @ApiResponse(responseCode = "409", description = "La solicitud ya fue atendida")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        solicitudService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
