package com.imss.sivimss.facturacion.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FacturaBDResponse {
	
	private String total;
	private String razonSocial;
	private String rfc;
	private String fecCan;
}
