package com.imss.sivimss.facturacion.model.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CrearFacRequest {
	
	private String tipoFactura;
	private String folio;
	private String idPagoBitacora;
	private String idRegistro;
	private String nomContratante;
	private String idVelatorio;
	private String totalPagado;
	private String totalServicios;
	private List<ServiciosRequest> servicios;
	private String rfc;
	private String correo;
	private String razonSocial;
	private String tipoPersona;
	private String regimenFiscal;
	private UbicacionRequest domicilioFiscal;
	private CfdiFacRequest cfdi;
	private MetPagoFacRequest metPagoFac;
	private ForPagoRequest forPago;
	private String obsAutomatica;
	private String obsManual;
}
