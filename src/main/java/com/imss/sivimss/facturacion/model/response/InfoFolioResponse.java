package com.imss.sivimss.facturacion.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class InfoFolioResponse {
	
	private String nomContratante;
	private String fecOds;
	private String fecPago;
	private String concPago;
	private String totalPagado;
	private String totalServicios;
	private String rfc;
	private String correo;
	private String idVelatorio;
	private List<Map<String, Object>> metodosPago;
	private List<Map<String, Object>> servicios;
	private FinadoResponse finado;
}
