package com.imss.sivimss.facturacion.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CancelarFacRequest {
	
	private String folioFactura;
	private String folioFiscal;
	private String folioRelacionado;
	private MotivoCancelacion motivoCancelacion;
	
}
