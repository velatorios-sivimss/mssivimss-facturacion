package com.imss.sivimss.facturacion.service.impl;

import java.util.logging.Level;

import javax.xml.bind.DatatypeConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.imss.sivimss.facturacion.model.request.CfdiRequest;
import com.imss.sivimss.facturacion.service.CatalogosService;
import com.imss.sivimss.facturacion.util.AppConstantes;
import com.imss.sivimss.facturacion.util.CatalogosUtil;
import com.imss.sivimss.facturacion.util.DatosRequest;
import com.imss.sivimss.facturacion.util.LogUtil;
import com.imss.sivimss.facturacion.util.MensajeResponseUtil;
import com.imss.sivimss.facturacion.util.ProviderServiceRestTemplate;
import com.imss.sivimss.facturacion.util.Response;

@Service
public class CatalogosServiceImpl implements CatalogosService {

	@Value("${endpoints.mod-catalogos}")
	private String urlDomino;
	
	@Autowired
	private LogUtil logUtil;
	
	@Autowired
	private ProviderServiceRestTemplate providerRestTemplate;
	
	private static final String CONSULTA = "consulta";
	private static final String CONSULTA_GENERICA = "/consulta";
	private static final String SIN_INFORMACION = "45";
	
	@Override
	public Response<Object> buscarCfdi(DatosRequest request, Authentication authentication) throws Exception {
		Gson gson = new Gson();
		CfdiRequest cfdiRequest = gson.fromJson(String.valueOf(request.getDatos().get(AppConstantes.DATOS)), CfdiRequest.class);
		Response<Object> response = new Response<>();
		String filtro;
		CatalogosUtil catalogosUtil = new CatalogosUtil();
		String query;
		
		if( cfdiRequest.getTipoPersona().equals( AppConstantes.PERSONA_MORAL ) ) {
			filtro = "IND_APL_MORAL = '1'";
		}else {
			filtro = "IND_APL_FISICA = '1'";
		}
		
		query = catalogosUtil.cfdi(filtro);
		
		logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(), 
				this.getClass().getPackage().toString(), "",CONSULTA +" " + query, authentication);
		
		request.getDatos().put(AppConstantes.QUERY, DatatypeConverter.printBase64Binary(query.getBytes("UTF-8")));
		
		response = providerRestTemplate.consumirServicio(request.getDatos(), urlDomino + CONSULTA_GENERICA, 
				authentication);
		
		return MensajeResponseUtil.mensajeConsultaResponse( response, SIN_INFORMACION );
	}

	@Override
	public Response<Object> metPago(DatosRequest request, Authentication authentication) throws Exception {
		Gson gson = new Gson();
		CfdiRequest cfdiRequest = gson.fromJson(String.valueOf(request.getDatos().get(AppConstantes.DATOS)), CfdiRequest.class);
		Response<Object> response = new Response<>();
		String filtro;
		CatalogosUtil catalogosUtil = new CatalogosUtil();
		String query;
		
		if( cfdiRequest.getTipoPersona().equals( AppConstantes.PERSONA_MORAL ) ) {
			filtro = "IND_APL_MORAL = '1'";
		}else {
			filtro = "IND_APL_FISICA = '1'";
		}
		
		query = catalogosUtil.metPago(filtro);
		
		logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(), 
				this.getClass().getPackage().toString(), "",CONSULTA +" " + query, authentication);
		
		request.getDatos().put(AppConstantes.QUERY, DatatypeConverter.printBase64Binary(query.getBytes("UTF-8")));
		
		response = providerRestTemplate.consumirServicio(request.getDatos(), urlDomino + CONSULTA_GENERICA, 
				authentication);
		
		return MensajeResponseUtil.mensajeConsultaResponse( response, SIN_INFORMACION );
	}

	@Override
	public Response<Object> formaPago(DatosRequest request, Authentication authentication) throws Exception {
		Gson gson = new Gson();
		CfdiRequest cfdiRequest = gson.fromJson(String.valueOf(request.getDatos().get(AppConstantes.DATOS)), CfdiRequest.class);
		Response<Object> response = new Response<>();
		String filtro;
		CatalogosUtil catalogosUtil = new CatalogosUtil();
		String query;
		
		if( cfdiRequest.getTipoPersona().equals( AppConstantes.PERSONA_MORAL ) ) {
			filtro = "IND_APL_MORAL = '1'";
		}else {
			filtro = "IND_APL_FISICA = '1'";
		}
		
		query = catalogosUtil.formaPago(filtro);
		
		logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(), 
				this.getClass().getPackage().toString(), "",CONSULTA +" " + query, authentication);
		
		request.getDatos().put(AppConstantes.QUERY, DatatypeConverter.printBase64Binary(query.getBytes("UTF-8")));
		
		response = providerRestTemplate.consumirServicio(request.getDatos(), urlDomino + CONSULTA_GENERICA, 
				authentication);
		
		return MensajeResponseUtil.mensajeConsultaResponse( response, SIN_INFORMACION );
	}

}
