package com.imss.sivimss.facturacion.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FinadoResponse {
	
	private String idFinado;
	private String nomFinado;
	private String fecFinado;
	
}
