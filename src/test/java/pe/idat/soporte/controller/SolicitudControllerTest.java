package pe.idat.soporte.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Prueba la API completa por HTTP: validaciones, códigos de estado y formato de error.
 * Cada prueba crea sus propios datos porque el contexto (y la memoria) se comparte entre pruebas.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SolicitudControllerTest {

    @Autowired
    private MockMvc mvc;

    private long crear(String ruta, String json) throws Exception {
        String cuerpo = mvc.perform(post(ruta).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(cuerpo, "$.id")).longValue();
    }

    private long crearCliente(String email) throws Exception {
        return crear("/api/v1/clientes", """
                {"nombre":"Cliente de prueba","email":"%s","telefono":"987654321"}""".formatted(email));
    }

    private long crearTecnico(String email) throws Exception {
        return crear("/api/v1/tecnicos", """
                {"nombre":"Técnico de prueba","email":"%s","telefono":"912345678","especialidad":"REDES"}"""
                .formatted(email));
    }

    private String solicitud(long clienteId) {
        return """
                {"titulo":"No hay internet","descripcion":"Se cayó la red del piso 2","prioridad":"ALTA","clienteId":%d}"""
                .formatted(clienteId);
    }

    @Test
    void registrarDevuelve201ConUbicacion() throws Exception {
        long clienteId = crearCliente("registro@x.pe");

        mvc.perform(post("/api/v1/solicitudes").contentType(MediaType.APPLICATION_JSON).content(solicitud(clienteId)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/v1/solicitudes/")))
                .andExpect(jsonPath("$.estado").value("ABIERTA"))
                .andExpect(jsonPath("$.clienteNombre").value("Cliente de prueba"));
    }

    @Test
    void datosInvalidosDevuelven400ConDetallePorCampo() throws Exception {
        mvc.perform(post("/api/v1/solicitudes").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"x\",\"descripcion\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.estado").value(400))
                .andExpect(jsonPath("$.errores.titulo").exists())
                .andExpect(jsonPath("$.errores.descripcion").exists())
                .andExpect(jsonPath("$.errores.prioridad").value("La prioridad es obligatoria"))
                .andExpect(jsonPath("$.errores.clienteId").value("El cliente es obligatorio"));
    }

    @Test
    void prioridadDesconocidaDevuelve400() throws Exception {
        mvc.perform(post("/api/v1/solicitudes").contentType(MediaType.APPLICATION_JSON)
                        .content(solicitud(1).replace("ALTA", "URGENTE")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value(containsString("no permitidos")));
    }

    @Test
    void solicitudInexistenteDevuelve404() throws Exception {
        mvc.perform(get("/api/v1/solicitudes/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Solicitud con id 999999 no existe"))
                .andExpect(jsonPath("$.ruta").value("/api/v1/solicitudes/999999"));
    }

    @Test
    void idNoNumericoDevuelve400() throws Exception {
        mvc.perform(get("/api/v1/solicitudes/abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void transicionInvalidaDevuelve409() throws Exception {
        long id = crear("/api/v1/solicitudes", solicitud(crearCliente("transicion@x.pe")));

        mvc.perform(patch("/api/v1/solicitudes/{id}/estado", id).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"CERRADA\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensaje").value("No se puede pasar una solicitud de ABIERTA a CERRADA"));
    }

    @Test
    void flujoCompletoDeAtencion() throws Exception {
        long clienteId = crearCliente("flujo@x.pe");
        long tecnicoId = crearTecnico("tecnico.flujo@x.pe");
        long id = crear("/api/v1/solicitudes", solicitud(clienteId));

        mvc.perform(patch("/api/v1/solicitudes/{id}/tecnico", id).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tecnicoId\":%d}".formatted(tecnicoId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("EN_PROCESO"))
                .andExpect(jsonPath("$.tecnicoNombre").value("Técnico de prueba"));

        mvc.perform(patch("/api/v1/solicitudes/{id}/estado", id).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"RESUELTA\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("RESUELTA"));

        mvc.perform(get("/api/v1/tecnicos/{id}/solicitudes", tecnicoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        mvc.perform(delete("/api/v1/solicitudes/{id}", id))
                .andExpect(status().isConflict());
    }

    @Test
    void eliminarSolicitudAbiertaDevuelve204() throws Exception {
        long id = crear("/api/v1/solicitudes", solicitud(crearCliente("eliminar@x.pe")));

        mvc.perform(delete("/api/v1/solicitudes/{id}", id)).andExpect(status().isNoContent());
        mvc.perform(get("/api/v1/solicitudes/{id}", id)).andExpect(status().isNotFound());
    }

    @Test
    void clienteConSolicitudesNoSeElimina() throws Exception {
        long clienteId = crearCliente("conSolicitudes@x.pe");
        crear("/api/v1/solicitudes", solicitud(clienteId));

        mvc.perform(delete("/api/v1/clientes/{id}", clienteId))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensaje").value(endsWith("no se puede eliminar")));
    }

    @Test
    void emailDuplicadoDevuelve409() throws Exception {
        crearCliente("duplicado@x.pe");

        mvc.perform(post("/api/v1/clientes").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Otro\",\"email\":\"DUPLICADO@x.pe\",\"telefono\":\"987654321\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void swaggerPublicaLaDocumentacion() throws Exception {
        mvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("API de Solicitudes de Soporte Técnico"))
                .andExpect(jsonPath("$.paths['/api/v1/solicitudes/{id}/estado']").exists());
    }
}
