package com.imss.sivimss.facturacion.model.response;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name="actions")
public class FacturaResponse {

	private String transaccion;
	private String tipo_proceso;
	private String codigo_retorno;
	private String fecha_hora_factura;
	private String estatus_factura;
	private String folio_factura;
	private String folio_ods_convenio_permiso;
	private String concepto;
	private String total_pagado;
	private String cadena_original;
	private String luger_emision;
	private String folio_fiscal;
	private String sello_digital_cfdi;
	private String sello_digital;
	private String observaciones_servicio_funerario;
	private String observaciones;
	private String xml_timbrado;
	private String zip;
	private String codigo_error;
	private String mensaje_error;
	
	@XmlElement(name = "transaccion")
	public String getTransaccion() {
		return transaccion;
	}
	public void setTransaccion(String transaccion) {
		this.transaccion = transaccion.trim();
	}
	
	@XmlElement(name = "tipo_proceso")
	public String getTipo_proceso() {
		return tipo_proceso;
	}
	public void setTipo_proceso(String tipo_proceso) {
		this.tipo_proceso = tipo_proceso.trim();
	}
	
	@XmlElement(name = "codigo_retorno")
	public String getCodigo_retorno() {
		return codigo_retorno;
	}
	public void setCodigo_retorno(String codigo_retorno) {
		this.codigo_retorno = codigo_retorno.trim();
	}
	
	@XmlElement(name = "fecha_hora_factura")
	public String getFecha_hora_factura() {
		return fecha_hora_factura;
	}
	public void setFecha_hora_factura(String fecha_hora_factura) {
		this.fecha_hora_factura = fecha_hora_factura.trim();
	}
	
	@XmlElement(name = "estatus_factura")
	public String getEstatus_factura() {
		return estatus_factura;
	}
	public void setEstatus_factura(String estatus_factura) {
		this.estatus_factura = estatus_factura.trim();
	}
	
	@XmlElement(name = "folio_factura")
	public String getFolio_factura() {
		return folio_factura;
	}
	public void setFolio_factura(String folio_factura) {
		this.folio_factura = folio_factura.trim();
	}
	
	@XmlElement(name = "folio_ods_convenio_permiso")
	public String getFolio_ods_convenio_permiso() {
		return folio_ods_convenio_permiso;
	}
	public void setFolio_ods_convenio_permiso(String folio_ods_convenio_permiso) {
		this.folio_ods_convenio_permiso = folio_ods_convenio_permiso.trim();
	}
	
	@XmlElement(name = "concepto")
	public String getConcepto() {
		return concepto;
	}
	public void setConcepto(String concepto) {
		this.concepto = concepto.trim();
	}
	
	@XmlElement(name = "total_pagado")
	public String getTotal_pagado() {
		return total_pagado;
	}
	public void setTotal_pagado(String total_pagado) {
		this.total_pagado = total_pagado.trim();
	}
	
	@XmlElement(name = "cadena_original")
	public String getCadena_original() {
		return cadena_original;
	}
	public void setCadena_original(String cadena_original) {
		this.cadena_original = cadena_original.trim();
	}
	
	@XmlElement(name = "luger_emision")
	public String getLuger_emision() {
		return luger_emision.trim();
	}
	public void setLuger_emision(String luger_emision) {
		this.luger_emision = luger_emision;
	}
	
	@XmlElement(name = "folio_fiscal")
	public String getFolio_fiscal() {
		return folio_fiscal;
	}
	public void setFolio_fiscal(String folio_fiscal) {
		this.folio_fiscal = folio_fiscal.trim();
	}
	
	@XmlElement(name = "sello_digital_cfdi")
	public String getSello_digital_cfdi() {
		return sello_digital_cfdi;
	}
	public void setSello_digital_cfdi(String sello_digital_cfdi) {
		this.sello_digital_cfdi = sello_digital_cfdi.trim();
	}
	
	@XmlElement(name = "sello_digital")
	public String getSello_digital() {
		return sello_digital;
	}
	public void setSello_digital(String sello_digital) {
		this.sello_digital = sello_digital;
	}
	
	@XmlElement(name = "observaciones_servicio_funerario")
	public String getObservaciones_servicio_funerario() {
		return observaciones_servicio_funerario;
	}
	public void setObservaciones_servicio_funerario(String observaciones_servicio_funerario) {
		this.observaciones_servicio_funerario = observaciones_servicio_funerario;
	}
	
	@XmlElement(name = "observaciones")
	public String getObservaciones() {
		return observaciones;
	}
	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}
	
	@XmlElement(name = "xml_timbrado")
	public String getXml_timbrado() {
		return xml_timbrado;
	}
	public void setXml_timbrado(String xml_timbrado) {
		this.xml_timbrado = xml_timbrado;
	}
	
	@XmlElement(name = "zip")
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	
	@XmlElement(name = "codigo_error")
	public String getCodigo_error() {
		return codigo_error;
	}
	public void setCodigo_error(String codigo_error) {
		this.codigo_error = codigo_error;
	}
	
	@XmlElement(name = "mensaje_error")
	public String getMensaje_error() {
		return mensaje_error;
	}
	public void setMensaje_error(String mensaje_error) {
		this.mensaje_error = mensaje_error;
	}
	
}
