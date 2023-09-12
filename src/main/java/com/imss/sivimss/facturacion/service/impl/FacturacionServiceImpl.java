package com.imss.sivimss.facturacion.service.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.imss.sivimss.facturacion.service.FacturacionService;
import com.imss.sivimss.facturacion.util.DatosRequest;
import com.imss.sivimss.facturacion.util.Response;

import lombok.extern.log4j.Log4j2;

import com.imss.sivimss.facturacion.model.request.UsuarioDto;
import com.imss.sivimss.facturacion.model.request.CrearFacRequest;
import com.imss.sivimss.facturacion.model.request.FiltroRequest;
import com.imss.sivimss.facturacion.model.request.GenerarFacturaRequest;
import com.imss.sivimss.facturacion.util.AppConstantes;
import com.imss.sivimss.facturacion.util.MensajeResponseUtil;
import com.imss.sivimss.facturacion.util.FacturacionUtil;
import com.imss.sivimss.facturacion.util.LogUtil;
import com.imss.sivimss.facturacion.util.ProviderServiceRestTemplate;

@Log4j2
@Service
public class FacturacionServiceImpl implements FacturacionService {
	
	@Value("${endpoints.mod-catalogos}")
	private String urlDomino;
	
	@Autowired
	private LogUtil logUtil;
	
	@Autowired
	private ProviderServiceRestTemplate providerRestTemplate;
	
	private Response<Object>response;
	
	private static final String ERROR_INFORMACION = "52"; // Error al consultar la información.
	private static final String CONSULTA = "consulta";
	private static final String CONSULTA_PAGINADA = "/paginado";
	private static final String CONSULTA_GENERICA = "/consulta";
	private static final String GENERAR_FACTURA = "Generar Factura: " ;
	private static final String ERROR_ZIP = "Error al descomprimir el zip: ";
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

	@Override
	public Response<Object> generarFacturaPdf(DatosRequest request, Authentication authentication) throws IOException {
		GenerarFacturaRequest facturaRequest  = new Gson().fromJson(String.valueOf(request.getDatos().get(AppConstantes.DATOS)), GenerarFacturaRequest.class);
		byte[] zipData = Base64.getDecoder().decode(facturaRequest.getBase64EncodedZip());
		 try (ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(zipData))) {
	            ZipEntry entry;
	            while ((entry = zipInputStream.getNextEntry()) != null) {
	                byte[] data = IOUtils.toByteArray(zipInputStream);
	                byte[] encoded = Base64.getEncoder().encode(data);
	                if( entry.getName().contains("pdf")) {
	                	response= new Response<>(false, 200, "EXITO", new String(encoded));
	                }
	                zipInputStream.closeEntry();
	            }
		} catch (Exception e) {
			log.error(ERROR_ZIP );
			logUtil.crearArchivoLog(Level.WARNING.toString(), GENERAR_FACTURA + this.getClass().getSimpleName(),
					this.getClass().getPackage().toString(), ERROR_ZIP, GENERAR_FACTURA,authentication);
			throw new IOException(ERROR_INFORMACION, e.getCause());
		}
		return response;
	}

}
