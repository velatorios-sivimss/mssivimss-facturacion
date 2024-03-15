package com.imss.sivimss.facturacion.model.entity;

import lombok.Data;

@Data
public class Factura {
    
    private Integer ID_FACTURA;
	private Integer ID_PAGO;
	private String FEC_FACTURACION;
	private String IMP_TOTAL_PAGADO;
	private String IMP_TOTAL_SERV;
	private String CVE_RFC_CONTRATANTE;
	private String REF_CORREOE;
	private String REF_RAZON_SOCIAL;
	private String TIP_PERSONA;
	private String REF_REGIMEN_FISCAL;
	private String REF_DOMICILIO;
	private Integer IND_USO_CFDI;
	private Integer ID_MET_PAGO_FAC;
	private Integer ID_FOR_PAGO_FAC;
	private String REF_OBSERVACIONES;
	private String REF_COMENTARIOS;
	private Integer ID_MOTIVO_CANCELACION;
	private String CVE_FOLIO_RELACIONADO;
	private Integer ID_ESTATUS_FACTURA;
	private Integer ID_FLUJO_PAGOS;
	private String REF_LUGAR_EXPEDICION;
	private String CVE_FOLIO_FISCAL;
	private String REF_CADENA_XML;
	private String REF_CADENA_PDF;
	private String FEC_CANCELACION;
	private String REF_PARTIDA_PRES;
	private String REF_CUENTA_CONTABLE;
	private Integer ID_VELATORIO;
	private String CVE_FOLIO;
	private Integer IND_ACTIVO;
	private Integer ID_USUARIO_ALTA;
	private Integer ID_USUARIO_MODIFICA;
	private Integer ID_USUARIO_BAJA;
	private String FEC_ALTA;
	private String FEC_BAJA;
	private String FEC_ACTUALIZACION;

    
}

