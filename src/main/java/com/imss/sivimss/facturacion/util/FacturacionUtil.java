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
			
			query.append( "PB.ID_FLUJO_PAGOS = '" + filtros.getIdFlujoPagos() +"' " );
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

}
