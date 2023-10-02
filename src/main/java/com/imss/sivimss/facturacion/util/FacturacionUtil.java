package com.imss.sivimss.facturacion.util;

import java.io.UnsupportedEncodingException;

import javax.xml.bind.DatatypeConverter;

import com.imss.sivimss.facturacion.model.request.CrearFacRequest;
import com.imss.sivimss.facturacion.model.request.FiltroRequest;
import com.imss.sivimss.facturacion.model.response.FacturaResponse;

public class FacturacionUtil {
	
	private static String CONSULTA_TABLA = "SELECT \r\n"
			+ "VEL.DES_VELATORIO AS velatorio,\r\n"
			+ "FAC.ID_VELATORIO AS idVelatorio,\r\n"
			+ "FAC.CVE_FOLIO AS folio,\r\n"
			+ "FAC.ID_FACTURA AS folioFactura,\r\n"
			+ "FAC.FEC_FACTURACION AS fechaFactura,\r\n"
			+ "FAC.CVE_FOLIO_FISCAL AS folioFiscal,\r\n"
			+ "FAC.CVE_RFC_CONTRATANTE AS rfc,\r\n"
			+ "FAC.DES_RAZON_SOCIAL AS razonSocial,\r\n"
			+ "ESFAC.DES_ESTATUS AS estatusFactura,\r\n"
			+ "FAC.ID_FLUJO_PAGOS AS idFlujoPagos\r\n"
			+ "FROM SVC_FACTURA FAC\r\n"
			+ "INNER JOIN SVC_VELATORIO VEL ON VEL.ID_VELATORIO = FAC.ID_VELATORIO\r\n"
			+ "INNER JOIN SVC_ESTATUS_FACTURA ESFAC ON ESFAC.ID_ESTATUS_FACTURA = FAC.ID_ESTATUS_FACTURA "
			+ " WHERE FAC.IND_ACTIVO = '1' ";
	
	public String consultaTabla(FiltroRequest filtros) {
		
		StringBuilder query = new StringBuilder(CONSULTA_TABLA);
		
		if( validarWhere(filtros) ) {
			query.append( construyeFiltros(filtros) );
		}
		
		return query.toString();
	}
	
	private Boolean validarWhere(FiltroRequest filtros) {
		
		if( (filtros.getIdVelatorio()==null || filtros.getIdVelatorio().isEmpty() )
			&& (filtros.getIdFlujoPagos()==null || filtros.getIdFlujoPagos().isEmpty() )
			&& (filtros.getFolio()==null || filtros.getFolio().isEmpty() )
			&& (filtros.getFolioFactura()==null || filtros.getFolioFactura().isEmpty() )
			&& (filtros.getFolioFiscal()==null || filtros.getFolioFiscal().isEmpty() )
			&& (filtros.getRfc()==null || filtros.getRfc().isEmpty() )
			&& (filtros.getFechaInicio()==null || filtros.getFechaInicio().isEmpty() )
			&& (filtros.getFechaFin()==null || filtros.getFechaFin().isEmpty() ) ) {
			
			return false;
		
		}
		
		return true;
	}
	
	private String construyeFiltros(FiltroRequest filtros) {
		StringBuilder query = new StringBuilder("");
		
		if( (filtros.getIdVelatorio()!=null && !filtros.getIdVelatorio().isEmpty() )) {
			query.append( " AND FAC.ID_VELATORIO = '" + filtros.getIdVelatorio() + "' " );
		}
		
		if( (filtros.getIdFlujoPagos()!=null && !filtros.getIdFlujoPagos().isEmpty() )) {
			
			if( filtros.getIdFlujoPagos().equals("2") ) {
				query.append( " AND FAC.ID_FLUJO_PAGOS IN (2,3)" );
			}else {
				query.append( " AND FAC.ID_FLUJO_PAGOS = '" + filtros.getIdFlujoPagos() +"' " );
			}
			
		}
		
		if( (filtros.getFolio()!=null && !filtros.getFolio().isEmpty() )) {
			query.append( " AND FAC.CVE_FOLIO LIKE CONCAT('" + filtros.getFolio() + "', '%') " );
		}
		
		if( (filtros.getFolioFactura()!=null && !filtros.getFolioFactura().isEmpty() )) {
			query.append( " AND FAC.ID_FACTURA LIKE CONCAT('" + filtros.getFolioFactura() + "', '%') " );
		}
		
		if( (filtros.getFolioFiscal()!=null && !filtros.getFolioFiscal().isEmpty() )) {
			query.append( " AND FAC.CVE_FOLIO_FISCAL LIKE CONCAT('" + filtros.getFolioFiscal() + "', '%') " );
		}
		
		if( (filtros.getRfc()!=null && !filtros.getRfc().isEmpty() ) ) {
			query.append( " AND FAC.CVE_RFC_CONTRATANTE LIKE CONCAT('"+ filtros.getRfc() + "', '%') " );
		}
		
		if( (filtros.getFechaInicio()!=null && !filtros.getFechaInicio().isEmpty() ) ) {
			query.append( " AND FAC.FEC_FACTURACION BETWEEN '" + filtros.getFechaInicio() + "' AND '" + filtros.getFechaFin() + "' " );
		}
		
		return query.toString();
	}
	
	public String consultaFolios(String tipoFactura) {
		
		String query = "";
		
		switch(tipoFactura) {
		
		case "1":
		case "2":
		case "3":
				query = foliosBitacora(tipoFactura);
			break;
		case "4":
				query = foliosPA();
			break;
		case "5":
			break;
		
		}
		
		return query;
	}

	
	private String foliosBitacora(String tipoFactura) {
		StringBuilder query = new StringBuilder("");
		
		query.append( "SELECT\r\n"
				+ "PB.ID_PAGO_BITACORA AS idPagoBitacora,\r\n"
				+ "PB.ID_REGISTRO AS idRegistro,\r\n"
				+ "PB.CVE_FOLIO AS folio\r\n"
				+ "FROM SVT_PAGO_BITACORA PB\r\n"
				+ "WHERE\r\n"
				+ "PB.ID_FLUJO_PAGOS = "
				+ tipoFactura
				+ "\r\n"
				+ "AND PB.CVE_ESTATUS_PAGO =4" );
		
		return query.toString();
	}
	
	private String foliosPA() {
		StringBuilder query = new StringBuilder("");
		
		query.append( "SELECT\r\n"
				+ "ID_PLAN_SFPA AS idRegistro,\r\n"
				+ "NUM_FOLIO_PLAN_SFPA AS folio\r\n"
				+ "FROM SVT_PLAN_SFPA\r\n"
				+ "WHERE\r\n"
				+ "ID_ESTATUS_PLAN_SFPA IN (4,5)\r\n"
				+ "AND IND_ACTIVO = '1'" );
		
		return query.toString();
	}
	
	public String infoPagosODS(String idPagoBitacora) {
		
		StringBuilder query = new StringBuilder("");
		
		query.append( "SELECT\r\n"
				+ "CONCAT('\"',PB.NOM_CONTRATANTE,'\"') AS nomContratante,\r\n"
				+ "PB.FEC_ODS AS fecOds,\r\n"
				+ "CONCAT('\"',PB.FEC_ACTUALIZACION,'\"') AS fecPago,\r\n"
				+ "CONCAT('\"', FP.DESC_FLUJO_PAGOS,'\"') AS concPago,\r\n"
				+ "IFNULL( \r\n"
				+ "(SELECT SUM(PD.IMP_PAGO)\r\n"
				+ "FROM  SVT_PAGO_DETALLE PD\r\n"
				+ "WHERE\r\n"
				+ "PD.ID_PAGO_BITACORA = " );
		query.append( idPagoBitacora );
		query.append( "\r\n" );
		query.append( "AND PD.CVE_ESTATUS = '4' ), 0) \r\n"
				+ "AS totalPagado,\r\n"
				+ "PB.DESC_VALOR AS totalServicios,\r\n"
				+ "PER.CVE_RFC AS rfc,\r\n"
				+ "PER.DES_CORREO AS correo,\r\n"
				+ "PB.ID_VELATORIO AS idVelatorio\r\n"
				+ "FROM SVT_PAGO_BITACORA PB\r\n"
				+ "INNER JOIN SVC_FLUJO_PAGOS FP ON FP.ID_FLUJO_PAGOS = PB.ID_FLUJO_PAGOS\r\n"
				+ "INNER JOIN SVC_ORDEN_SERVICIO OS ON OS.ID_ORDEN_SERVICIO = PB.ID_REGISTRO\r\n"
				+ "INNER JOIN SVC_CONTRATANTE CON ON CON.ID_CONTRATANTE = OS.ID_CONTRATANTE\r\n"
				+ "INNER JOIN SVC_PERSONA PER ON PER.ID_PERSONA = CON.ID_PERSONA\r\n"
				+ "WHERE\r\n"
				+ "PB.ID_PAGO_BITACORA = ");
		query.append( idPagoBitacora );
		query.append( "\r\n LIMIT 1" );
		
		return query.toString();
	}
	
	public String infoPagosCon(String idPagoBitacora) {
		
		StringBuilder query = new StringBuilder("");
		
		query.append( "SELECT\r\n"
				+ "CONCAT('\"',PB.NOM_CONTRATANTE,'\"') AS nomContratante,\r\n"
				+ "PB.FEC_ODS AS fecOds,\r\n"
				+ "CONCAT('\"',PB.FEC_ACTUALIZACION,'\"') AS fecPago,\r\n"
				+ "CONCAT('\"', FP.DESC_FLUJO_PAGOS,'\"') AS concPago,\r\n"
				+ "IFNULL( \r\n"
				+ "(SELECT SUM(PD.IMP_PAGO)\r\n"
				+ "FROM  SVT_PAGO_DETALLE PD\r\n"
				+ "WHERE\r\n"
				+ "PD.ID_PAGO_BITACORA = " );
		query.append( idPagoBitacora );
		query.append( "\r\n" );
		query.append( "AND PD.CVE_ESTATUS = '4' ), 0) \r\n"
				+ "AS totalPagado,\r\n"
				+ "PB.DESC_VALOR AS totalServicios,\r\n"
				+ "PER.CVE_RFC AS rfc,\r\n"
				+ "PER.DES_CORREO AS correo,\r\n"
				+ "PB.ID_VELATORIO AS idVelatorio\r\n"
				+ "FROM SVT_PAGO_BITACORA PB\r\n"
				+ "INNER JOIN SVC_FLUJO_PAGOS FP ON FP.ID_FLUJO_PAGOS = PB.ID_FLUJO_PAGOS\r\n"
				+ "INNER JOIN SVT_CONVENIO_PF CONV ON CONV.ID_CONVENIO_PF = PB.ID_REGISTRO\r\n"
				+ "INNER JOIN SVT_CONTRATANTE_PAQUETE_CONVENIO_PF CPC ON CPC.ID_CONVENIO_PF = CONV.ID_CONVENIO_PF\r\n"
				+ "INNER JOIN SVC_CONTRATANTE CON ON CON.ID_CONTRATANTE = CPC.ID_CONTRATANTE\r\n"
				+ "INNER JOIN SVC_PERSONA PER ON PER.ID_PERSONA = CON.ID_PERSONA "
				+ "WHERE\r\n"
				+ "PB.ID_PAGO_BITACORA = ");
		query.append( idPagoBitacora );
		query.append( "\r\n LIMIT 1" );
		
		return query.toString();
	}
	
	public String obtMetPago(String idPagoBitacora) {
		
		StringBuilder query = new StringBuilder("");
		
		query.append( "SELECT\r\n"
				+ "MP.DESC_METODO_PAGO AS metodoPago,\r\n"
				+ "PD.IMP_PAGO AS importe\r\n"
				+ "FROM\r\n"
				+ "SVT_PAGO_DETALLE PD\r\n"
				+ "INNER JOIN SVC_METODO_PAGO MP ON MP.ID_METODO_PAGO = PD.ID_METODO_PAGO\r\n"
				+ "WHERE ID_PAGO_BITACORA = " );
		query.append( idPagoBitacora );
		query.append( "\r\n AND PD.CVE_ESTATUS = '4'" );
		
		return query.toString();
	}
	
	public String obtMetPagoPA(String idPagoBitacora) {
		
		StringBuilder query = new StringBuilder("");
		
		query.append( "SELECT\r\n"
				+ "MP.DESC_METODO_PAGO AS metodoPago,\r\n"
				+ "BP.DES_IMPORTE AS importe\r\n"
				+ "FROM\r\n"
				+ "SVC_BITACORA_PAGO_ANTICIPADO BP\r\n"
				+ "INNER JOIN SVC_METODO_PAGO MP ON MP.ID_METODO_PAGO = BP.ID_METODO_PAGO\r\n"
				+ "WHERE\r\n"
				+ "ID_PLAN_SFPA = " );
		query.append( idPagoBitacora );
		
		return query.toString();
	}
	
	/**
	public String obtServiciosODS(String idRegistro) {
		
		StringBuilder query = new StringBuilder("");
		
		query.append( "SELECT\r\n"
				+ "IFNULL(CA.DES_CATEGORIA_ARTICULO, SER.REF_SERVICIO) AS grupo,\r\n"
				+ "IFNULL(AR.DES_ARTICULO, SER.DES_SERVICIO) AS concepto,\r\n"
				+ "DCP.CAN_DET_PRESUP AS cantidad,\r\n"
				+ "'' AS claveSAT,\r\n"
				+ "DCP.IMP_CARAC_PRESUP AS importe,\r\n"
				+ "DCP.IMP_CARAC_PRESUP AS total\r\n"
				+ "FROM\r\n"
				+ "SVC_CARAC_PRESUPUESTO CP\r\n"
				+ "INNER JOIN SVC_DETALLE_CARAC_PRESUP DCP ON DCP.ID_CARAC_PRESUPUESTO = CP.ID_CARAC_PRESUPUESTO\r\n"
				+ "LEFT JOIN SVT_ARTICULO AR ON AR.ID_ARTICULO = DCP.ID_ARTICULO\r\n"
				+ "LEFT JOIN SVC_CATEGORIA_ARTICULO CA ON CA.ID_CATEGORIA_ARTICULO = AR.ID_CATEGORIA_ARTICULO\r\n"
				+ "LEFT JOIN SVT_SERVICIO SER ON SER.ID_SERVICIO = DCP.ID_SERVICIO\r\n"
				+ "WHERE\r\n"
				+ "CP.ID_ORDEN_SERVICIO = " );
		query.append( idRegistro );
		
		return query.toString();
	}
	**/
	
	public String crear(CrearFacRequest datos, Integer idUsuario) {
		
		QueryHelper q = new QueryHelper("INSERT INTO SVC_FACTURA");
		
		if( datos.getTipoFactura().equals("4") ) {
			q.agregarParametroValues("ID_PAGO", "'" + datos.getIdRegistro() + "'");
		}else {
			q.agregarParametroValues("ID_PAGO", "'" + datos.getIdPagoBitacora() + "'");
		}
		
		q.agregarParametroValues("IMP_TOTAL_PAGADO", "'" + datos.getTotalPagado() + "'");
		q.agregarParametroValues("IMP_TOTAL_SERV", "'" + datos.getTotalServicios() + "'");
		q.agregarParametroValues("CVE_RFC_CONTRATANTE", "'" + datos.getRfc() + "'");
		q.agregarParametroValues("DES_CORREOE", "'" + datos.getCorreo() + "'");
		q.agregarParametroValues("DES_RAZON_SOCIAL", "'" + datos.getRazonSocial() + "'");
		q.agregarParametroValues("TIPO_PERSONA", "'" + datos.getTipoPersona() + "'");
		q.agregarParametroValues("DES_REGIMEN_FISCAL", "'" + datos.getRegimenFiscal() + "'");
		
		StringBuilder dom = new StringBuilder("");
		dom.append( "C.P. " + datos.getDomicilioFiscal().getCp() + ", " );
		dom.append( datos.getDomicilioFiscal().getCalle() + " ");
		dom.append( datos.getDomicilioFiscal().getNexterior() + ", " );
		dom.append( datos.getDomicilioFiscal().getDcolonia() + ", " );
		dom.append( datos.getDomicilioFiscal().getDmunicipio() + ", " );
		dom.append( datos.getDomicilioFiscal().getDentFed() + "." );
		
		q.agregarParametroValues("DES_DOMICILIO", "'" + dom.toString() + "'");
		q.agregarParametroValues("ID_USO_CFDI", "'" + datos.getCfdi().getIdCfdi() + "'");
		q.agregarParametroValues("ID_MET_PAGO_FAC", "'" + datos.getMetPagoFac().getIdMetPagoFac() + "'");
		q.agregarParametroValues("ID_FOR_PAGO_FAC", "'" + datos.getForPago().getIdForPago() + "'");
		q.agregarParametroValues("DES_OBSERVACIONES", "'" + datos.getObsAutomatica() + "'");
		q.agregarParametroValues("DES_COMENTARIOS", "'" + datos.getObsManual() + "'");
		q.agregarParametroValues("ID_ESTATUS_FACTURA", "'1'");
		q.agregarParametroValues("ID_FLUJO_PAGOS", "'" + datos.getTipoFactura() + "'");
		q.agregarParametroValues("ID_VELATORIO", "'" + datos.getIdVelatorio() + "'");
		q.agregarParametroValues("CVE_FOLIO", "'" + datos.getFolio() + "'");
		q.agregarParametroValues("IND_ACTIVO", "b'1'");
		
		return q.obtenerQueryInsertar();
		
	}
	
	public String obtServiciosConv(String idRegistro) {
		
		StringBuilder query = new StringBuilder("");
		
		query.append( "SELECT\r\n"
				+ "IFNULL(CA.DES_CATEGORIA_ARTICULO, SER.REF_SERVICIO) AS grupo,\r\n"
				+ "IFNULL(AR.DES_ARTICULO, SER.DES_SERVICIO) AS concepto,\r\n"
				+ "DCP.CAN_DET_PRESUP AS cantidad,\r\n"
				+ "'' AS claveSAT,\r\n"
				+ "DCP.IMP_CARAC_PRESUP AS importe,\r\n"
				+ "DCP.IMP_CARAC_PRESUP AS total\r\n"
				+ "FROM\r\n"
				+ "SVT_CONTRATANTE_PAQUETE_CONVENIO_PF CPC\r\n"
				+ "INNER JOIN SVC_CARAC_PRESUPUESTO CP ON CP.ID_PAQUETE = CPC.ID_PAQUETE\r\n"
				+ "INNER JOIN SVC_DETALLE_CARAC_PRESUP DCP ON DCP.ID_CARAC_PRESUPUESTO = CP.ID_CARAC_PRESUPUESTO\r\n"
				+ "LEFT JOIN SVT_ARTICULO AR ON AR.ID_ARTICULO = DCP.ID_ARTICULO\r\n"
				+ "LEFT JOIN SVC_CATEGORIA_ARTICULO CA ON CA.ID_CATEGORIA_ARTICULO = AR.ID_CATEGORIA_ARTICULO\r\n"
				+ "LEFT JOIN SVT_SERVICIO SER ON SER.ID_SERVICIO = DCP.ID_SERVICIO\r\n"
				+ "WHERE\r\n"
				+ "CPC.ID_CONVENIO_PF = " );
		query.append( idRegistro );
		
		return query.toString();
	}
	
	public String infoPagosRenCon(String idPagoBitacora) {
		
		StringBuilder query = new StringBuilder("");
		
		query.append( "SELECT\r\n"
				+ "CONCAT('\"',PB.NOM_CONTRATANTE,'\"') AS nomContratante,\r\n"
				+ "PB.FEC_ODS AS fecOds,\r\n"
				+ "CONCAT('\"',PB.FEC_ACTUALIZACION,'\"') AS fecPago,\r\n"
				+ "CONCAT('\"', FP.DESC_FLUJO_PAGOS,'\"') AS concPago,\r\n"
				+ "IFNULL( \r\n"
				+ "(SELECT SUM(PD.IMP_PAGO)\r\n"
				+ "FROM  SVT_PAGO_DETALLE PD\r\n"
				+ "WHERE\r\n"
				+ "PD.ID_PAGO_BITACORA = " );
		query.append( idPagoBitacora );
		query.append( "\r\n" );
		query.append( "AND PD.CVE_ESTATUS = '4' ), 0) \r\n"
				+ "AS totalPagado,\r\n"
				+ "PB.DESC_VALOR AS totalServicios,\r\n"
				+ "PER.CVE_RFC AS rfc,\r\n"
				+ "PER.DES_CORREO AS correo,\r\n"
				+ "PB.ID_VELATORIO AS idVelatorio\r\n"
				+ "FROM SVT_PAGO_BITACORA PB\r\n"
				+ "INNER JOIN SVC_FLUJO_PAGOS FP ON FP.ID_FLUJO_PAGOS = PB.ID_FLUJO_PAGOS\r\n"
				+ "INNER JOIN SVT_RENOVACION_CONVENIO_PF RCON ON RCON.ID_RENOVACION_CONVENIO_PF = PB.ID_REGISTRO\r\n"
				+ "INNER JOIN SVT_CONVENIO_PF CONV ON CONV.ID_CONVENIO_PF = RCON.ID_CONVENIO_PF\r\n"
				+ "INNER JOIN SVT_CONTRATANTE_PAQUETE_CONVENIO_PF CPC ON CPC.ID_CONVENIO_PF = CONV.ID_CONVENIO_PF\r\n"
				+ "INNER JOIN SVC_CONTRATANTE CON ON CON.ID_CONTRATANTE = CPC.ID_CONTRATANTE\r\n"
				+ "INNER JOIN SVC_PERSONA PER ON PER.ID_PERSONA = CON.ID_PERSONA "
				+ "WHERE\r\n"
				+ "PB.ID_PAGO_BITACORA = ");
		query.append( idPagoBitacora );
		query.append( "\r\n LIMIT 1" );
		
		return query.toString();
	}
	
	public String obtServiciosRenConv(String idRegistro) {
		
		StringBuilder query = new StringBuilder("");
		
		query.append( "SELECT\r\n"
				+ "IFNULL(CA.DES_CATEGORIA_ARTICULO, SER.REF_SERVICIO) AS grupo,\r\n"
				+ "IFNULL(AR.DES_ARTICULO, SER.DES_SERVICIO) AS concepto,\r\n"
				+ "DCP.CAN_DET_PRESUP AS cantidad,\r\n"
				+ "'' AS claveSAT,\r\n"
				+ "DCP.IMP_CARAC_PRESUP AS importe,\r\n"
				+ "DCP.IMP_CARAC_PRESUP AS total\r\n"
				+ "FROM\r\n"
				+ "SVT_CONTRATANTE_PAQUETE_CONVENIO_PF CPC\r\n"
				+ "INNER JOIN SVT_RENOVACION_CONVENIO_PF RCON ON RCON.ID_CONVENIO_PF = CPC.ID_CONVENIO_PF\r\n"		
				+ "INNER JOIN SVC_CARAC_PRESUPUESTO CP ON CP.ID_PAQUETE = CPC.ID_PAQUETE\r\n"
				+ "INNER JOIN SVC_DETALLE_CARAC_PRESUP DCP ON DCP.ID_CARAC_PRESUPUESTO = CP.ID_CARAC_PRESUPUESTO\r\n"
				+ "LEFT JOIN SVT_ARTICULO AR ON AR.ID_ARTICULO = DCP.ID_ARTICULO\r\n"
				+ "LEFT JOIN SVC_CATEGORIA_ARTICULO CA ON CA.ID_CATEGORIA_ARTICULO = AR.ID_CATEGORIA_ARTICULO\r\n"
				+ "LEFT JOIN SVT_SERVICIO SER ON SER.ID_SERVICIO = DCP.ID_SERVICIO\r\n"
				+ "WHERE\r\n"
				+ "RCON.ID_RENOVACION_CONVENIO_PF = " );
		query.append( idRegistro );
		
		return query.toString();
	}
	
	public String infoPagosPA(String idPagoBitacora) {
		
		StringBuilder query = new StringBuilder("");
		
		query.append( "SELECT\r\n"
				+ "CONCAT('\"', PER.NOM_PERSONA, ' ', PER.NOM_PRIMER_APELLIDO, ' ',PER.NOM_SEGUNDO_APELLIDO, '\"') AS nomContratante,\r\n"
				+ "PA.FEC_INGRESO AS fecOds,\r\n"
				+ "(\r\n"
				+ "SELECT\r\n"
				+ "FEC_ALTA\r\n"
				+ "FROM\r\n"
				+ "SVC_BITACORA_PAGO_ANTICIPADO\r\n"
				+ "WHERE \r\n"
				+ "ID_PLAN_SFPA = " );
		query.append( idPagoBitacora );
		query.append( "\r\n" );
		query.append( "ORDER BY FEC_ALTA DESC\r\n"
				+ "LIMIT 1) AS fecPago,\r\n"
				+ "CONCAT('\"', 'Nuevos convenios del Plan de Servicios Funerarios Pagos Anticipados.','\"')AS concPago,\r\n"
				+ "IFNULL(\r\n"
				+ "(SELECT SUM(DES_IMPORTE)\r\n"
				+ "FROM SVC_BITACORA_PAGO_ANTICIPADO\r\n"
				+ "WHERE\r\n"
				+ "ID_PLAN_SFPA = " );
		query.append( idPagoBitacora );
		query.append("), 0)\r\n"
				+ "AS totalPagado,\r\n"
				+ "PA.MON_PRECIO AS totalServicios,\r\n"
				+ "PER.CVE_RFC AS rfc,\r\n"
				+ "PER.DES_CORREO AS correo,\r\n"
				+ "PA.ID_VELATORIO AS idVelatorio\r\n"
				+ "FROM SVT_PLAN_SFPA PA\r\n"
				+ "INNER JOIN SVC_CONTRATANTE CON ON CON.ID_CONTRATANTE = PA.ID_TITULAR\r\n"
				+ "INNER JOIN SVC_PERSONA PER ON PER.ID_PERSONA = CON.ID_PERSONA\r\n"
				+ "WHERE\r\n"
				+ "PA.ID_PLAN_SFPA =");
		query.append( idPagoBitacora );
		query.append( "\r\n LIMIT 1" );
		
		return query.toString();
	}
	
	public String obtServiciosPA(String idRegistro) {
		
		StringBuilder query = new StringBuilder("");
		
		query.append( "SELECT\r\n"
				+ "PAQ.DES_NOM_PAQUETE AS grupo,\r\n"
				+ "PAQ.DES_PAQUETE AS concepto,\r\n"
				+ "'1' AS cantidad,\r\n"
				+ "'' AS claveSAT,\r\n"
				+ "PAQ.MON_PRECIO AS importe,\r\n"
				+ "PAQ.MON_PRECIO AS total\r\n"
				+ "FROM\r\n"
				+ "SVT_PLAN_SFPA SFPA\r\n"
				+ "INNER JOIN SVT_PAQUETE PAQ ON PAQ.ID_PAQUETE = SFPA.ID_PAQUETE\r\n"
				+ "WHERE\r\n"
				+ "SFPA.ID_PLAN_SFPA = " );
		query.append( idRegistro );
		
		return query.toString();
	}
	
	public String actFact(Integer idFactura, String pdf, FacturaResponse resPar) {
		
		String decode="";
		QueryHelper q = new QueryHelper("UPDATE SVC_FACTURA");
		q.agregarParametroValues("FEC_FACTURACION", "'" +resPar.getFecha_hora_factura() + "'");
		q.agregarParametroValues("DES_LUGAR_EXPEDICION", "'" + resPar.getLuger_emision() + "'");
		q.agregarParametroValues("CVE_FOLIO_FISCAL", "'" + resPar.getFolio_fiscal() + "'");
		try {
			decode = new String(DatatypeConverter.parseBase64Binary(resPar.getXml_timbrado()), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		q.agregarParametroValues("DESC_CADENA_XML", "'" + decode + "'");
		q.agregarParametroValues("DESC_CADENA_PDF", "'" + pdf + "'");
		q.addWhere("ID_FACTURA = " + idFactura);
		
		return q.obtenerQueryActualizar();
	}
	
	public String actPB(String idPb, Integer idUsuario) {
		
		QueryHelper q = new QueryHelper("UPDATE SVT_PAGO_BITACORA");
		q.agregarParametroValues("CVE_ESTATUS_PAGO", "5");
		q.agregarParametroValues("FEC_ACTUALIZACION", "NOW()");
		q.agregarParametroValues("ID_USUARIO_MODIFICA", idUsuario.toString());
		q.addWhere("ID_PAGO_BITACORA = " + idPb);
	
		return q.obtenerQueryActualizar();
	}
	
	public String obtServiciosODS(String idRegistro) {
		
		StringBuilder query = new StringBuilder("");
		
		query.append( "SELECT\r\n"
				+ "PAQ.DES_NOM_PAQUETE AS grupo,\r\n"
				+ "PAQ.DES_PAQUETE AS concepto,\r\n"
				+ "'1' AS cantidad,\r\n"
				+ "'' AS claveSAT,\r\n"
				+ "PAQ.MON_PRECIO AS importe,\r\n"
				+ "PAQ.MON_PRECIO AS total\r\n"
				+ "FROM\r\n"
				+ "SVC_CARAC_PRESUPUESTO CP\r\n"
				+ "INNER JOIN SVT_PAQUETE PAQ ON PAQ.ID_PAQUETE = CP.ID_PAQUETE\r\n"
				+ "WHERE\r\n"
				+ "CP.ID_ORDEN_SERVICIO = " );
		query.append( idRegistro );
		
		return query.toString();
	}
	
	public String buscarPDF(String idFactura) {
		StringBuilder query = new StringBuilder("");
		
		query.append( "SELECT\r\n"
				+ "DESC_CADENA_PDF AS pdf\r\n"
				+ "FROM SVC_FACTURA\r\n"
				+ "WHERE\r\n"
				+ "ID_FACTURA = "
				+ idFactura
				+ "\r\n"
				+ "LIMIT 1" );
		
		return query.toString();
	}
}
