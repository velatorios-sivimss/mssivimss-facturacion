package com.imss.sivimss.facturacion.service;

import org.springframework.security.core.Authentication;

import com.imss.sivimss.facturacion.util.DatosRequest;
import com.imss.sivimss.facturacion.util.Response;

public interface CatalogosService {

	Response<Object> buscarCfdi(DatosRequest request, Authentication authentication) throws Exception;
	Response<Object> metPago(DatosRequest request, Authentication authentication) throws Exception;
	Response<Object> formaPago(DatosRequest request, Authentication authentication) throws Exception;
}
