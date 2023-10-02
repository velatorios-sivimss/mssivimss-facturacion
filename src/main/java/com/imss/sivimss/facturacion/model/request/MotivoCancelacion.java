package com.imss.sivimss.facturacion.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MotivoCancelacion {
	
	private String idMotivoCancelacion;
	private String clave;
	private String descripcion;
	private String aplicaFolio;

}
