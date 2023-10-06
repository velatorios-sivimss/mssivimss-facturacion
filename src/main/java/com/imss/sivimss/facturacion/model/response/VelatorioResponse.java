package com.imss.sivimss.facturacion.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class VelatorioResponse {
	
	private String nomVelatorio;
	private String cp;
	private String calle;
	private String numExterior;
	private String numInterior;
	private String colonia;
	private String municipio;
	private String estado;
	
}
