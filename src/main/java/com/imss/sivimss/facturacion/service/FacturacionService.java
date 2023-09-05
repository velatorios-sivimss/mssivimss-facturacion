package com.imss.sivimss.facturacion.service;

import java.io.IOException;

import org.springframework.security.core.Authentication;

import com.imss.sivimss.facturacion.util.DatosRequest;
import com.imss.sivimss.facturacion.util.Response;

public interface FacturacionService {

	Response<Object> buscar(DatosRequest request, Authentication authentication) throws IOException;
	Response<Object> buscarFolios(DatosRequest request, Authentication authentication) throws IOException;
	Response<Object> infoFolio(DatosRequest request, Authentication authentication) throws IOException;
	Response<Object> crear(DatosRequest request, Authentication authentication) throws IOException;
}
