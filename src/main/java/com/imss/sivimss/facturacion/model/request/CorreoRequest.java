package com.imss.sivimss.facturacion.model.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CorreoRequest {

	private String tipoCorreo;
	private String nombre;
	private String correoPara;
	private String asunto;
	private String cuerpoCorreo;
	private String remitente;
	private List<Archivos> adjuntos;
}
