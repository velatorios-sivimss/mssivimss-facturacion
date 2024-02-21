package com.imss.sivimss.facturacion.util;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import com.imss.sivimss.facturacion.model.request.CancelarFacRequest;
import com.imss.sivimss.facturacion.model.request.CrearFacRequest;
import com.imss.sivimss.facturacion.model.request.FiltroRequest;
import com.imss.sivimss.facturacion.model.response.FacturaResponse;

public class FacturacionUtil {
	
	private static String CONSULTA_TABLA = "SELECT\r\n"
			+ "VEL.DES_VELATORIO AS nomVelatorio,\r\n"
			+ "FAC.CVE_FOLIO AS folio,\r\n"
			+ "FAC.ID_FACTURA AS folioFactura,\r\n"
			+ "FAC.FEC_FACTURACION AS fechaFactura,\r\n"
			+ "FAC.CVE_FOLIO_FISCAL AS folioFiscal,\r\n"
			+ "FAC.CVE_RFC_CONTRATANTE AS rfc,\r\n"
			+ "FAC.REF_RAZON_SOCIAL AS razonSocial,\r\n"
			+ "ESFAC.DES_ESTATUS AS estatusFactura\r\n"
			+ "FROM SVC_FACTURA FAC\r\n"
			+ "INNER JOIN SVC_ESTATUS_FACTURA ESFAC ON ESFAC.ID_ESTATUS_FACTURA = FAC.ID_ESTATUS_FACTURA\r\n"
			+ "INNER JOIN SVC_VELATORIO VEL ON VEL.ID_VELATORIO = FAC.ID_VELATORIO\r\n"
			+ "WHERE FAC.IND_ACTIVO = '1' \r\n";
	
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
				+ "PB.IMP_VALOR AS totalServicios,\r\n"
				+ "IF( PER.CVE_RFC IS NULL OR LTRIM(PER.CVE_RFC) = '', NULL, LTRIM(PER.CVE_RFC)) AS rfc,\r\n"
				+ "IF( PER.REF_CORREO IS NULL OR LTRIM(PER.REF_CORREO) = '', NULL, LTRIM(PER.REF_CORREO)) AS correo,\r\n"
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
				+ "PB.IMP_VALOR AS totalServicios,\r\n"
				+ "IF( PER.CVE_RFC IS NULL OR LTRIM(PER.CVE_RFC) = '', NULL, LTRIM(PER.CVE_RFC)) AS rfc,\r\n"
				+ "IF( PER.REF_CORREO IS NULL OR LTRIM(PER.REF_CORREO) = '', NULL, LTRIM(PER.REF_CORREO)) AS correo,\r\n"
				+ "PB.ID_VELATORIO AS idVelatorio\r\n"
				+ "FROM SVT_PAGO_BITACORA PB\r\n"
				+ "INNER JOIN SVC_FLUJO_PAGOS FP ON FP.ID_FLUJO_PAGOS = PB.ID_FLUJO_PAGOS\r\n"
				+ "INNER JOIN SVT_CONVENIO_PF CONV ON CONV.ID_CONVENIO_PF = PB.ID_REGISTRO\r\n"
				+ "INNER JOIN SVT_CONTRA_PAQ_CONVENIO_PF CPC ON CPC.ID_CONVENIO_PF = CONV.ID_CONVENIO_PF\r\n"
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
				+ "MP.DES_METODO_PAGO AS metodoPago,\r\n"
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
		
		String query="SELECT\r\n"
				+ "	FORMAT((@I := @I + 1), 0) AS numeroPago,\r\n"
				+ "	TBL1.*\r\n"
				+ "FROM\r\n"
				+ "	(\r\n"
				+ "	SELECT\r\n"
				+ "		SMP.DES_METODO_PAGO AS desMetodoPago,\r\n"
				+ "		CASE WHEN SBPA.IMP_PAGO IS NOT NULL\r\n"
				+ "		THEN CONCAT('$', FORMAT(SBPA.IMP_PAGO,2))\r\n"
				+ "		WHEN SBPA.IMP_AUTORIZADO_VALE_PARITARIO IS NOT NULL\r\n"
				+ "		THEN  CONCAT('$', FORMAT(SBPA.IMP_AUTORIZADO_VALE_PARITARIO,2))\r\n"
				+ "		ELSE '' END AS importeTotal\r\n"
				+ "	FROM\r\n"
				+ "		SVC_BITACORA_PAGO_ANTICIPADO SBPA\r\n"
				+ "	INNER JOIN SVT_PAGO_SFPA SPS ON\r\n"
				+ "		SBPA.ID_PAGO_SFPA = SPS.ID_PAGO_SFPA\r\n"
				+ "	INNER JOIN SVC_METODO_PAGO SMP ON\r\n"
				+ "		SBPA.ID_METODO_PAGO = SMP.ID_METODO_PAGO\r\n"
				+ "	INNER JOIN SVC_ESTATUS_PAGO_ANTICIPADO SPA ON\r\n"
				+ "		SPS.ID_ESTATUS_PAGO = SPA.ID_ESTATUS_PAGO_ANTICIPADO\r\n"
				+ "	WHERE\r\n"
				+ "		SPS.ID_PAGO_SFPA = " +idPagoBitacora+" AND SBPA.IND_ACTIVO = 1 \r\n "
				+ "	GROUP BY\r\n "
				+ "		SBPA.ID_BITACORA_PAGO\r\n"
				+ "	ORDER BY\r\n"
				+ "		SBPA.ID_BITACORA_PAGO,\r\n"
				+ "		SBPA.FEC_PAGO,\r\n"
				+ "		SBPA.IND_ACTIVO ) TBL1,\r\n"
				+ "	(\r\n"
				+ "	SELECT\r\n"
				+ "		@I := 0) C";
		
		return query;
	}
	
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
		q.agregarParametroValues("REF_CORREOE", "'" + datos.getCorreo() + "'");
		q.agregarParametroValues("REF_RAZON_SOCIAL", "'" + datos.getRazonSocial() + "'");
		q.agregarParametroValues("TIP_PERSONA", "'" + datos.getTipoPersona() + "'");
		q.agregarParametroValues("REF_REGIMEN_FISCAL", "'" + datos.getRegimenFiscal() + "'");
		
		StringBuilder dom = new StringBuilder("");
		dom.append( "C.P. " + datos.getDomicilioFiscal().getCp() + ", " );
		dom.append( datos.getDomicilioFiscal().getCalle() + " ");
		dom.append( datos.getDomicilioFiscal().getNexterior() + ", " );
		dom.append( datos.getDomicilioFiscal().getDcolonia() + ", " );
		dom.append( datos.getDomicilioFiscal().getDmunicipio() + ", " );
		dom.append( datos.getDomicilioFiscal().getDentFed() + "." );
		
		q.agregarParametroValues("REF_DOMICILIO", "'" + dom.toString() + "'");
		q.agregarParametroValues("IND_USO_CFDI", "'" + datos.getCfdi().getIdCfdi() + "'");
		q.agregarParametroValues("ID_MET_PAGO_FAC", "'" + datos.getMetPagoFac().getIdMetPagoFac() + "'");
		q.agregarParametroValues("ID_FOR_PAGO_FAC", "'" + datos.getForPago().getIdForPago() + "'");
		q.agregarParametroValues("REF_OBSERVACIONES", "'" + datos.getObsAutomatica() + "'");
		q.agregarParametroValues("REF_COMENTARIOS", "'" + datos.getObsManual() + "'");
		q.agregarParametroValues("ID_ESTATUS_FACTURA", "'1'");
		q.agregarParametroValues("ID_FLUJO_PAGOS", "'" + datos.getTipoFactura() + "'");
		q.agregarParametroValues("ID_VELATORIO", "'" + datos.getIdVelatorio() + "'");
		q.agregarParametroValues("CVE_FOLIO", "'" + datos.getFolio() + "'");
		q.agregarParametroValues("IND_ACTIVO", "b'1'");
		q.agregarParametroValues("ID_USUARIO_ALTA", idUsuario.toString());
		q.agregarParametroValues("FEC_ALTA", "NOW()");
		
		return q.obtenerQueryInsertar();
		
	}
	
	public String obtServiciosConv(String idRegistro) {
		
		StringBuilder query = new StringBuilder("");
		
		query.append( "SELECT\r\n"
				+ "'Paquete' AS grupo,\r\n"
				+ "PAQ.REF_PAQUETE_NOMBRE AS concepto,\r\n"
				+ "'1' AS cantidad,\r\n"
				+ "CONCAT(CS.CVE_PRODUCTOS_SERVICIOS, ' ', CS.REF_UNIDAD_SAT) AS claveSAT,\r\n"
				+ "CS.CVE_PRODUCTOS_SERVICIOS AS claveProd,\r\n"
				+ "PAQ.MON_PRECIO AS importe,\r\n"
				+ "PAQ.MON_PRECIO AS total\r\n"
				+ "FROM\r\n"
				+ "SVT_CONTRA_PAQ_CONVENIO_PF CPC\r\n"
				+ "INNER JOIN SVT_PAQUETE PAQ ON PAQ.ID_PAQUETE = CPC.ID_PAQUETE\r\n"
				+ "INNER JOIN SVC_CLAVES_PRODUCTOS_SERVICIOS CS ON CS.ID_PRODUCTOS_SERVICIOS = PAQ.ID_PRODUCTOS_SERVICIOS\r\n"
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
				+ "PB.IMP_VALOR AS totalServicios,\r\n"
				+ "IF( PER.CVE_RFC IS NULL OR LTRIM(PER.CVE_RFC) = '', NULL, LTRIM(PER.CVE_RFC)) AS rfc,\r\n"
				+ "IF( PER.REF_CORREO IS NULL OR LTRIM(PER.REF_CORREO) = '', NULL, LTRIM(PER.REF_CORREO)) AS correo,\r\n"
				+ "PB.ID_VELATORIO AS idVelatorio\r\n"
				+ "FROM SVT_PAGO_BITACORA PB\r\n"
				+ "INNER JOIN SVC_FLUJO_PAGOS FP ON FP.ID_FLUJO_PAGOS = PB.ID_FLUJO_PAGOS\r\n"
				+ "INNER JOIN SVT_RENOVACION_CONVENIO_PF RCON ON RCON.ID_RENOVACION_CONVENIO_PF = PB.ID_REGISTRO\r\n"
				+ "INNER JOIN SVT_CONVENIO_PF CONV ON CONV.ID_CONVENIO_PF = RCON.ID_CONVENIO_PF\r\n"
				+ "INNER JOIN SVT_CONTRA_PAQ_CONVENIO_PF CPC ON CPC.ID_CONVENIO_PF = CONV.ID_CONVENIO_PF\r\n"
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
				+ "'Paquete' AS grupo,\r\n"
				+ "PAQ.REF_PAQUETE_NOMBRE AS concepto,\r\n"
				+ "'1' AS cantidad,\r\n"
				+ "CONCAT(CS.CVE_PRODUCTOS_SERVICIOS, ' ', CS.REF_UNIDAD_SAT) AS claveSAT,\r\n"
				+ "CS.CVE_PRODUCTOS_SERVICIOS AS claveProd,\r\n"
				+ "PAQ.MON_PRECIO AS importe,\r\n"
				+ "PAQ.MON_PRECIO AS total\r\n"
				+ "FROM\r\n"
				+ "SVT_CONTRA_PAQ_CONVENIO_PF CPC\r\n"
				+ "INNER JOIN SVT_PAQUETE PAQ ON PAQ.ID_PAQUETE = CPC.ID_PAQUETE\r\n"
				+ "INNER JOIN SVT_RENOVACION_CONVENIO_PF RCON ON RCON.ID_CONVENIO_PF = CPC.ID_CONVENIO_PF\r\n"
				+ "INNER JOIN SVC_CLAVES_PRODUCTOS_SERVICIOS CS ON CS.ID_PRODUCTOS_SERVICIOS = PAQ.ID_PRODUCTOS_SERVICIOS\r\n"
				+ "WHERE\r\n"
				+ "RCON.ID_RENOVACION_CONVENIO_PF = " );
		query.append( idRegistro );
		
		return query.toString();
	}
	
	public String infoPagosPA(String idPlan,String idParcialidad) {
		
		SelectQueryUtil selectQueryUtil= new SelectQueryUtil();
		selectQueryUtil.select(
				"CONCAT('\"', PER.NOM_PERSONA, ' ', PER.NOM_PRIMER_APELLIDO, ' ', PER.NOM_SEGUNDO_APELLIDO, '\"') AS nomContratante",
				"PA.FEC_INGRESO AS fecOds",
			"sps.FEC_PARCIALIDAD AS fecPago",
			"sps.REF_FOLIO_RECIBO AS folio",
			"	CONCAT('\"', 'Pago de la Parcialidad con folio ', sps.REF_FOLIO_RECIBO, ' Plan de Servicios Funerarios Pagos Anticipados.', '\"')AS concPago",
			"sps.IMP_MONTO_MENSUAL AS totalPagado",
			"	PA.IMP_PRECIO AS totalServicios",
			"	IF( PER.CVE_RFC IS NULL	OR LTRIM(PER.CVE_RFC) = '', NULL, LTRIM(PER.CVE_RFC)) AS rfc",
			"	IF( PER.REF_CORREO IS NULL OR LTRIM(PER.REF_CORREO) = '', NULL,LTRIM(PER.REF_CORREO)) AS correo",
			"	PA.ID_VELATORIO AS idVelatorio"
				)
		.from("SVT_PLAN_SFPA PA")
		.innerJoin("SVC_CONTRATANTE CON", "CON.ID_CONTRATANTE = PA.ID_TITULAR")
		.innerJoin("SVC_PERSONA PER", "PER.ID_PERSONA = CON.ID_PERSONA")
		.innerJoin("SVT_PAGO_SFPA sps", "PA.ID_PLAN_SFPA = sps.ID_PLAN_SFPA")
		.where("PA.ID_PLAN_SFPA = "+idPlan
				+ " AND sps.ID_PAGO_SFPA = "+idParcialidad);
		
		return selectQueryUtil.build();
	}
	
	public String obtServiciosPA(String idRegistro) {
		
		StringBuilder query = new StringBuilder("");
		
		query.append( "SELECT\r\n"
				+ "'Paquete' AS grupo,\r\n"
				+ "PAQ.REF_PAQUETE_NOMBRE AS concepto,\r\n"
				+ "'1' AS cantidad,\r\n"
				+ "CONCAT(CS.CVE_PRODUCTOS_SERVICIOS, ' ', CS.REF_UNIDAD_SAT) AS claveSAT,\r\n"
				+ "CS.CVE_PRODUCTOS_SERVICIOS AS claveProd,\r\n"
				+ "SFPA.IMP_PRECIO AS importe,\r\n"
				+ "SFPA.IMP_PRECIO AS total\r\n"
				+ "FROM\r\n"
				+ "SVT_PLAN_SFPA SFPA\r\n"
				+ "INNER JOIN SVT_PAQUETE PAQ ON PAQ.ID_PAQUETE = SFPA.ID_PAQUETE\r\n"
				+ "INNER JOIN SVC_CLAVES_PRODUCTOS_SERVICIOS CS ON CS.ID_PRODUCTOS_SERVICIOS = PAQ.ID_PRODUCTOS_SERVICIOS\r\n"
				+ "WHERE\r\n"
				+ "SFPA.ID_PLAN_SFPA = " );
		query.append( idRegistro );
		
		return query.toString();
	}
	
	public String actFact(Integer idFactura, String pdf, FacturaResponse resPar) {
		
		String decode="";
		QueryHelper q = new QueryHelper("UPDATE SVC_FACTURA");
		q.agregarParametroValues("FEC_FACTURACION", "'" +resPar.getFecha_hora_factura() + "'");
		q.agregarParametroValues("REF_LUGAR_EXPEDICION", "'" + resPar.getLuger_emision() + "'");
		q.agregarParametroValues("CVE_FOLIO_FISCAL", "'" + resPar.getFolio_fiscal() + "'");
		try {
			decode = new String(DatatypeConverter.parseBase64Binary(resPar.getXml_timbrado()), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		q.agregarParametroValues("REF_CADENA_XML", "'" + decode + "'");
		q.agregarParametroValues("REF_CADENA_PDF", "'" + pdf + "'");
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


//		SELECT
//		    'Paquete' AS grupo,
//		    PAQ.REF_PAQUETE_NOMBRE AS concepto,
//		    '1' AS cantidad,
//		    CONCAT(
//		        CS.CVE_PRODUCTOS_SERVICIOS,
//		        ' ',
//		        CS.REF_UNIDAD_SAT
//		    ) AS claveSAT,
//		    CS.CVE_PRODUCTOS_SERVICIOS AS claveProd,
//		    PAQ.MON_PRECIO AS importe,
//		    PAQ.MON_PRECIO AS total
//		FROM
//		    SVC_CARAC_PRESUPUESTO CP
//		INNER JOIN SVT_PAQUETE PAQ ON
//		    PAQ.ID_PAQUETE = CP.ID_PAQUETE
//		INNER JOIN SVC_CLAVES_PRODUCTOS_SERVICIOS CS ON
//		    CS.ID_PRODUCTOS_SERVICIOS = PAQ.ID_PRODUCTOS_SERVICIOS
//		WHERE
//		    0 <(
//		    SELECT
//		        COUNT(sdcp.ID_DETALLE_CARAC)
//		    FROM
//		        SVC_CARACTERISTICAS_PAQUETE scp
//		    INNER JOIN SVC_DETALLE_CARAC_PAQ sdcp ON
//		        scp.ID_CARAC_PAQUETE = sdcp.ID_CARAC_PAQUETE
//		    WHERE
//		        scp.ID_ORDEN_SERVICIO = 112 AND sdcp.IND_ACTIVO IS NOT NULL
//		) AND CP.ID_ORDEN_SERVICIO = 112 AND CP.IND_ACTIVO = 1
//		UNION ALL
//		SELECT
//		    'Articulo' AS grupo,
//		    ART.REF_ARTICULO AS concepto,
//		    DP.CAN_DET_PRESUP AS cantidad,
//		    CONCAT(
//		        CS.CVE_PRODUCTOS_SERVICIOS,
//		        ' ',
//		        CS.REF_UNIDAD_SAT
//		    ) AS claveSAT,
//		    CS.CVE_PRODUCTOS_SERVICIOS AS claveProd,
//		    DP.IMP_CARAC_PRESUP AS importe,
//		    DP.IMP_CARAC_PRESUP AS total
//		FROM
//		    SVC_CARAC_PRESUPUESTO CP
//		INNER JOIN SVC_DETALLE_CARAC_PRESUP DP ON
//		    DP.ID_CARAC_PRESUPUESTO = CP.ID_CARAC_PRESUPUESTO
//		INNER JOIN SVT_ARTICULO ART ON
//		    ART.ID_ARTICULO = DP.ID_ARTICULO
//		INNER JOIN SVC_CLAVES_PRODUCTOS_SERVICIOS CS ON
//		    CS.ID_PRODUCTOS_SERVICIOS = ART.ID_PRODUCTOS_SERVICIOS
//		WHERE
//		    CP.ID_ORDEN_SERVICIO = 112 AND CP.IND_ACTIVO = 1 AND DP.IND_ACTIVO = 1 AND DP.DES_PROVIENE = 'presupuesto'
//		UNION ALL
//		SELECT
//		    'Servicio' AS grupo,
//		    SER.REF_SERVICIO AS concepto,
//		    DP.CAN_DET_PRESUP AS cantidad,
//		    CONCAT(
//		        CS.CVE_PRODUCTOS_SERVICIOS,
//		        ' ',
//		        CS.REF_UNIDAD_SAT
//		    ) AS claveSAT,
//		    CS.CVE_PRODUCTOS_SERVICIOS AS claveProd,
//		    DP.IMP_CARAC_PRESUP AS importe,
//		    DP.IMP_CARAC_PRESUP AS total
//		FROM
//		    SVC_CARAC_PRESUPUESTO CP
//		INNER JOIN SVC_DETALLE_CARAC_PRESUP DP ON
//		    DP.ID_CARAC_PRESUPUESTO = CP.ID_CARAC_PRESUPUESTO
//		INNER JOIN SVT_SERVICIO SER ON
//		    SER.ID_SERVICIO = DP.ID_SERVICIO
//		INNER JOIN SVC_CLAVES_PRODUCTOS_SERVICIOS CS ON
//		    CS.ID_PRODUCTOS_SERVICIOS = SER.ID_PRODUCTOS_SERVICIOS
//		WHERE
//		    CP.ID_ORDEN_SERVICIO = 112 AND CP.IND_ACTIVO = 1 AND DP.IND_ACTIVO = 1 AND DP.DES_PROVIENE = 'presupuesto'
//		
		
		
		StringBuilder query = new StringBuilder("");
		
		query.append( "SELECT\r\n"
				+ "'Paquete' AS grupo,\r\n"
				+ "PAQ.REF_PAQUETE_NOMBRE AS concepto,\r\n"
				+ "'1' AS cantidad,\r\n"
				+ "CONCAT(CS.CVE_PRODUCTOS_SERVICIOS, ' ', CS.REF_UNIDAD_SAT) AS claveSAT,\r\n"
				+ "CS.CVE_PRODUCTOS_SERVICIOS AS claveProd,\r\n"
				+ "PAQ.MON_PRECIO AS importe,\r\n"
				+ "PAQ.MON_PRECIO AS total\r\n"
				+ "FROM\r\n"
				+ "SVC_CARAC_PRESUPUESTO CP\r\n"
				+ "INNER JOIN SVT_PAQUETE PAQ ON PAQ.ID_PAQUETE = CP.ID_PAQUETE\r\n"
				+ "INNER JOIN SVC_CLAVES_PRODUCTOS_SERVICIOS CS ON CS.ID_PRODUCTOS_SERVICIOS = PAQ.ID_PRODUCTOS_SERVICIOS\r\n"
				+ "WHERE\r\n"
				+ "CP.ID_ORDEN_SERVICIO = " );
		query.append( idRegistro );
		query.append( "\r\n" );
		query.append( "AND CP.IND_ACTIVO = 1\r\n"
				+ "UNION ALL\r\n"
				+ "SELECT\r\n"
				+ "'Articulo' AS grupo,\r\n"
				+ "ART.REF_ARTICULO AS concepto,\r\n"
				+ "DP.CAN_DET_PRESUP AS cantidad,\r\n"
				+ "CONCAT(CS.CVE_PRODUCTOS_SERVICIOS, ' ', CS.REF_UNIDAD_SAT) AS claveSAT,\r\n"
				+ "CS.CVE_PRODUCTOS_SERVICIOS AS claveProd,\r\n"
				+ "(DP.IMP_CARAC_PRESUP/DP.CAN_DET_PRESUP) AS importe,\r\n"
				+ "DP.IMP_CARAC_PRESUP AS total\r\n"
				+ "FROM\r\n"
				+ "SVC_CARAC_PRESUPUESTO CP\r\n"
				+ "INNER JOIN SVC_DETALLE_CARAC_PRESUP DP ON DP.ID_CARAC_PRESUPUESTO = CP.ID_CARAC_PRESUPUESTO\r\n"
				+ "INNER JOIN SVT_ARTICULO ART ON ART.ID_ARTICULO = DP.ID_ARTICULO\r\n"
				+ "INNER JOIN SVC_CLAVES_PRODUCTOS_SERVICIOS CS ON CS.ID_PRODUCTOS_SERVICIOS = ART.ID_PRODUCTOS_SERVICIOS\r\n"
				+ "WHERE\r\n"
				+ "CP.ID_ORDEN_SERVICIO = " );
		query.append( idRegistro );
		query.append( "\r\n" );
		query.append( "AND CP.IND_ACTIVO = 1\r\n"
				+ "AND DP.IND_ACTIVO = 1\r\n"
				+ "AND DP.DES_PROVIENE = 'presupuesto'\r\n"
				+ "UNION ALL\r\n"
				+ "SELECT\r\n"
				+ "'Servicio' AS grupo,\r\n"
				+ "SER.REF_SERVICIO AS concepto,\r\n"
				+ "DP.CAN_DET_PRESUP AS cantidad,\r\n"
				+ "CONCAT(CS.CVE_PRODUCTOS_SERVICIOS, ' ', CS.REF_UNIDAD_SAT) AS claveSAT,\r\n"
				+ "CS.CVE_PRODUCTOS_SERVICIOS AS claveProd,\r\n"
				+ "DP.IMP_CARAC_PRESUP AS importe,\r\n"
				+ "DP.IMP_CARAC_PRESUP AS total\r\n"
				+ "FROM\r\n"
				+ "SVC_CARAC_PRESUPUESTO CP\r\n"
				+ "INNER JOIN SVC_DETALLE_CARAC_PRESUP DP ON DP.ID_CARAC_PRESUPUESTO = CP.ID_CARAC_PRESUPUESTO\r\n"
				+ "INNER JOIN SVT_SERVICIO SER ON SER.ID_SERVICIO = DP.ID_SERVICIO\r\n"
				+ "INNER JOIN SVC_CLAVES_PRODUCTOS_SERVICIOS CS ON CS.ID_PRODUCTOS_SERVICIOS = SER.ID_PRODUCTOS_SERVICIOS\r\n"
				+ "WHERE\r\n"
				+ "CP.ID_ORDEN_SERVICIO = " );
		query.append( idRegistro );
		query.append( "\r\n" );
		query.append( "AND CP.IND_ACTIVO = 1\r\n"
				+ "AND DP.IND_ACTIVO = 1\r\n"
				+ "AND DP.DES_PROVIENE = 'presupuesto'" );
		
		return query.toString();
	}
	
	public String buscarPDF(String idFactura) {
		StringBuilder query = new StringBuilder("");
		
		query.append( "SELECT\r\n"
				+ "REF_CADENA_PDF AS pdf\r\n"
				+ "FROM SVC_FACTURA\r\n"
				+ "WHERE\r\n"
				+ "ID_FACTURA = "
				+ idFactura
				+ "\r\n"
				+ "LIMIT 1" );
		
		return query.toString();
	}
	
	public String buscarArchivos(String idFactura) {
		StringBuilder query = new StringBuilder("");
		
		query.append( "SELECT\r\n"
				+ "REF_CADENA_PDF AS arcPdf,\r\n"
				+ "REF_CADENA_XML AS arcXml,\r\n"
				+ "CVE_FOLIO_FISCAL AS folioFiscal,\r\n"
				+ "REF_RAZON_SOCIAL AS razonSocial,\r\n"
				+ "REF_CORREOE AS correo\r\n"
				+ "FROM SVC_FACTURA\r\n"
				+ "WHERE\r\n"
				+ "ID_FACTURA = "
				+ idFactura
				+ "\r\n"
				+ "LIMIT 1" );
		
		return query.toString();
	}
	
	public String cancelar(CancelarFacRequest cancelarFacRequest, Integer idUsuario) {
		
		QueryHelper q = new QueryHelper("UPDATE SVC_FACTURA");
		q.agregarParametroValues("ID_MOTIVO_CANCELACION", "'" + 
		cancelarFacRequest.getMotivoCancelacion().getIdMotivoCancelacion() + "'");
		
		if( cancelarFacRequest.getFolioRelacionado() != null ) {
			q.agregarParametroValues("CVE_FOLIO_RELACIONADO", "'" +
		cancelarFacRequest.getFolioRelacionado() + "'");
		}
		q.agregarParametroValues("ID_ESTATUS_FACTURA", "2");
		q.agregarParametroValues("FEC_CANCELACION", "NOW()");
		q.agregarParametroValues("FEC_ACTUALIZACION", "NOW()");
		q.agregarParametroValues("ID_USUARIO_MODIFICA", idUsuario.toString());
		
		q.addWhere("ID_FACTURA = " + cancelarFacRequest.getFolioFactura());
		return q.obtenerQueryActualizar();
	}
	
	public Map<String, Object> reporteCU079(FiltroRequest filtros, String nombrePdfReportes) {
		Map<String, Object> envioDatos = new HashMap<>();
		StringBuilder condicion = new StringBuilder(" ");
		
		if( filtros.getIdVelatorio() != null ) {
			condicion.append( " AND FAC.ID_VELATORIO = '" + filtros.getIdVelatorio() + "' " );
		}
		
		if( filtros.getIdDelegacion() != null ) {
			condicion.append( " AND VEL.ID_DELEGACION = '" + filtros.getIdDelegacion() + "' " );
		}
		
		
		if( filtros.getFechaInicio() == null) {
			envioDatos.put("periodo", " " );
		}else {
			envioDatos.put("periodo", "Periodo: del " + filtros.getFechaInicio() + " al " + filtros.getFechaFin());
			condicion.append( " AND DATE_FORMAT(FAC.FEC_FACTURACION,'%Y-%m-%d') BETWEEN DATE_FORMAT('" + filtros.getFechaInicio() + "','%Y-%m-%d') AND DATE_FORMAT('" + filtros.getFechaFin() + "','%Y-%m-%d') " );
		}
		
		envioDatos.put("filtros", condicion);
		envioDatos.put("tipoReporte", filtros.getTipoReporte());
		envioDatos.put("rutaNombreReporte", nombrePdfReportes);
		return envioDatos;
	}
	
	public String totalFactura(FiltroRequest filtros) {
		
		StringBuilder query = new StringBuilder("SELECT \r\n"
				+ "IFNULL(SUM( IMP_TOTAL_SERV ), 0) AS total\r\n"
				+ "FROM\r\n"
				+ "SVC_FACTURA FAC\r\n"
				+ "WHERE IND_ACTIVO = '1' ");
		
		if( validarWhere(filtros) ) {
			query.append( construyeFiltros(filtros) );
		}
		
		return query.toString();
	}
	
	public String nomVelatorio(String idVelatorio) {
		
		StringBuilder query = new StringBuilder("SELECT\r\n"
				+ "CONCAT('\"', DES_VELATORIO,'\"') AS nombre\r\n"
				+ "FROM \r\n"
				+ "SVC_VELATORIO\r\n"
				+ "WHERE\r\n"
				+ "ID_VELATORIO = ");
		
		query.append( idVelatorio );
		
		return query.toString();
	}
	
	public String canFact(Integer idFactura) {
		
		QueryHelper q = new QueryHelper("UPDATE SVC_FACTURA");
		q.agregarParametroValues("IND_ACTIVO", "b'0'");
		q.addWhere("ID_FACTURA = " + idFactura);
		
		return q.obtenerQueryActualizar();
	}
	
	public String datosVelatorio(String idVelatorio) {
		
		StringBuilder query = new StringBuilder("SELECT\r\n"
				+ "CONCAT('\"', VEL.DES_VELATORIO,'\"')  AS nomVelatorio,\r\n"
				+ "CONCAT('\"', DOM.REF_CP, '\"') AS cp,\r\n"
				+ "CONCAT('\"', DOM.REF_CALLE, '\"') AS calle,\r\n"
				+ "CONCAT('\"', DOM.NUM_EXTERIOR, '\"') AS numExterior,\r\n"
				+ "CONCAT('\"', DOM.NUM_INTERIOR, '\"') AS numInterior,\r\n"
				+ "CONCAT('\"', DOM.REF_COLONIA, '\"') AS colonia,\r\n"
				+ "CONCAT('\"', DOM.REF_MUNICIPIO, '\"') AS municipio,\r\n"
				+ "CONCAT('\"', DOM.REF_ESTADO, '\"') AS estado\r\n"
				+ "FROM\r\n"
				+ "SVC_VELATORIO VEL\r\n"
				+ "LEFT JOIN SVT_DOMICILIO DOM ON DOM.ID_DOMICILIO = VEL.ID_DOMICILIO\r\n"
				+ "WHERE\r\n"
				+ "ID_VELATORIO = ");
		
		query.append( idVelatorio );
		
		return query.toString();
	}
	
	public String finadoOds(String idOds, String formatoFecha) {
		
		StringBuilder query = new StringBuilder("SELECT\r\n"
				+ "FIN.ID_FINADO AS idFinado,\r\n"
				+ "CONCAT('\"',DATE_FORMAT(FIN.FEC_DECESO, '");
		query.append(formatoFecha);
		query.append("'), '\"') AS fecFinado,\r\n"
				+ "CONCAT('\"', PER.NOM_PERSONA, ' ', PER.NOM_PRIMER_APELLIDO, ' ', "
				+ "PER.NOM_SEGUNDO_APELLIDO, '\"') AS nomFinado\r\n"
				+ "FROM SVC_FINADO FIN\r\n"
				+ "INNER JOIN SVC_PERSONA PER ON PER.ID_PERSONA = FIN.ID_PERSONA\r\n"
				+ "INNER JOIN SVC_ORDEN_SERVICIO ODS ON ODS.ID_ORDEN_SERVICIO = FIN.ID_ORDEN_SERVICIO\r\n"
				+ "WHERE\r\n"
				+ "ODS.ID_ORDEN_SERVICIO = ");
		query.append( idOds );
		query.append(" LIMIT 1");
		
		return query.toString();
		
	}
	
	public String finadoCon(String idCon, String formatoFecha) {
		
		StringBuilder query = new StringBuilder("SELECT\r\n"
				+ "FIN.ID_FINADO AS idFinado,\r\n"
				+ "CONCAT('\"',DATE_FORMAT(FIN.FEC_DECESO, '");
		query.append(formatoFecha);
		query.append("'), '\"') AS fecFinado,\r\n"
				+ "CONCAT('\"', PER.NOM_PERSONA, ' ', PER.NOM_PRIMER_APELLIDO, ' ', PER.NOM_SEGUNDO_APELLIDO, '\"') AS nomFinado\r\n"
				+ "FROM SVC_FINADO FIN\r\n"
				+ "INNER JOIN SVC_PERSONA PER ON PER.ID_PERSONA = FIN.ID_PERSONA\r\n"
				+ "INNER JOIN SVT_CONVENIO_PF CON ON CON.ID_CONVENIO_PF = FIN.ID_CONTRATO_PREVISION\r\n"
				+ "WHERE\r\n"
				+ "CON.ID_CONVENIO_PF = ");
		query.append( idCon );
		query.append(" LIMIT 1");
		
		return query.toString();
		
	}
	
	public String finadoRenCon(String idRenCon, String formatoFecha) {
		
		StringBuilder query = new StringBuilder("SELECT\r\n"
				+ "FIN.ID_FINADO AS idFinado,\r\n"
				+ "CONCAT('\"',DATE_FORMAT(FIN.FEC_DECESO, '");
		query.append(formatoFecha);
		query.append("'), '\"') AS fecFinado,\r\n"
				+ "CONCAT('\"', PER.NOM_PERSONA, ' ', PER.NOM_PRIMER_APELLIDO, ' ', PER.NOM_SEGUNDO_APELLIDO, '\"') AS nomFinado\r\n"
				+ "FROM SVC_FINADO FIN\r\n"
				+ "INNER JOIN SVC_PERSONA PER ON PER.ID_PERSONA = FIN.ID_PERSONA\r\n"
				+ "INNER JOIN SVT_CONVENIO_PF CON ON CON.ID_CONVENIO_PF = FIN.ID_CONTRATO_PREVISION\r\n"
				+ "INNER JOIN SVT_RENOVACION_CONVENIO_PF REN ON REN.ID_CONVENIO_PF = CON.ID_CONVENIO_PF\r\n"
				+ "WHERE\r\n"
				+ "REN.ID_RENOVACION_CONVENIO_PF = ");
		query.append( idRenCon );
		query.append(" LIMIT 1");
		
		return query.toString();
		
	}
	
	public String finadoPA(String idPA, String formatoFecha) {
		
		StringBuilder query = new StringBuilder("SELECT\r\n"
				+ "FIN.ID_FINADO AS idFinado,\r\n"
				+ "CONCAT('\"',DATE_FORMAT(FIN.FEC_DECESO, '");
		query.append(formatoFecha);
		query.append("'), '\"') AS fecFinado,\r\n"
				+ "CONCAT('\"', PER.NOM_PERSONA, ' ', PER.NOM_PRIMER_APELLIDO, ' ', PER.NOM_SEGUNDO_APELLIDO, '\"') AS nomFinado\r\n"
				+ "FROM SVC_FINADO FIN\r\n"
				+ "INNER JOIN SVC_PERSONA PER ON PER.ID_PERSONA = FIN.ID_PERSONA\r\n"
				+ "INNER JOIN SVT_PLAN_SFPA PA ON PA.ID_PLAN_SFPA = FIN.ID_CONTRATO_PREVISION_PA\r\n"
				+ "WHERE\r\n"
				+ "PA.ID_PLAN_SFPA = ");
		query.append( idPA );
		query.append(" LIMIT 1");
		
		return query.toString();
		
	}
	
	public String datosFactura(String idFactura, String formatoFecha) {
		StringBuilder query = new StringBuilder("");
		
		query.append( "SELECT\r\n"
				+ "IMP_TOTAL_SERV AS total,\r\n"
				+ "CONCAT('\"', REF_RAZON_SOCIAL, '\"') AS razonSocial,\r\n"
				+ "CVE_RFC_CONTRATANTE AS rfc,\r\n"
				+ "CONCAT('\"',DATE_FORMAT(NOW(), '");
		query.append(formatoFecha);
		query.append("'), '\"') AS fecCan\r\n"
				+ "FROM SVC_FACTURA\r\n"
				+ "WHERE\r\n"
				+ "ID_FACTURA = "
				+ idFactura
				+ "\r\n"
				+ "LIMIT 1" );
		
		return query.toString();
	}
}
