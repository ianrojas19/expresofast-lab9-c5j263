package cr.ac.ucr.paraiso.ie.c5j263.expresofast.controller;

import cr.ac.ucr.paraiso.ie.c5j263.expresofast.business.EnvioService;
import cr.ac.ucr.paraiso.ie.c5j263.expresofast.domain.dto.CambioEstadoDTO;
import cr.ac.ucr.paraiso.ie.c5j263.expresofast.domain.dto.EnvioDTO;
import cr.ac.ucr.paraiso.ie.c5j263.expresofast.domain.dto.EnvioRequestDTO;
import cr.ac.ucr.paraiso.ie.c5j263.expresofast.domain.dto.EnvioResponseDTO;
import cr.ac.ucr.paraiso.ie.c5j263.expresofast.domain.dto.BitacoraResponseDTO;
import cr.ac.ucr.paraiso.ie.c5j263.expresofast.domain.dto.ResumenMetricasDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class EnvioController {

    private final EnvioService envioService;

    public EnvioController(EnvioService envioService) {
        this.envioService = envioService;
    }




    @GetMapping("/v1/envios")
    public ResponseEntity<Page<EnvioDTO>> listarEnviosPaginados(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "fechaCreacion") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false) String estado) {

        Page<EnvioDTO> result = envioService.listarPaginado(page, size, sortBy, direction, busqueda, estado);
        return ResponseEntity.ok(result);
    }


    @GetMapping("/v1/envios/procedimiento/{estado}")
    public ResponseEntity<List<EnvioDTO>> listarPorProcedimientoAlmacenado(
            @PathVariable String estado) {
        List<EnvioDTO> result = envioService.listarViaStoredProcedure(estado);
        return ResponseEntity.ok(result);
    }


    @GetMapping("/v1/envios/metricas")
    public ResponseEntity<List<ResumenMetricasDTO>> obtenerMetricas() {
        return ResponseEntity.ok(envioService.obtenerResumenMetricas());
    }



    @GetMapping("/envios/optimizados")
    public ResponseEntity<List<EnvioResponseDTO>> getEnviosOptimizados() {
        return ResponseEntity.ok(envioService.obtenerEnviosOptimizados());
    }

    @GetMapping("/envios/{id}")
    public ResponseEntity<EnvioResponseDTO> getEnvioById(@PathVariable Integer id) {
        return ResponseEntity.ok(envioService.obtenerEnvio(id));
    }

    @PostMapping("/envios")
    public ResponseEntity<EnvioResponseDTO> registrarEnvio(@Valid @RequestBody EnvioRequestDTO envioRequestDTO) {
        return ResponseEntity.ok(envioService.registrarEnvio(envioRequestDTO));
    }

    @PatchMapping("/envios/{id}/estado")
    public ResponseEntity<EnvioResponseDTO> actualizarEstado(@PathVariable Integer id, @RequestBody CambioEstadoDTO cambioEstadoDTO) {
        return ResponseEntity.ok(envioService.actualizarEstado(id, cambioEstadoDTO));
    }

    @GetMapping("/envios/{id}/bitacora")
    public ResponseEntity<List<BitacoraResponseDTO>> obtenerBitacora(@PathVariable Integer id) {
        return ResponseEntity.ok(envioService.obtenerBitacora(id));
    }
}
