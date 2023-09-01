package com.imss.sivimss.facturacion.util;

public class CatalogosUtil {
	
	public String cfdi(String filtro) {
		
		StringBuilder query = new StringBuilder("");
		query.append( "SELECT\r\n"
				+ "ID_CFDI AS idCfdi,\r\n"
				+ "CONCAT(CVE_USO_CFDI, ' ', DESC_CFDI) AS desCfdi\r\n"
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
}
