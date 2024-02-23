package com.imss.sivimss.facturacion.service.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.xml.bind.DatatypeConverter;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.imss.sivimss.facturacion.model.request.FoliosRequest;
import com.imss.sivimss.facturacion.model.response.FinadoResponse;
import com.imss.sivimss.facturacion.model.response.InfoFolioResponse;
import com.imss.sivimss.facturacion.service.FoliosService;
import com.imss.sivimss.facturacion.util.AppConstantes;
import com.imss.sivimss.facturacion.util.DatosRequest;
import com.imss.sivimss.facturacion.util.FacturacionUtil;
import com.imss.sivimss.facturacion.util.LogUtil;
import com.imss.sivimss.facturacion.util.MensajeResponseUtil;
import com.imss.sivimss.facturacion.util.ProviderServiceRestTemplate;
import com.imss.sivimss.facturacion.util.Response;

@Service
public class FoliosServiceImpl implements FoliosService {

	@Value("${endpoints.mod-catalogos}")
	private String urlDomino;
	
	@Value("${formato_fecha}")
	private String formatoFecha;
	
	@Autowired
	private LogUtil logUtil;
	
	@Autowired
	private ProviderServiceRestTemplate providerRestTemplate;
	
	@Autowired
	private ModelMapper modelMapper;
	
	private static final String CONSULTA = "consulta";
	private static final String SIN_INFORMACION = "45";
	private static final String CONSULTA_GENERICA = "/consulta";
	
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

	@Override
	public Response<Object> infoFolio(DatosRequest request, Authentication authentication) throws IOException {
		
		Gson gson = new Gson();
		FoliosRequest foliosRequest = gson.fromJson(String.valueOf(request.getDatos().get(AppConstantes.DATOS)), FoliosRequest.class);
		Response<Object> response = null;
		
		switch(foliosRequest.getTipoFactura()) {
		
		case "1":
				response = infoFolioODS(foliosRequest, authentication);	
			break;
		case "2":
			response = infoFolioCon(foliosRequest, authentication);	
			break;
		case "3":
			response = infoFolioRenCon(foliosRequest, authentication);	
			break;
		case "4":
			response = infoFolioPA(foliosRequest, authentication);
			break;
		case "5":
			break;
		
		}
		
		return MensajeResponseUtil.mensajeConsultaResponse( response, SIN_INFORMACION );
		
	}

	@SuppressWarnings("unchecked")
	private Response<Object> infoFolioODS ( FoliosRequest foliosRequest, Authentication authentication )
			throws IOException {
		
		Response<Object> response = null;
		String query = "";
		FacturacionUtil facturacionUtil = new FacturacionUtil();
		List<Map<String, Object>> listadatos;
		InfoFolioResponse detalle;
		DatosRequest request = new DatosRequest();
		Gson gson = new Gson();
		
		/**
		 * Primero obtenemos la informacion basica del Pago en SVT_PAGO_BITACORA
		 */
		query = facturacionUtil.infoPagosODS(foliosRequest.getIdPagoBitacora());
		
		logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(), 
				this.getClass().getPackage().toString(), "",CONSULTA +" " + query, authentication);
		
		Map<String, Object> datos = new HashMap<>();
		datos.put(AppConstantes.QUERY, DatatypeConverter.printBase64Binary(query.getBytes("UTF-8")));
		request.setDatos(datos);
		
		response = providerRestTemplate.consumirServicio(request.getDatos(), urlDomino + CONSULTA_GENERICA, 
				authentication);
		
		listadatos = Arrays.asList(modelMapper.map(response.getDatos(), Map[].class));
		detalle = gson.fromJson(String.valueOf(listadatos.get(0)), InfoFolioResponse.class);
		
		/**
		 * Despues obtenemos todos los metodos del Pagos en SVT_PAGO_DETALLE
		 */
		
		query = facturacionUtil.obtMetPago(foliosRequest.getIdPagoBitacora() );
		
		logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(), 
				this.getClass().getPackage().toString(), "",CONSULTA +" " + query, authentication);
		
		request.getDatos().put(AppConstantes.QUERY, DatatypeConverter.printBase64Binary(query.getBytes("UTF-8")));
		
		response = providerRestTemplate.consumirServicio(request.getDatos(), urlDomino + CONSULTA_GENERICA, 
				authentication);
		
		listadatos = Arrays.asList(modelMapper.map(response.getDatos(), Map[].class));
		
		detalle.setMetodosPago(listadatos);
		
		/**
		 * Por ultimo obtenemos la informacion de los servicios en
		 * SVC_CARACTERISTICAS_PRESUPUESTO y SVC_DETALLE_CARACTERISTICAS_PRESUPUESTO
		 */
		query = facturacionUtil.obtServiciosODS(foliosRequest.getIdRegistro());
		
		logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(), 
				this.getClass().getPackage().toString(), "",CONSULTA +" " + query, authentication);
		
		request.getDatos().put(AppConstantes.QUERY, DatatypeConverter.printBase64Binary(query.getBytes("UTF-8")));
		
		response = providerRestTemplate.consumirServicio(request.getDatos(), urlDomino + CONSULTA_GENERICA, 
				authentication);
		
		listadatos = Arrays.asList(modelMapper.map(response.getDatos(), Map[].class));
		
		detalle.setServicios(listadatos);
		
		query = facturacionUtil.finadoOds(foliosRequest.getIdRegistro(), formatoFecha);
		
		logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(), 
				this.getClass().getPackage().toString(), "",CONSULTA +" " + query, authentication);
		
		request.getDatos().put(AppConstantes.QUERY, DatatypeConverter.printBase64Binary(query.getBytes("UTF-8")));
		
		response = providerRestTemplate.consumirServicio(request.getDatos(), urlDomino + CONSULTA_GENERICA, 
				authentication);
		
		listadatos = Arrays.asList(modelMapper.map(response.getDatos(), Map[].class));
		
		if( (listadatos != null) && (listadatos.size() > 0) ) {
			
			FinadoResponse finado = gson.fromJson(String.valueOf(listadatos.get(0)), FinadoResponse.class);;
			detalle.setFinado( finado );
		}
		
		response.setDatos(detalle);
		
		return response;
		
	}
	
	@SuppressWarnings("unchecked")
	private Response<Object> infoFolioCon ( FoliosRequest foliosRequest, Authentication authentication )
			throws IOException {
		
		Response<Object> response = null;
		String query = "";
		FacturacionUtil facturacionUtil = new FacturacionUtil();
		List<Map<String, Object>> listadatos;
		InfoFolioResponse detalle;
		DatosRequest request = new DatosRequest();
		Gson gson = new Gson();
		
		/**
		 * Primero obtenemos la informacion basica del Pago en SVT_PAGO_BITACORA
		 */
		query = facturacionUtil.infoPagosCon(foliosRequest.getIdPagoBitacora());
		
		logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(), 
				this.getClass().getPackage().toString(), "",CONSULTA +" " + query, authentication);
		
		Map<String, Object> datos = new HashMap<>();
		datos.put(AppConstantes.QUERY, DatatypeConverter.printBase64Binary(query.getBytes("UTF-8")));
		request.setDatos(datos);
		
		response = providerRestTemplate.consumirServicio(request.getDatos(), urlDomino + CONSULTA_GENERICA, 
				authentication);
		
		listadatos = Arrays.asList(modelMapper.map(response.getDatos(), Map[].class));
		detalle = gson.fromJson(String.valueOf(listadatos.get(0)), InfoFolioResponse.class);
		
		/**
		 * Despues obtenemos todos los metodos del Pagos en SVT_PAGO_DETALLE
		 */
		
		query = facturacionUtil.obtMetPago(foliosRequest.getIdPagoBitacora() );
		
		logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(), 
				this.getClass().getPackage().toString(), "",CONSULTA +" " + query, authentication);
		
		request.getDatos().put(AppConstantes.QUERY, DatatypeConverter.printBase64Binary(query.getBytes("UTF-8")));
		
		response = providerRestTemplate.consumirServicio(request.getDatos(), urlDomino + CONSULTA_GENERICA, 
				authentication);
		
		listadatos = Arrays.asList(modelMapper.map(response.getDatos(), Map[].class));
		
		detalle.setMetodosPago(listadatos);
		
		/**
		 * Por ultimo obtenemos la informacion de los servicios en
		 * SVC_CARACTERISTICAS_PRESUPUESTO y SVC_DETALLE_CARACTERISTICAS_PRESUPUESTO
		 */
		query = facturacionUtil.obtServiciosConv(foliosRequest.getIdRegistro());
		
		logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(), 
				this.getClass().getPackage().toString(), "",CONSULTA +" " + query, authentication);
		
		request.getDatos().put(AppConstantes.QUERY, DatatypeConverter.printBase64Binary(query.getBytes("UTF-8")));
		
		response = providerRestTemplate.consumirServicio(request.getDatos(), urlDomino + CONSULTA_GENERICA, 
				authentication);
		
		listadatos = Arrays.asList(modelMapper.map(response.getDatos(), Map[].class));
		
		detalle.setServicios(listadatos);
		
		query = facturacionUtil.finadoCon(foliosRequest.getIdRegistro(), formatoFecha);
		
		logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(), 
				this.getClass().getPackage().toString(), "",CONSULTA +" " + query, authentication);
		
		request.getDatos().put(AppConstantes.QUERY, DatatypeConverter.printBase64Binary(query.getBytes("UTF-8")));
		
		response = providerRestTemplate.consumirServicio(request.getDatos(), urlDomino + CONSULTA_GENERICA, 
				authentication);
		
		listadatos = Arrays.asList(modelMapper.map(response.getDatos(), Map[].class));
		
		if( (listadatos != null) && (listadatos.size() > 0) ) {
			
			FinadoResponse finado = gson.fromJson(String.valueOf(listadatos.get(0)), FinadoResponse.class);;
			detalle.setFinado( finado );
		}
		
		response.setDatos(detalle);
		
		return response;
		
	}
	
	@SuppressWarnings("unchecked")
	private Response<Object> infoFolioRenCon ( FoliosRequest foliosRequest, Authentication authentication )
			throws IOException {
		
		Response<Object> response = null;
		String query = "";
		FacturacionUtil facturacionUtil = new FacturacionUtil();
		List<Map<String, Object>> listadatos;
		InfoFolioResponse detalle;
		DatosRequest request = new DatosRequest();
		Gson gson = new Gson();
		
		/**
		 * Primero obtenemos la informacion basica del Pago en SVT_PAGO_BITACORA
		 */
		query = facturacionUtil.infoPagosRenCon(foliosRequest.getIdPagoBitacora());
		
		logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(), 
				this.getClass().getPackage().toString(), "",CONSULTA +" " + query, authentication);
		
		Map<String, Object> datos = new HashMap<>();
		datos.put(AppConstantes.QUERY, DatatypeConverter.printBase64Binary(query.getBytes("UTF-8")));
		request.setDatos(datos);
		
		response = providerRestTemplate.consumirServicio(request.getDatos(), urlDomino + CONSULTA_GENERICA, 
				authentication);
		
		listadatos = Arrays.asList(modelMapper.map(response.getDatos(), Map[].class));
		detalle = gson.fromJson(String.valueOf(listadatos.get(0)), InfoFolioResponse.class);
		
		
		/**
		 * Despues obtenemos todos los metodos del Pagos en SVT_PAGO_DETALLE
		 */
		
		query = facturacionUtil.obtMetPago(foliosRequest.getIdPagoBitacora() );
		
		logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(), 
				this.getClass().getPackage().toString(), "",CONSULTA +" " + query, authentication);
		
		request.getDatos().put(AppConstantes.QUERY, DatatypeConverter.printBase64Binary(query.getBytes("UTF-8")));
		
		response = providerRestTemplate.consumirServicio(request.getDatos(), urlDomino + CONSULTA_GENERICA, 
				authentication);
		
		listadatos = Arrays.asList(modelMapper.map(response.getDatos(), Map[].class));
		
		detalle.setMetodosPago(listadatos);
		
		/**
		 * Por ultimo obtenemos la informacion de los servicios en
		 * SVC_CARACTERISTICAS_PRESUPUESTO y SVC_DETALLE_CARACTERISTICAS_PRESUPUESTO
		 */
		query = facturacionUtil.obtServiciosRenConv(foliosRequest.getIdRegistro());
		
		logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(), 
				this.getClass().getPackage().toString(), "",CONSULTA +" " + query, authentication);
		
		request.getDatos().put(AppConstantes.QUERY, DatatypeConverter.printBase64Binary(query.getBytes("UTF-8")));
		
		response = providerRestTemplate.consumirServicio(request.getDatos(), urlDomino + CONSULTA_GENERICA, 
				authentication);
		
		listadatos = Arrays.asList(modelMapper.map(response.getDatos(), Map[].class));
		
		detalle.setServicios(listadatos);
		
		query = facturacionUtil.finadoRenCon(foliosRequest.getIdRegistro(), formatoFecha);
		
		logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(), 
				this.getClass().getPackage().toString(), "",CONSULTA +" " + query, authentication);
		
		request.getDatos().put(AppConstantes.QUERY, DatatypeConverter.printBase64Binary(query.getBytes("UTF-8")));
		
		response = providerRestTemplate.consumirServicio(request.getDatos(), urlDomino + CONSULTA_GENERICA, 
				authentication);
		
		listadatos = Arrays.asList(modelMapper.map(response.getDatos(), Map[].class));
		
		if( (listadatos != null) && (listadatos.size() > 0) ) {
			
			FinadoResponse finado = gson.fromJson(String.valueOf(listadatos.get(0)), FinadoResponse.class);;
			detalle.setFinado( finado );
		}
		
		response.setDatos(detalle);
		
		return response;
		
	}
	
	@SuppressWarnings("unchecked")
	private Response<Object> infoFolioPA ( FoliosRequest foliosRequest, Authentication authentication )
			throws IOException {
		
		Response<Object> response = null;
		String query = "";
		FacturacionUtil facturacionUtil = new FacturacionUtil();
		List<Map<String, Object>> listadatos;
		InfoFolioResponse detalle;
		DatosRequest request = new DatosRequest();
		Gson gson = new Gson();
		
		/**
		 * Primero obtenemos la informacion basica del Pago en SVT_PAGO_BITACORA
		 */
		query = facturacionUtil.infoPagosPA(foliosRequest.getIdRegistro(),foliosRequest.getIdPagoBitacora());
		
		logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(), 
				this.getClass().getPackage().toString(), "",CONSULTA +" " + query, authentication);
		
		Map<String, Object> datos = new HashMap<>();
		datos.put(AppConstantes.QUERY, DatatypeConverter.printBase64Binary(query.getBytes("UTF-8")));
		request.setDatos(datos);
		
		response = providerRestTemplate.consumirServicio(request.getDatos(), urlDomino + CONSULTA_GENERICA, 
				authentication);
		
		listadatos = Arrays.asList(modelMapper.map(response.getDatos(), Map[].class));
		detalle = gson.fromJson(String.valueOf(listadatos.get(0)), InfoFolioResponse.class);
		
		
		/**
		 * Despues obtenemos todos los metodos del Pagos en SVT_PAGO_DETALLE
		 */
		
		query = facturacionUtil.obtMetPagoPA(foliosRequest.getIdPagoBitacora());
		
		logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(), 
				this.getClass().getPackage().toString(), "",CONSULTA +" " + query, authentication);
		
		request.getDatos().put(AppConstantes.QUERY, DatatypeConverter.printBase64Binary(query.getBytes("UTF-8")));
		
		response = providerRestTemplate.consumirServicio(request.getDatos(), urlDomino + CONSULTA_GENERICA, 
				authentication);
		
		listadatos = Arrays.asList(modelMapper.map(response.getDatos(), Map[].class));
		
		detalle.setMetodosPago(listadatos);
		
		/**
		 * Por ultimo obtenemos la informacion de los servicios en
		 * SVC_CARACTERISTICAS_PRESUPUESTO y SVC_DETALLE_CARACTERISTICAS_PRESUPUESTO
		 */
		query = facturacionUtil.obtServiciosPA(foliosRequest.getIdRegistro());
		
		logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(), 
				this.getClass().getPackage().toString(), "",CONSULTA +" " + query, authentication);
		
		request.getDatos().put(AppConstantes.QUERY, DatatypeConverter.printBase64Binary(query.getBytes("UTF-8")));
		
		response = providerRestTemplate.consumirServicio(request.getDatos(), urlDomino + CONSULTA_GENERICA, 
				authentication);
		
		listadatos = Arrays.asList(modelMapper.map(response.getDatos(), Map[].class));
		
		detalle.setServicios(listadatos);
		
		query = facturacionUtil.finadoPA(foliosRequest.getIdRegistro(), formatoFecha);
		
		logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(), 
				this.getClass().getPackage().toString(), "",CONSULTA +" " + query, authentication);
		
		request.getDatos().put(AppConstantes.QUERY, DatatypeConverter.printBase64Binary(query.getBytes("UTF-8")));
		
		response = providerRestTemplate.consumirServicio(request.getDatos(), urlDomino + CONSULTA_GENERICA, 
				authentication);
		
		listadatos = Arrays.asList(modelMapper.map(response.getDatos(), Map[].class));
		
		if( (listadatos != null) && (listadatos.size() > 0) ) {
			
			FinadoResponse finado = gson.fromJson(String.valueOf(listadatos.get(0)), FinadoResponse.class);;
			detalle.setFinado( finado );
		}
		
		response.setDatos(detalle);
		
		return response;
		
	}

	@Override
	public Response<Object> buscarNumRecPago(DatosRequest request, Authentication authentication) throws IOException {
		
		Gson gson = new Gson();
		FoliosRequest foliosRequest = gson.fromJson(String.valueOf(request.getDatos().get(AppConstantes.DATOS)), FoliosRequest.class);
		FacturacionUtil facturacionUtil = new FacturacionUtil();
		String query = facturacionUtil.foliosPagosSFPA( foliosRequest.getIdRegistro() );
		
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
