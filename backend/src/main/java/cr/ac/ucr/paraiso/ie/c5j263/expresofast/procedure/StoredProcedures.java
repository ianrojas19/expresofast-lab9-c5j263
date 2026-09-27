package cr.ac.ucr.paraiso.ie.c5j263.expresofast.procedure;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class StoredProcedures {


    public static ResultSet obtenerEnviosPorEstado(Connection conn, String pEstado) throws Exception {
        PreparedStatement ps = conn.prepareStatement(
            "SELECT e.envio_id, e.codigo_rastreo, e.direccion_destino, " +
            "e.peso_kg, e.costo, e.estado_envio, e.vehiculo_id, " +
            "e.conductor_id, e.fecha_creacion, e.fecha_modificacion " +
            "FROM envio e " +
            "WHERE e.estado_envio = ? " +
            "ORDER BY e.fecha_creacion DESC"
        );
        ps.setString(1, pEstado);
        return ps.executeQuery();
    }


    public static ResultSet resumenMetricasEnvios(Connection conn) throws Exception {
        PreparedStatement ps = conn.prepareStatement(
            "SELECT estado_envio, COUNT(*) AS total_envios, " +
            "COALESCE(SUM(costo), 0) AS suma_flete " +
            "FROM envio " +
            "GROUP BY estado_envio " +
            "ORDER BY estado_envio"
        );
        return ps.executeQuery();
    }
}
