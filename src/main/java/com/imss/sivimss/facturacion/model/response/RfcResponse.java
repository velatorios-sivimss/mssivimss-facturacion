package com.imss.sivimss.facturacion.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.imss.sivimss.facturacion.service.rfc.Ubicacion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RfcResponse {
	
	private String razonSocial;
	private String tipoPersona;
	private String regimenFiscal;
	private Ubicacion domicilioFiscal;
	
}
