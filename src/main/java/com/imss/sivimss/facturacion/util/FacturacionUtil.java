package com.imss.sivimss.facturacion.util;

import com.imss.sivimss.facturacion.model.request.FiltroRequest;

public class FacturacionUtil {
	
	private static String CONSULTA_TABLA = "SELECT \r\n"
			+ "VEL.DES_VELATORIO AS velatorio,\r\n"
			+ "PB.ID_VELATORIO AS idVelatorio,\r\n"
			+ "PB.CVE_FOLIO AS folio,\r\n"
			+ "FAC.ID_FACTURA AS folioFactura,\r\n"
			+ "FAC.FEC_FACTURACION AS fechaFactura,\r\n"
			+ "FAC.CVE_FOLIO_FISCAL AS folioFiscal,\r\n"
			+ "FAC.CVE_RFC_CONTRATANTE AS rfc,\r\n"
			+ "FAC.DES_RAZON_SOCIAL AS razonSocial,\r\n"
			+ "ESFAC.DES_ESTATUS AS estatusFactura,\r\n"
			+ "PB.ID_FLUJO_PAGOS AS idFlujoPagos\r\n"
			+ "FROM SVC_FACTURA FAC\r\n"
			+ "INNER JOIN SVT_PAGO_BITACORA PB ON PB.ID_PAGO_BITACORA = FAC.ID_PAGO\r\n"
			+ "INNER JOIN SVC_VELATORIO VEL ON VEL.ID_VELATORIO = PB.ID_VELATORIO\r\n"
			+ "INNER JOIN SVC_ESTATUS_FACTURA ESFAC ON ESFAC.ID_ESTATUS_FACTURA = FAC.ID_ESTATUS_FACTURA";
	
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
		
		query.append( " WHERE " );
		
		int i=0;
		
		
		if( (filtros.getIdVelatorio()!=null && !filtros.getIdVelatorio().isEmpty() )) {
			query.append( "PB.ID_VELATORIO = '" + filtros.getIdVelatorio() + "' " );
			i++;
		}
		
		if( (filtros.getIdFlujoPagos()!=null && !filtros.getIdFlujoPagos().isEmpty() )) {
			
			if(i>=1) {
				query.append( "AND ");
			}
			
			if( filtros.getIdFlujoPagos().equals("2") ) {
				query.append( "PB.ID_FLUJO_PAGOS IN (2,3)" );
			}else {
				query.append( "PB.ID_FLUJO_PAGOS = '" + filtros.getIdFlujoPagos() +"' " );
			}
			i++;
		}
		
		if( (filtros.getFolio()!=null && !filtros.getFolio().isEmpty() )) {
			
			if(i>=1) {
				query.append( "AND ");
			}
			
			query.append( "PB.CVE_FOLIO LIKE CONCAT('" + filtros.getFolio() + "', '%') " );
			i++;
		}
		
		if( (filtros.getFolioFactura()!=null && !filtros.getFolioFactura().isEmpty() )) {
			
			if(i>=1) {
				query.append( "AND ");
			}
			
			query.append( "FAC.ID_FACTURA LIKE CONCAT('" + filtros.getFolioFactura() + "', '%') " );
			i++;
		}
		
		if( (filtros.getFolioFiscal()!=null && !filtros.getFolioFiscal().isEmpty() )) {
			
			if(i>=1) {
				query.append( "AND ");
			}
			
			query.append( "FAC.CVE_FOLIO_FISCAL LIKE CONCAT('" + filtros.getFolioFiscal() + "', '%') " );
			i++;
		}
		
		if( (filtros.getRfc()!=null && !filtros.getRfc().isEmpty() ) ) {
			
			if(i>=1) {
				query.append( "AND ");
			}
			
			query.append( "FAC.CVE_RFC_CONTRATANTE LIKE CONCAT('"+ filtros.getRfc() + "', '%') " );
			i++;
		}
		
		if( (filtros.getFechaInicio()!=null && !filtros.getFechaInicio().isEmpty() ) ) {
			
			if(i>=1) {
				query.append( "AND ");
			}
			
			query.append( "T.fecha BETWEEN '" + filtros.getFechaInicio() + "' AND '" + filtros.getFechaFin() + "' " );
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
				+ "PER.DES_CORREO AS correo\r\n"
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
	
}
