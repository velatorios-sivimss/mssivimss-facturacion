package com.imss.sivimss.facturacion.service.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import javax.xml.bind.DatatypeConverter;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.imss.sivimss.facturacion.service.FacturacionService;
import com.imss.sivimss.facturacion.util.DatosRequest;
import com.imss.sivimss.facturacion.util.Response;
import com.imss.sivimss.facturacion.model.request.FiltroRequest;
import com.imss.sivimss.facturacion.model.request.FoliosRequest;
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
	
	@Autowired
	private ModelMapper modelMapper;
	
	private static final String CONSULTA = "consulta";
	private static final String CONSULTA_PAGINADA = "/paginado";
	private static final String CONSULTA_GENERICA = "/consulta";
	private static final String ACTUALIZAR_MULTIPLES = "/actualizar/multiples";
	private static final String SIN_INFORMACION = "45";
	private static final String NOM_REPORTE = "reportes/generales/ReporteTablaPagos.jrxml";
	private static final String ERROR_AL_DESCARGAR_DOCUMENTO= "64"; // Error en la descarga del documento.Intenta nuevamente.
	
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
	public Response<Object> buscarFolios(DatosRequest request, Authentication authentication) throws IOException {
		Gson gson = new Gson();
		FoliosRequest foliosRequest = gson.fromJson(String.valueOf(request.getDatos().get(AppConstantes.DATOS)), FoliosRequest.class);
		FacturacionUtil facturacionUtil = new FacturacionUtil();
		String query = facturacionUtil.consultaFolios(foliosRequest.getTipoFactura());
		
		logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(), 
				this.getClass().getPackage().toString(), "",CONSULTA +" " + query, authentication);
		
		Map<String, Object> datos = new HashMap<>();
		datos.put(AppConstantes.QUERY, DatatypeConverter.printBase64Binary(query.getBytes("UTF-8")));
		request.setDatos(datos);
		
		Response<Object> response = providerRestTemplate.consumirServicio(request.getDatos(), urlDomino + CONSULTA_GENERICA, 
				authentication);
		
		return MensajeResponseUtil.mensajeConsultaResponse( response, SIN_INFORMACION );
	}

}
