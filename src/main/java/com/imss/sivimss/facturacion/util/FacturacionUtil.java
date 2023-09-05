package com.imss.sivimss.facturacion.util;

import com.imss.sivimss.facturacion.model.request.CrearFacRequest;
import com.imss.sivimss.facturacion.model.request.FiltroRequest;

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
				query = foliosODS();
			break;
		case "2":
			break;
		case "3":
			break;
		case "4":
			break;
		case "5":
			break;
		
		}
		
		return query;
	}

	
	private String foliosODS() {
		StringBuilder query = new StringBuilder("");
		
		query.append( "SELECT\r\n"
				+ "PB.ID_PAGO_BITACORA AS idPagoBitacora,\r\n"
				+ "PB.ID_REGISTRO AS idRegistro,\r\n"
				+ "PB.CVE_FOLIO AS folio\r\n"
				+ "FROM SVT_PAGO_BITACORA PB\r\n"
				+ "WHERE\r\n"
				+ "PB.ID_FLUJO_PAGOS = 1\r\n"
				+ "AND PB.CVE_ESTATUS_PAGO =4" );
		
		return query.toString();
	}
	
	public String infoPagos(String idPagoBitacora) {
		
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
	
	public String obtServicios(String idRegistro) {
		
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
	
	public String crear(CrearFacRequest datos, Integer idUsuario) {
		
		QueryHelper q = new QueryHelper("INSERT INTO SVC_FACTURA");
		q.agregarParametroValues("ID_PAGO", "'" + datos.getIdPagoBitacora() + "'");
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
	
}
