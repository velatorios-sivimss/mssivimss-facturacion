package com.imss.sivimss.facturacion.util;

public class CatalogosUtil {
	
	public String cfdi(String filtro) {
		
		StringBuilder query = new StringBuilder("");
		query.append( "SELECT\r\n"
				+ "ID_CFDI AS idCfdi,\r\n"
				+ "CONCAT(CVE_USO_CFDI, ' ', DES_CFDI) AS desCfdi\r\n"
				+ "FROM\r\n"
				+ "SVC_CFDI\r\n"
				+ "WHERE\r\n");
		query.append( filtro );
		query.append( "\r\n ORDER BY ID_CFDI ASC" );
		
		return query.toString();
		
	}
	
	public String metPago(String filtro) {
		
		StringBuilder query = new StringBuilder("");
		query.append( "SELECT\r\n"
				+ "ID_MET_PAGO_FAC AS idMetPagoFac,\r\n"
				+ "CONCAT(CVE_MET_PAGO_FAC, ' ', DESC_MET_PAGO_FAC) AS desMetPagoFac\r\n"
				+ "FROM\r\n"
				+ "SVC_MET_PAGO_FAC\r\n"
				+ "WHERE\r\n");
		query.append( filtro );
		query.append( "\r\n ORDER BY ID_MET_PAGO_FAC ASC" );
		
		return query.toString();
		
	}
	
	public String formaPago(String filtro) {
		
		StringBuilder query = new StringBuilder("");
		query.append( "SELECT\r\n"
				+ "ID_FOR_PAGO_FAC AS idForPago,\r\n"
				+ "CONCAT(CVE_FOR_PAGO_FAC, ' ', DESC_FOR_PAGO_FAC) AS desForPago\r\n"
				+ "FROM\r\n"
				+ "SVC_FOR_PAGO_FAC\r\n"
				+ "WHERE\r\n");
		query.append( filtro );
		query.append( "\r\n ORDER BY ID_FOR_PAGO_FAC ASC" );
		
		return query.toString();
		
	}
	
	public String motCancelacion() {
		
		StringBuilder query = new StringBuilder("SELECT\r\n"
				+ "ID_MOTIVO_CANCELACION AS idMotivoCancelacion,\r\n"
				+ "CVE_MOTIVO AS clave,\r\n"
				+ "DES_MOTIVO AS descripcion,\r\n"
				+ "FOLIO_RELACIONADO AS aplicaFolio\r\n"
				+ "FROM\r\n"
				+ "SVC_MOTIVO_CANCELACION\r\n"
				+ "WHERE\r\n"
				+ "IND_ACTIVO = '1'\r\n"
				+ "ORDER BY ID_MOTIVO_CANCELACION ASC");
		
		return query.toString();
		
	}
}
