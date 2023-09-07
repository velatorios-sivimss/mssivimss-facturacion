package com.imss.sivimss.facturacion.service.impl;

import java.io.IOException;
import java.util.logging.Level;

import javax.xml.bind.DatatypeConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.imss.sivimss.facturacion.service.FacturacionService;
import com.imss.sivimss.facturacion.util.DatosRequest;
import com.imss.sivimss.facturacion.util.Response;
import com.imss.sivimss.facturacion.model.request.UsuarioDto;
import com.imss.sivimss.facturacion.model.request.CrearFacRequest;
import com.imss.sivimss.facturacion.model.request.FiltroRequest;
import com.imss.sivimss.facturacion.util.AppConstantes;
import com.imss.sivimss.facturacion.util.MensajeResponseUtil;
import com.imss.sivimss.facturacion.util.FacturacionUtil;
import com.imss.sivimss.facturacion.util.LogUtil;
import com.imss.sivimss.facturacion.util.ProviderServiceRestTemplate;

@Service
public class FacturacionServiceImpl implements FacturacionService {
	
	@Value("${endpoints.mod-catalogos}")
	private String urlDomino;
	
	@Autowired
	private LogUtil logUtil;
	
	@Autowired
	private ProviderServiceRestTemplate providerRestTemplate;
	
	private static final String CONSULTA = "consulta";
	private static final String CONSULTA_PAGINADA = "/paginado";
	private static final String CONSULTA_GENERICA = "/consulta";
	private static final String SIN_INFORMACION = "45";
	
	@Override
	public Response<Object> buscar(DatosRequest request, Authentication authentication) throws IOException {
		
		Gson gson = new Gson();
		FiltroRequest filtrosRequest = gson.fromJson(String.valueOf(request.getDatos().get(AppConstantes.DATOS)), FiltroRequest.class);
		FacturacionUtil facturacionUtil = new FacturacionUtil();
		String query = facturacionUtil.consultaTabla(filtrosRequest);
		
		logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(), 
				this.getClass().getPackage().toString(), "",CONSULTA +" " + query, authentication);
		
		request.getDatos().put(AppConstantes.QUERY, DatatypeConverter.printBase64Binary(query.getBytes("UTF-8")));
		
		Response<Object> response = providerRestTemplate.consumirServicio(request.getDatos(), urlDomino + CONSULTA_PAGINADA, 
				authentication);
		
		return MensajeResponseUtil.mensajeConsultaResponse( response, SIN_INFORMACION );
	
	}

	@Override
	public Response<Object> crear(DatosRequest request, Authentication authentication) throws IOException {
		
		Gson gson = new Gson();
		CrearFacRequest crearFacRequest = gson.fromJson(String.valueOf(request.getDatos().get(AppConstantes.DATOS)), CrearFacRequest.class);
		UsuarioDto usuarioDto = gson.fromJson((String) authentication.getPrincipal(), UsuarioDto.class);
		Response<Object> response;
		FacturacionUtil facturacionUtil = new FacturacionUtil();
		String query = facturacionUtil.crear(crearFacRequest, usuarioDto.getIdUsuario());
		
		logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(), 
				this.getClass().getPackage().toString(), "",CONSULTA +" " + query, authentication);
		
		request.getDatos().put(AppConstantes.QUERY, DatatypeConverter.printBase64Binary(query.getBytes("UTF-8")));
		
		response = providerRestTemplate.consumirServicio(request.getDatos(), urlDomino + CONSULTA_GENERICA, 
				authentication);
		
		// TODO Auto-generated method stub
		//Falta el consumo de la Factura
		
		
		return response;
	}

}
