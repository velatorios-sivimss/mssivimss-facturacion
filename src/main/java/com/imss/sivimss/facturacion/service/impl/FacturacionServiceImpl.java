package com.imss.sivimss.facturacion.service.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
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
import com.imss.sivimss.facturacion.model.request.ServiciosRequest;
import com.imss.sivimss.facturacion.util.AppConstantes;
import com.imss.sivimss.facturacion.util.MensajeResponseUtil;
import com.imss.sivimss.facturacion.util.FacturacionUtil;
import com.imss.sivimss.facturacion.util.LogUtil;
import com.imss.sivimss.facturacion.util.ProviderServiceRestTemplate;

import mx.gob.imss.cit.clienteswebservices.externo.jonima.DocumentFields;
import mx.gob.imss.cit.clienteswebservices.externo.jonima.ExecuteProcedure1;
import mx.gob.imss.cit.clienteswebservices.externo.jonima.ExecuteProcedureResponse1;
import mx.gob.imss.cit.clienteswebservices.externo.jonima.Services;
import mx.gob.imss.cit.clienteswebservices.externo.jonima.Services_Service;

@Log4j2
@Service
public class FacturacionServiceImpl implements FacturacionService {
	
	@Value("${endpoints.mod-catalogos}")
	private String urlDomino;
	
	@Value("${jonima_user}")
	private String user;

	@Value("${jonima_pass}")
	private String pass;
	
	@Value("${jonima_appName}")
	private String appName;
	
	@Value("${jonima_owner}")
	private String owner;
	
	@Value("${jonima_procedure_crear}")
	private String pCrear;
	
	@Value("${jonima_procedure_cancelar}")
	private String pCancelar;
	
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
	private static final String CREAR = "/crear";
	
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
		Response<Object> response = new Response<Object>();
		FacturacionUtil facturacionUtil = new FacturacionUtil();
		String query = facturacionUtil.crear(crearFacRequest, usuarioDto.getIdUsuario());
		
		logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(), 
				this.getClass().getPackage().toString(), "",CONSULTA +" " + query, authentication);
		
		request.getDatos().put(AppConstantes.QUERY, DatatypeConverter.printBase64Binary(query.getBytes("UTF-8")));
		
		//response = providerRestTemplate.consumirServicio(request.getDatos(), urlDomino + CREAR, 
		//		authentication);
		
		Integer idFactura = 5; //(Integer) response.getDatos();
		
		// TODO Auto-generated method stub
		//Haciendo el consumo de la Factura
		
		ExecuteProcedure1 ep = new ExecuteProcedure1();
		ep.setAppName( appName );
		ep.setOwner( owner );
		ep.setProcedure( pCrear );
		
		DocumentFields document = new DocumentFields();
		document.setFieldName("transaccion");
		document.setFieldValue("1");
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("tipo_proceso");
		document.setFieldValue("2");
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("folio_factura");
		document.setFieldValue(idFactura.toString());
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("serie_factura");
		document.setFieldValue("A");
		ep.getFields().add(document);
		
		// TODO Auto-generated method stub
		//Revisar datos de Emisor
		
		document = new DocumentFields();
		document.setFieldName("nombre_emisor");
		document.setFieldValue("FIDEICOMISO DE BENEFICIOS SOCIALES");
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("rfc_emisor");
		document.setFieldValue("EWE1709045U0");
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("cp_emisor");
		document.setFieldValue("06720");
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("calle_emisor");
		document.setFieldValue("Calle de emision");
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("numero_exterior_emisor");
		document.setFieldValue("115");
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("numero_interior_emisor");
		document.setFieldValue("25");
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("colonia_emisor");
		document.setFieldValue("San Rafael");
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("municipio_emisor");
		document.setFieldValue("Cuahutemoc");
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("estado_emisor");
		document.setFieldValue("CDMX");
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("pais_emisor");
		document.setFieldValue("MX");
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("telefono_emisor");
		document.setFieldValue("5553259000");
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("regimen_fiscal_emisor");
		document.setFieldValue("603");
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("clave_velatorio");
		document.setFieldValue( crearFacRequest.getIdVelatorio() );
		ep.getFields().add(document);
		
		// TODO Auto-generated method stub
		// Falta consultar la informacion del Velatorio
		
		document = new DocumentFields();
		document.setFieldName("velatorio");
		document.setFieldValue( "VELATORIO 1" );
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("cp_velatorio");
		document.setFieldValue( "06720" );
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("calle_velatorio");
		document.setFieldValue( "Dr. RAFAEL LUCIO" );
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("numero_exterior_velatorio");
		document.setFieldValue( "237" );
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("numero_interior_velatorio");
		document.setFieldValue( "" );
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("colonia_velatorio");
		document.setFieldValue( "Doctores" );
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("municipio_velatorio");
		document.setFieldValue( "CUAUHTEMOC" );
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("estado_velatorio");
		document.setFieldValue( "CIUDAD DE MEXICO" );
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("pais_velatorio");
		document.setFieldValue( "MEXICO" );
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("usuario_elaboracion");
		document.setFieldValue( usuarioDto.getNombre() );
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("fecha_pago");
		document.setFieldValue( crearFacRequest.getFecPago() );
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("forma_pago");
		document.setFieldValue( crearFacRequest.getForPago().getDesForPago() );
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("metodo_pago");
		document.setFieldValue( crearFacRequest.getMetPagoFac().getDesMetPagoFac() );
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("folio_ods_convenio_permiso");
		document.setFieldValue( crearFacRequest.getFolio() );
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("numero_cuota");
		document.setFieldValue( "" );
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("concepto");
		document.setFieldValue( crearFacRequest.getConcPago() );
		ep.getFields().add(document);
		
		// TODO Auto-generated method stub
		// Validar que es tipo_pago
		document = new DocumentFields();
		document.setFieldName("tipo_pago");
		document.setFieldValue( "5" );
		ep.getFields().add(document);
		
		for( ServiciosRequest ser : crearFacRequest.getServicios() ) {
			
			// TODO Auto-generated method stub
			document = new DocumentFields();
			document.setFieldName("claveprodserv");
			document.setFieldValue( "48131502" );
			ep.getFields().add(document);
			
			document = new DocumentFields();
			document.setFieldName("cantidad");
			document.setFieldValue( ser.getCantidad() );
			ep.getFields().add(document);
			
			document = new DocumentFields();
			document.setFieldName("precio_unitario");
			document.setFieldValue( ser.getImporte() );
			ep.getFields().add(document);
			
			// TODO Auto-generated method stub
			// Validar que es unidad
			document = new DocumentFields();
			document.setFieldName("unidad");
			document.setFieldValue( "PIEZA" );
			ep.getFields().add(document);
			
			document = new DocumentFields();
			document.setFieldName("objeto_impuesto");
			document.setFieldValue( "02" );
			ep.getFields().add(document);
		
			// TODO Auto-generated method stub
			// Validar que es clave unidad
			document = new DocumentFields();
			document.setFieldName("clave_unidad");
			document.setFieldValue( "H87" );
			ep.getFields().add(document);
			
			document = new DocumentFields();
			document.setFieldName("descripcion");
			document.setFieldValue( ser.getConcepto() );
			ep.getFields().add(document);
			
		}
		
		document = new DocumentFields();
		document.setFieldName("total_pagado");
		document.setFieldValue( crearFacRequest.getTotalServicios() );
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("observaciones_servicio_funerario");
		document.setFieldValue( crearFacRequest.getObsAutomatica() );
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("observaciones");
		document.setFieldValue( crearFacRequest.getObsAutomatica() );
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("razon_social_receptor");
		document.setFieldValue( crearFacRequest.getRazonSocial() );
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("rfc_receptor");
		document.setFieldValue( crearFacRequest.getRfc() );
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("regimen_fiscal_receptor");
		document.setFieldValue( crearFacRequest.getRegimenFiscal() );
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("tipo_persona_receptor");
		document.setFieldValue( crearFacRequest.getTipoPersona() );
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("correo_receptor");
		document.setFieldValue( crearFacRequest.getCorreo() );
		ep.getFields().add(document);
		
		// TODO Auto-generated method stub
		// Validar que es giro receptor
		document = new DocumentFields();
		document.setFieldName("giro_receptor");
		document.setFieldValue( "EMPLEADO" );
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("uso_cfdi");
		document.setFieldValue( crearFacRequest.getCfdi().getDesCfdi() );
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("cp_afiliad");
		document.setFieldValue( crearFacRequest.getDomicilioFiscal().getCp() );
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("calle_afiliado");
		document.setFieldValue( crearFacRequest.getDomicilioFiscal().getCalle() );
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("numero_afiliadoexterior_afiliado");
		document.setFieldValue( crearFacRequest.getDomicilioFiscal().getNexterior() );
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("numero_afiliadointerior_afiliado");
		document.setFieldValue( crearFacRequest.getDomicilioFiscal().getNinterior() );
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("colonia_afiliado");
		document.setFieldValue( crearFacRequest.getDomicilioFiscal().getDcolonia() );
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("municipio_afiliado");
		document.setFieldValue( crearFacRequest.getDomicilioFiscal().getDmunicipio() );
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("estado_afiliado");
		document.setFieldValue( crearFacRequest.getDomicilioFiscal().getDentFed() );
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("pais_afiliado");
		document.setFieldValue( crearFacRequest.getDomicilioFiscal().getPaisResidencia() );
		ep.getFields().add(document);
		
		// TODO Auto-generated method stub
		// Falta ir por los datos del Finado
		document = new DocumentFields();
		document.setFieldName("nombre_finado");
		document.setFieldValue( "LUCILA GUADALUPE VARGAS HERNANDEZ" );
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("fecha_defuncion");
		document.setFieldValue( "21/08/2023" );
		ep.getFields().add(document);
		
		Services_Service service = new Services_Service();
		Services port = service.getServicesPort();
		String token = port.authenticate(user, pass);
		
		ExecuteProcedureResponse1 factura = port.executeProcedure("apikey", token, ep);
		
		response.setCodigo(200);
		response.setMensaje("Exito");
		response.setDatos(factura);
		
		
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
