package cr.ac.ucr.paraiso.ie.c5j263.expresofast.data;

import cr.ac.ucr.paraiso.ie.c5j263.expresofast.domain.Envio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EnvioRepository extends JpaRepository<Envio, Integer> {

    @Query("SELECT e FROM Envio e JOIN FETCH e.vehiculo v JOIN FETCH v.empresa JOIN FETCH e.conductor")
    List<Envio> findAllWithDetails();

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Envio e SET e.estadoEnvio = :nuevoEstado WHERE e.vehiculo.id = :vehiculoId")
    void actualizarEstadoPorVehiculo(@Param("vehiculoId") Integer vehiculoId, @Param("nuevoEstado") String nuevoEstado);




    Page<Envio> findByEstadoEnvio(String estadoEnvio, Pageable pageable);


    @Query("SELECT e FROM Envio e WHERE " +
           "LOWER(e.codigoRastreo) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
           "LOWER(e.direccionDestino) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
           "LOWER(e.estadoEnvio) LIKE LOWER(CONCAT('%', :busqueda, '%'))")
    Page<Envio> buscarPorTermino(@Param("busqueda") String busqueda, Pageable pageable);


    @Query("SELECT e FROM Envio e WHERE " +
           "e.estadoEnvio = :estado AND (" +
           "LOWER(e.codigoRastreo) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
           "LOWER(e.direccionDestino) LIKE LOWER(CONCAT('%', :busqueda, '%')))")
    Page<Envio> buscarPorTerminoYEstado(@Param("busqueda") String busqueda, @Param("estado") String estado, Pageable pageable);
}
