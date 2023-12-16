package com.imss.sivimss.facturacion.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Archivos {

	private String nombreAdjunto;
	private String adjuntoBase64;
	
}
