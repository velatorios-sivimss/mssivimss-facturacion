package com.imss.sivimss.facturacion.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UbicacionRequest {
	
	private String calle;
    private String email;
    private String paisResidencia;
    private String telefono1;
    private String telefono2;
    private String calr;
    private String ccolonia;
    private String centFed;
    private String clocalidad;
    private String cmunicipio;
    private String ccrh;
    private String cp;
    private String dalr;
    private String dcolonia;
    private String dentFed;
    private String dentreCalle1;
    private String dentreCalle2;
    private String dinmueble;
    private String dlocalidad;
    private String dmunicipio;
    private String dreferencia;
    private String dvialidad;
    private String dcrh;
    private String nexterior;
    private String ninterior;
    private String tinmueble;
    private String ttel1;
    private String ttel2;
    private String faltaDom;
    private String tvialidad;
    private String caractDomicilio;

}
