package cr.ac.ucr.paraiso.ie.c5j263.expresofast.business;

import cr.ac.ucr.paraiso.ie.c5j263.expresofast.data.BitacoraEnvioRepository;
import cr.ac.ucr.paraiso.ie.c5j263.expresofast.data.ConductorRepository;
import cr.ac.ucr.paraiso.ie.c5j263.expresofast.data.EnvioRepository;
import cr.ac.ucr.paraiso.ie.c5j263.expresofast.data.UsuarioRepository;
import cr.ac.ucr.paraiso.ie.c5j263.expresofast.data.VehiculoRepository;
import cr.ac.ucr.paraiso.ie.c5j263.expresofast.domain.BitacoraEnvio;
import cr.ac.ucr.paraiso.ie.c5j263.expresofast.domain.Envio;
import cr.ac.ucr.paraiso.ie.c5j263.expresofast.domain.Usuario;
import cr.ac.ucr.paraiso.ie.c5j263.expresofast.domain.Vehiculo;
import cr.ac.ucr.paraiso.ie.c5j263.expresofast.domain.Conductor;
import cr.ac.ucr.paraiso.ie.c5j263.expresofast.domain.dto.CambioEstadoDTO;
import cr.ac.ucr.paraiso.ie.c5j263.expresofast.domain.dto.EnvioDTO;
import cr.ac.ucr.paraiso.ie.c5j263.expresofast.domain.dto.EnvioRequestDTO;
import cr.ac.ucr.paraiso.ie.c5j263.expresofast.domain.dto.EnvioResponseDTO;
import cr.ac.ucr.paraiso.ie.c5j263.expresofast.domain.dto.BitacoraResponseDTO;
import cr.ac.ucr.paraiso.ie.c5j263.expresofast.domain.dto.ResumenMetricasDTO;
import cr.ac.ucr.paraiso.ie.c5j263.expresofast.exception.InvalidStateTransitionException;
import cr.ac.ucr.paraiso.ie.c5j263.expresofast.exception.ResourceNotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import javax.sql.DataSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class EnvioService {

    private final EnvioRepository envioRepository;
    private final VehiculoRepository vehiculoRepository;
    private final ConductorRepository conductorRepository;
    private final BitacoraEnvioRepository bitacoraEnvioRepository;
    private final UsuarioRepository usuarioRepository;
    private final DataSource dataSource;

    @PersistenceContext
    private EntityManager entityManager;

    public EnvioService(EnvioRepository envioRepository, VehiculoRepository vehiculoRepository, 
                        ConductorRepository conductorRepository, BitacoraEnvioRepository bitacoraEnvioRepository,
                        UsuarioRepository usuarioRepository, DataSource dataSource) {
        this.envioRepository = envioRepository;
        this.vehiculoRepository = vehiculoRepository;
        this.conductorRepository = conductorRepository;
        this.bitacoraEnvioRepository = bitacoraEnvioRepository;
        this.usuarioRepository = usuarioRepository;
        this.dataSource = dataSource;
    }

    @Transactional(readOnly = true)
    public List<EnvioResponseDTO> obtenerEnviosOptimizados() {
        return envioRepository.findAllWithDetails().stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }




    @Transactional(readOnly = true)
    public Page<EnvioDTO> listarPaginado(int page, int size, String sortBy, String dir,
                                          String busqueda, String estado) {
        Sort sort = dir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        PageRequest pageRequest = PageRequest.of(page, size, sort);

        Page<Envio> enviosPage;

        boolean tieneBusqueda = busqueda != null && !busqueda.trim().isEmpty();
        boolean tieneEstado = estado != null && !estado.trim().isEmpty();

        if (tieneBusqueda && tieneEstado) {
            enviosPage = envioRepository.buscarPorTerminoYEstado(busqueda.trim(), estado.trim(), pageRequest);
        } else if (tieneBusqueda) {
            enviosPage = envioRepository.buscarPorTermino(busqueda.trim(), pageRequest);
        } else if (tieneEstado) {
            enviosPage = envioRepository.findByEstadoEnvio(estado.trim(), pageRequest);
        } else {
            enviosPage = envioRepository.findAll(pageRequest);
        }

        return enviosPage.map(this::mapToEnvioDTO);
    }


    @Transactional(readOnly = true)
    public List<EnvioDTO> listarViaStoredProcedure(String estado) {
        List<EnvioDTO> dtos = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM SP_OBTENER_ENVIOS_POR_ESTADO(?)")) {
            ps.setString(1, estado);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    dtos.add(new EnvioDTO(
                        rs.getInt("ENVIO_ID"),
                        rs.getString("CODIGO_RASTREO"),
                        null,
                        rs.getString("DIRECCION_DESTINO"),
                        rs.getBigDecimal("COSTO"),
                        rs.getString("ESTADO_ENVIO"),
                        rs.getTimestamp("FECHA_CREACION") != null
                            ? rs.getTimestamp("FECHA_CREACION").toLocalDateTime() : null
                    ));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error ejecutando SP_OBTENER_ENVIOS_POR_ESTADO: " + e.getMessage(), e);
        }
        return dtos;
    }


    @Transactional(readOnly = true)
    public List<ResumenMetricasDTO> obtenerResumenMetricas() {
        List<ResumenMetricasDTO> metricas = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM SP_RESUMEN_METRICAS_ENVIOS()")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    metricas.add(new ResumenMetricasDTO(
                        rs.getString("ESTADO_ENVIO"),
                        rs.getLong("TOTAL_ENVIOS"),
                        rs.getBigDecimal("SUMA_FLETE")
                    ));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error ejecutando SP_RESUMEN_METRICAS_ENVIOS: " + e.getMessage(), e);
        }
        return metricas;
    }



    public EnvioResponseDTO registrarEnvio(EnvioRequestDTO dto) {
        Envio envio = new Envio();
        envio.setCodigoRastreo(dto.getCodigoRastreo());
        envio.setDireccionDestino(dto.getDireccionDestino());
        envio.setPesoKg(dto.getPesoKg());
        envio.setCosto(dto.getCosto());
        
        if (dto.getVehiculoId() != null) {
            Vehiculo vehiculo = vehiculoRepository.findById(dto.getVehiculoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Vehículo no encontrado"));
            
            if (envio.getPesoKg().compareTo(vehiculo.getCapacidadKg()) > 0) {
                throw new InvalidStateTransitionException("El peso del envío supera la capacidad máxima del vehículo.");
            }
            envio.setVehiculo(vehiculo);
        }

        if (dto.getConductorId() != null) {
            Conductor conductor = conductorRepository.findById(dto.getConductorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Conductor no encontrado"));
            envio.setConductor(conductor);
        }
        
        envio.setEstadoEnvio("PENDIENTE");
        Envio guardado = envioRepository.save(envio);
        return mapToResponseDTO(guardado);
    }

    public EnvioResponseDTO actualizarEstado(Integer id, CambioEstadoDTO cambioDTO) {
        Envio envio = envioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Envío no encontrado"));
                
        String estadoAnterior = envio.getEstadoEnvio();
        String estadoNuevo = cambioDTO.getNuevoEstado();

        // Validar transición
        if (("ENTREGADO".equals(estadoAnterior) || "CANCELADO".equals(estadoAnterior)) &&
            ("PENDIENTE".equals(estadoNuevo) || "EN_TRANSITO".equals(estadoNuevo))) {
            throw new InvalidStateTransitionException("Transición de estado no permitida para el envío " + envio.getCodigoRastreo());
        }

        envio.setEstadoEnvio(estadoNuevo);
        Envio actualizado = envioRepository.save(envio);

        // Bitácora
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        BitacoraEnvio bitacora = new BitacoraEnvio();
        bitacora.setEnvio(actualizado);
        bitacora.setEstadoAnterior(estadoAnterior);
        bitacora.setEstadoNuevo(estadoNuevo);
        bitacora.setFechaCambio(LocalDateTime.now());
        bitacora.setUsuario(usuario);
        bitacora.setObservaciones(cambioDTO.getObservaciones());
        bitacoraEnvioRepository.save(bitacora);

        return mapToResponseDTO(actualizado);
    }

    @Transactional(readOnly = true)
    public List<BitacoraResponseDTO> obtenerBitacora(Integer envioId) {
        return bitacoraEnvioRepository.findByEnvioIdOrderByFechaCambioDesc(envioId)
                .stream()
                .map(b -> new BitacoraResponseDTO(
                        b.getId(),
                        b.getEstadoAnterior(),
                        b.getEstadoNuevo(),
                        b.getFechaCambio(),
                        b.getUsuario().getNombreCompleto(),
                        b.getObservaciones()
                ))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EnvioResponseDTO obtenerEnvio(Integer id) {
        Envio envio = envioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Envío no encontrado con id: " + id));
        return mapToResponseDTO(envio);
    }

    public EnvioResponseDTO cancelarEnvio(Integer id) {
        Envio envio = envioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Envío no encontrado con id: " + id));

        if ("EN_TRANSITO".equals(envio.getEstadoEnvio())) {
            throw new InvalidStateTransitionException("No se puede cancelar un envío que ya está en tránsito.");
        }
        if ("ENTREGADO".equals(envio.getEstadoEnvio())) {
            throw new InvalidStateTransitionException("No se puede cancelar un envío ya entregado.");
        }

        envio.setEstadoEnvio("CANCELADO");
        return mapToResponseDTO(envioRepository.save(envio));
    }

    public double calcularTarifa(double pesoKg, double distanciaKm) {
        if (pesoKg <= 10.0) {
            return 2500.0;
        } else if (pesoKg <= 50.0) {
            return 7500.0;
        } else {
            return 12000.0;
        }
    }



    private EnvioResponseDTO mapToResponseDTO(Envio envio) {
        EnvioResponseDTO dto = new EnvioResponseDTO();
        dto.setId(envio.getId());
        dto.setCodigoRastreo(envio.getCodigoRastreo());
        dto.setDireccionDestino(envio.getDireccionDestino());
        dto.setPesoKg(envio.getPesoKg());
        dto.setCosto(envio.getCosto());
        dto.setEstadoEnvio(envio.getEstadoEnvio());
        if (envio.getVehiculo() != null) dto.setPlacaVehiculo(envio.getVehiculo().getPlaca());
        if (envio.getConductor() != null) dto.setNombreConductor(envio.getConductor().getNombre() + " " + envio.getConductor().getApellidos());
        return dto;
    }

    private EnvioDTO mapToEnvioDTO(Envio envio) {
        String destinatario = envio.getConductor() != null
                ? envio.getConductor().getNombre() + " " + envio.getConductor().getApellidos()
                : null;
        return new EnvioDTO(
                envio.getId(),
                envio.getCodigoRastreo(),
                destinatario,
                envio.getDireccionDestino(),
                envio.getCosto(),
                envio.getEstadoEnvio(),
                envio.getFechaCreacion()
        );
    }
}
