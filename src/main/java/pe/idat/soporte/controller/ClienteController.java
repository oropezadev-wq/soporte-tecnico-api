package pe.idat.soporte.controller;

import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pe.idat.soporte.dto.ClienteRequest;
import pe.idat.soporte.dto.ClienteResponse;
import pe.idat.soporte.dto.SolicitudResponse;
import pe.idat.soporte.service.ClienteService;
import pe.idat.soporte.service.SolicitudService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/clientes")
@Tag(name = "Clientes", description = "Personas o empresas que reportan problemas")
public class ClienteController {

    private final ClienteService clienteService;
    private final SolicitudService solicitudService;

    public ClienteController(ClienteService clienteService, SolicitudService solicitudService) {
        this.clienteService = clienteService;
        this.solicitudService = solicitudService;
    }

    @PostMapping
    @Operation(summary = "Registrar un cliente")
    @ApiResponse(responseCode = "201", description = "Cliente creado")
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    @ApiResponse(responseCode = "409", description = "El email ya está registrado")
    public ResponseEntity<ClienteResponse> registrar(@Valid @RequestBody ClienteRequest request) {
        ClienteResponse creado = clienteService.registrar(request);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(creado.id()).toUri();
        return ResponseEntity.created(ubicacion).body(creado);
    }

    @GetMapping
    @Operation(summary = "Listar clientes")
    public List<ClienteResponse> listar() {
        return clienteService.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un cliente")
    @ApiResponse(responseCode = "200", description = "Cliente encontrado")
    @ApiResponse(responseCode = "404", description = "El cliente no existe")
    public ClienteResponse obtener(@PathVariable Long id) {
        return clienteService.obtener(id);
    }

    @GetMapping("/{id}/solicitudes")
    @Operation(summary = "Listar las solicitudes de un cliente")
    @ApiResponse(responseCode = "200", description = "Solicitudes del cliente")
    @ApiResponse(responseCode = "404", description = "El cliente no existe")
    public List<SolicitudResponse> solicitudes(@PathVariable Long id) {
        return solicitudService.listarPorCliente(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un cliente")
    @ApiResponse(responseCode = "200", description = "Cliente actualizado")
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    @ApiResponse(responseCode = "404", description = "El cliente no existe")
    @ApiResponse(responseCode = "409", description = "El email pertenece a otro cliente")
    public ClienteResponse actualizar(@PathVariable Long id, @Valid @RequestBody ClienteRequest request) {
        return clienteService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un cliente sin solicitudes")
    @ApiResponse(responseCode = "204", description = "Cliente eliminado")
    @ApiResponse(responseCode = "404", description = "El cliente no existe")
    @ApiResponse(responseCode = "409", description = "El cliente tiene solicitudes")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        clienteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
