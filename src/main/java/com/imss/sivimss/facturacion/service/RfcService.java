package com.imss.sivimss.facturacion.service;

import org.springframework.security.core.Authentication;

import com.imss.sivimss.facturacion.util.DatosRequest;
import com.imss.sivimss.facturacion.util.Response;

public interface RfcService {

	Response<Object> buscar(DatosRequest request, Authentication authentication) throws Exception;
	
}
