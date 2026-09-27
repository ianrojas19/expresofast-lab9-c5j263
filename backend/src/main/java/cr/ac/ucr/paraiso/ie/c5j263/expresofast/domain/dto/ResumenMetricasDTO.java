package cr.ac.ucr.paraiso.ie.c5j263.expresofast.domain.dto;

import java.math.BigDecimal;


public record ResumenMetricasDTO(
    String estado,
    Long totalEnvios,
    BigDecimal sumaFlete
) {}
