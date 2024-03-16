package com.imss.sivimss.facturacion.service.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.IOUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.imss.sivimss.facturacion.service.FacturacionService;
import com.imss.sivimss.facturacion.util.DatosRequest;
import com.imss.sivimss.facturacion.util.Response;
import com.imss.sivimss.facturacion.util.SQLLoader;

import lombok.extern.log4j.Log4j2;

import com.imss.sivimss.facturacion.model.request.UsuarioDto;
import com.imss.sivimss.facturacion.configuration.MyBatisConfig;
import com.imss.sivimss.facturacion.configuration.mapper.Consultas;
import com.imss.sivimss.facturacion.model.entity.Bitacora;
import com.imss.sivimss.facturacion.model.entity.OrdenesServicio;
import com.imss.sivimss.facturacion.model.entity.PagoBitacora;
import com.imss.sivimss.facturacion.model.entity.Factura;
import com.imss.sivimss.facturacion.model.entity.PagoBitacoraDetalles;
import com.imss.sivimss.facturacion.model.request.Archivos;
import com.imss.sivimss.facturacion.model.request.CancelarFacRequest;
import com.imss.sivimss.facturacion.model.request.CorreoRequest;
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

import com.imss.sivimss.facturacion.model.response.FacturaBDResponse;
import com.imss.sivimss.facturacion.model.response.FacturaResponse;
import com.imss.sivimss.facturacion.model.response.VelatorioResponse;

import java.io.StringReader;

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
	
	@Value("${rfc_fibeso}")
	private String rfcFibeso;
	
	@Value("${endpoints.ms-reportes}")
	private String urlReportes;
	
	@Value("${formato_fecha}")
	private String formatoFecha;
	
	@Value("${endpoints.envio-correo-factura}")
	private String urlEnvioCorreo;
	
	@Autowired
	private LogUtil logUtil;
	
	@Autowired
	private ProviderServiceRestTemplate providerRestTemplate;
	
	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private MyBatisConfig myBatisConfig;

	@Autowired
	private SQLLoader sqlLoader;
	
	private static final String ERROR_INFORMACION = "52"; // Error al consultar la información.
	private static final String CONSULTA = "consulta";
	private static final String CONSULTA_PAGINADA = "/paginado";
	private static final String CONSULTA_GENERICA = "/consulta";
	private static final String GENERAR_FACTURA = "Generar Factura: " ;
	private static final String ERROR_ZIP = "Error al descomprimir el zip: ";
	private static final String SIN_INFORMACION = "45";
	private static final String CREAR = "/crear";
	private static final String ACTUALIZAR = "/actualizar";
	private static final String NOM_REPORTE = "reportes/modulo/FacturasCU79.jrxml";
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

	@SuppressWarnings("unchecked")
	@Override
	public Response<Object> crear(DatosRequest request, Authentication authentication) throws IOException {
		
		Gson gson = new Gson();
		CrearFacRequest crearFacRequest = gson.fromJson(String.valueOf(request.getDatos().get(AppConstantes.DATOS)), CrearFacRequest.class);
		UsuarioDto usuarioDto = gson.fromJson((String) authentication.getPrincipal(), UsuarioDto.class);
		Response<Object> response = new Response<Object>();
		FacturacionUtil facturacionUtil = new FacturacionUtil();
		String query = facturacionUtil.crear(crearFacRequest, usuarioDto.getIdUsuario());
		List<Map<String, Object>> listadatos;
		VelatorioResponse velatorio;
		
		
		logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(), 
				this.getClass().getPackage().toString(), "","Datos Entrada " + crearFacRequest, authentication);
		
		logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(), 
				this.getClass().getPackage().toString(), "",CONSULTA +" " + query, authentication);
		
		request.getDatos().put(AppConstantes.QUERY, DatatypeConverter.printBase64Binary(query.getBytes("UTF-8")));
		
		response = providerRestTemplate.consumirServicio(request.getDatos(), urlDomino + CREAR, 
				authentication);
		
		Integer idFactura = (Integer) response.getDatos();

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
		
		//Datos de Emisor
		
		document = new DocumentFields();
		document.setFieldName("nombre_emisor");
		document.setFieldValue("FIDEICOMISO DE BENEFICIOS SOCIALES");
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("rfc_emisor");
		document.setFieldValue(rfcFibeso);
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("cp_emisor");
		document.setFieldValue("06700");
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("calle_emisor");
		document.setFieldValue("ALVARO OBREGON");
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("numero_exterior_emisor");
		document.setFieldValue("121");
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("numero_interior_emisor");
		document.setFieldValue("903-904");
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("colonia_emisor");
		document.setFieldValue("ROMA NORTE");
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("municipio_emisor");
		document.setFieldValue("CUAUHTEMOC");
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("estado_emisor");
		document.setFieldValue("CIUDAD DE MEXICO");
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("pais_emisor");
		document.setFieldValue("MX");
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("telefono_emisor");
		document.setFieldValue("5557055418");
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("regimen_fiscal_emisor");
		document.setFieldValue("603");
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("clave_velatorio");
		document.setFieldValue( crearFacRequest.getIdVelatorio() );
		ep.getFields().add(document);
		
		// Consultando la informacion del Velatorio
		
		query = facturacionUtil.datosVelatorio( crearFacRequest.getIdVelatorio() );
		
		logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(), 
				this.getClass().getPackage().toString(), "",CONSULTA +" " + query, authentication);
		
		request.getDatos().put(AppConstantes.QUERY, DatatypeConverter.printBase64Binary(query.getBytes("UTF-8")));
		
		response = providerRestTemplate.consumirServicio(request.getDatos(), urlDomino + CONSULTA_GENERICA, 
				authentication);
		
		listadatos = Arrays.asList(modelMapper.map(response.getDatos(), Map[].class));
		
		velatorio = gson.fromJson(String.valueOf(listadatos.get(0)), VelatorioResponse.class);//(VelatorioResponse) listadatos.get(0);
		
		
		document = new DocumentFields();
		document.setFieldName("velatorio");
		document.setFieldValue( velatorio.getNomVelatorio());
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("cp_velatorio");
		document.setFieldValue( velatorio.getCp() );
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("calle_velatorio");
		document.setFieldValue( velatorio.getCalle() );
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("numero_exterior_velatorio");
		document.setFieldValue( velatorio.getNumExterior() );
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("numero_interior_velatorio");
		document.setFieldValue( velatorio.getNumInterior() );
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("colonia_velatorio");
		document.setFieldValue( velatorio.getColonia() );
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("municipio_velatorio");
		document.setFieldValue( velatorio.getMunicipio() );
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("estado_velatorio");
		document.setFieldValue( velatorio.getEstado() );
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
		
		logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(), 
				this.getClass().getPackage().toString(), "","fecha_pago " + crearFacRequest.getFecPago(), authentication);
		
		document = new DocumentFields();
		document.setFieldName("forma_pago");
		document.setFieldValue( crearFacRequest.getForPago().getDesForPago().substring(0, 2) );
		ep.getFields().add(document);
		
		logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(), 
				this.getClass().getPackage().toString(), "","forma_pago " + crearFacRequest.getForPago().getDesForPago().substring(0, 2), authentication);
		
		document = new DocumentFields();
		document.setFieldName("metodo_pago");
		document.setFieldValue( crearFacRequest.getMetPagoFac().getDesMetPagoFac().substring(0, 3) );
		ep.getFields().add(document);
		
		logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(), 
				this.getClass().getPackage().toString(), "","metodo_pago " + crearFacRequest.getMetPagoFac().getDesMetPagoFac().substring(0, 3), authentication);
		
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
			
			document = new DocumentFields();
			document.setFieldName("claveprodserv");
			document.setFieldValue( ser.getClaveProd() );
			ep.getFields().add(document);
			
			document = new DocumentFields();
			document.setFieldName("cantidad");
			document.setFieldValue( ser.getCantidad() );
			ep.getFields().add(document);
			
			document = new DocumentFields();
			document.setFieldName("precio_unitario");
			document.setFieldValue( ser.getImporte() );
			ep.getFields().add(document);
			
			String claveSat;
			String unidad;
			
			if( ser.getClaveSAT()!=null && !ser.getClaveSAT().isEmpty()) {
				
				String[] claves = ser.getClaveSAT().split(" ");
				claveSat = claves[1];
				unidad = claves[ claves.length -1 ];
				
			}
			else {
				
				claveSat = "H87";
				unidad = "PIEZA";
				
			}
			
			// Validar que es unidad
			document = new DocumentFields();
			document.setFieldName("unidad");
			document.setFieldValue( unidad );
			ep.getFields().add(document);
			
			document = new DocumentFields();
			document.setFieldName("objeto_impuesto");
			document.setFieldValue( "02" );
			ep.getFields().add(document);
			
			document = new DocumentFields();
			document.setFieldName("clave_unidad");
			document.setFieldValue( claveSat );
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
		document.setFieldValue( crearFacRequest.getObsManual() );
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
		document.setFieldValue( crearFacRequest.getCveRegimenFiscal() );
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("tipo_persona_receptor");
		
		if( crearFacRequest.getTipoPersona().contains("moral") ) {
			document.setFieldValue( "MORAL" );
		}else {
			document.setFieldValue( "FISICA" );
		}
		
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("correo_receptor");
		document.setFieldValue( crearFacRequest.getCorreo() );
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("giro_receptor");
		document.setFieldValue( crearFacRequest.getRegimenFiscal() );
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("uso_cfdi");
		document.setFieldValue( crearFacRequest.getCfdi().getDesCfdi().substring(0, 4));
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("cp_afiliado");
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
		
		document = new DocumentFields();
		document.setFieldName("nombre_finado");
		String nomFinado = "N/A";
		
		if( crearFacRequest.getTipoFactura().equals("1") 
			&& ( crearFacRequest.getFinado() != null )
			&& ( crearFacRequest.getFinado().getNomFinado() != null )
			&& ( !crearFacRequest.getFinado().getNomFinado().isEmpty() )
		) {
			
			nomFinado = crearFacRequest.getFinado().getNomFinado();
			
		}
		
		document.setFieldValue( nomFinado );
		ep.getFields().add(document);
		
		logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(), 
				this.getClass().getPackage().toString(), "","nombre_finado " + nomFinado, authentication);
		
		
		document = new DocumentFields();
		document.setFieldName("fecha_defuncion");
		String fecDefuncion = " ";
		
		if( crearFacRequest.getTipoFactura().equals("1")
			&& ( crearFacRequest.getFinado() != null ) 
			&& ( crearFacRequest.getFinado().getFecFinado() != null ) 
			&& ( !crearFacRequest.getFinado().getFecFinado().isEmpty() ) 
		) {
			
			fecDefuncion = crearFacRequest.getFinado().getFecFinado();
			
		}
		
		document.setFieldValue( fecDefuncion );
		ep.getFields().add(document);
		
		logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(), 
				this.getClass().getPackage().toString(), "","fecha_defuncion " + fecDefuncion, authentication);
		
		Services_Service service = new Services_Service();
		Services port = service.getServicesPort();
		String token = port.authenticate(user, pass);
		
		String datosFactura = gson.toJson(ep);
		
		logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(), 
				this.getClass().getPackage().toString(), "","Datos Factura " + datosFactura, authentication);
		
		ExecuteProcedureResponse1 factura = port.executeProcedure("apikey", token, ep);
		
		String respuesta = factura.getContent();		
		
		logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(), 
				this.getClass().getPackage().toString(), "","Respuesta Factura: " + respuesta, authentication);
		
		
		respuesta = respuesta.replace("<xml>", "");
		respuesta = respuesta.replace("</xml>", "");
		
		respuesta = respuesta.replace("<action name=\"cfdi\" command=\"data\" debug=\"false\">", "");
		respuesta = respuesta.replace("</action>", "");
		
		respuesta = respuesta.replace("<![CDATA[", "");
		respuesta = respuesta.replace("]]>", "");
		
		respuesta = respuesta.replace("<row __num=\"0\">", "");
		respuesta = respuesta.replace("</row>", "");
		
		FacturaResponse resPar = null;
		
		try {
			
			JAXBContext context = JAXBContext.newInstance( FacturaResponse.class );
			Unmarshaller unmarshaller = context.createUnmarshaller();
			StringReader reader = new StringReader(respuesta);
			resPar = (FacturaResponse) unmarshaller.unmarshal( reader );
			
		} catch (JAXBException e) {
			log.info(e.getMessage());
			throw new IOException("Error al generar la factura");
		}
		
		if( !resPar.getCodigo_error().equals("") ) {
			
			//Eliminamos el reguistro anterior
			query = facturacionUtil.canFact(idFactura);
			
			logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(), 
					this.getClass().getPackage().toString(), "",CONSULTA +" " + query, authentication);
			
			request.getDatos().put(AppConstantes.QUERY, DatatypeConverter.printBase64Binary(query.getBytes("UTF-8")));
			
			response = providerRestTemplate.consumirServicio(request.getDatos(), urlDomino + ACTUALIZAR, 
					authentication);
			
			throw new IOException("Error al generar la factura");
			
		}
		
		//Obtenemos el PDF
		String pdf = "";
		
		byte[] zipData = Base64.getDecoder().decode( resPar.getZip() );
		 try (ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(zipData))) {
	            ZipEntry entry;
	            while ((entry = zipInputStream.getNextEntry()) != null) {
	                byte[] data = IOUtils.toByteArray(zipInputStream);
	                byte[] encoded = Base64.getEncoder().encode(data);
	                if( entry.getName().contains("pdf")) {
	                	
	                	pdf = new String(encoded);

	                }
	                zipInputStream.closeEntry();
	            }
		} catch (Exception e) {
			log.error(ERROR_ZIP );
			logUtil.crearArchivoLog(Level.WARNING.toString(), GENERAR_FACTURA + this.getClass().getSimpleName(),
					this.getClass().getPackage().toString(), ERROR_ZIP, GENERAR_FACTURA,authentication);
			throw new IOException(ERROR_INFORMACION, e.getCause());
		}
		
		query = facturacionUtil.actFact(idFactura, pdf, resPar);
			
		logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(), 
				this.getClass().getPackage().toString(), "",CONSULTA +" " + query, authentication);
		
		request.getDatos().put(AppConstantes.QUERY, DatatypeConverter.printBase64Binary(query.getBytes("UTF-8")));
		
		response = providerRestTemplate.consumirServicio(request.getDatos(), urlDomino + ACTUALIZAR, 
				authentication);
		
		if( !crearFacRequest.getTipoFactura().equals("4") ) {
			
			query = facturacionUtil.actPB(crearFacRequest.getIdPagoBitacora(), usuarioDto.getIdUsuario());
			
			logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(), 
					this.getClass().getPackage().toString(), "",CONSULTA +" " + query, authentication);
			
			request.getDatos().put(AppConstantes.QUERY, DatatypeConverter.printBase64Binary(query.getBytes("UTF-8")));
			
			response = providerRestTemplate.consumirServicio(request.getDatos(), urlDomino + ACTUALIZAR, 
					authentication);
		}
		
		response.setCodigo(200);
		response.setMensaje("Exito");
		response.setDatos(idFactura);
		
		log.info("----------------------------------------------------------------------------------------------------------------");
		//UsuarioDto usuarioDto = gson.fromJson((String) authentication.getPrincipal(), UsuarioDto.class);
		SqlSessionFactory sqlSessionFactory = myBatisConfig.buildqlSessionFactory();
		Factura pagoDespues = new Factura();
		List<PagoBitacoraDetalles> pagoDetallesDespues = null;
		OrdenesServicio ordenesServAntesDespues = null;
		try (SqlSession session = sqlSessionFactory.openSession()) {
			Consultas consultas = session.getMapper(Consultas.class);

			try {
				String consultaFactura = sqlLoader.getConsultaFacturaPorIdFactura();
				Factura regisFactura = consultas.cosultaFacturaPorFolioFiscal(consultaFactura.replace("#{idBitacora}", ""+idFactura));


				String queryBitacora = sqlLoader.getBitacoraNuevoRegistro();
				consultas.insertData(queryBitacora, new Bitacora(1, "SVC_FACTURA", null, regisFactura.toString(), usuarioDto.getIdUsuario()));
							
				session.commit();
			} catch (Exception e) {
				session.rollback();
				log.error(e.getMessage());
			}
		}
		log.info("----------------------------------------------------------------------------------------------------------------");
		
		
		return response;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Response<Object> generarFacturaPdf(DatosRequest request, Authentication authentication) throws IOException {
		
		Gson gson = new Gson();
		GenerarFacturaRequest generarFacturaRequest = gson.fromJson(String.valueOf(request.getDatos().get(AppConstantes.DATOS)), GenerarFacturaRequest.class);
		FacturacionUtil facturacionUtil = new FacturacionUtil();
		String query = facturacionUtil.buscarPDF(generarFacturaRequest.getIdFactura());
		List<Map<String, Object>> listadatos;
		
		logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(), 
				this.getClass().getPackage().toString(), "",CONSULTA +" " + query, authentication);
		
		request.getDatos().put(AppConstantes.QUERY, DatatypeConverter.printBase64Binary(query.getBytes("UTF-8")));
		
		Response<Object> response = providerRestTemplate.consumirServicio(request.getDatos(), urlDomino + CONSULTA_GENERICA, 
				authentication);
		
		listadatos = Arrays.asList(modelMapper.map(response.getDatos(), Map[].class));
		String pdf = String.valueOf(listadatos.get(0).get("pdf"));
		response.setDatos( pdf );
		
		return MensajeResponseUtil.mensajeConsultaResponse( response, SIN_INFORMACION );
	}

	@SuppressWarnings("unchecked")
	@Override
	public Response<Object> cancelar(DatosRequest request, Authentication authentication) throws IOException {
		
		Gson gson = new Gson();
		CancelarFacRequest cancelarFacRequest = gson.fromJson(String.valueOf(request.getDatos().get(AppConstantes.DATOS)), CancelarFacRequest.class);
		UsuarioDto usuarioDto = gson.fromJson((String) authentication.getPrincipal(), UsuarioDto.class);
		FacturacionUtil facturacionUtil = new FacturacionUtil();
		FacturaBDResponse factura;
		List<Map<String, Object>> listadatos;
		Response<Object> response = new Response<Object>();
		
		
		
		
		
		
		String query = facturacionUtil.datosFactura( cancelarFacRequest.getFolioFactura(), formatoFecha );
		
		logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(), 
				this.getClass().getPackage().toString(), "",CONSULTA +" " + query, authentication);
		
		request.getDatos().put(AppConstantes.QUERY, DatatypeConverter.printBase64Binary(query.getBytes("UTF-8")));
		
		response = providerRestTemplate.consumirServicio(request.getDatos(), urlDomino + CONSULTA_GENERICA, 
				authentication);
		
		listadatos = Arrays.asList(modelMapper.map(response.getDatos(), Map[].class));
		
		factura = gson.fromJson(String.valueOf(listadatos.get(0)), FacturaBDResponse.class);
		
		//Haciendo el consumo de la Factura
		
		ExecuteProcedure1 ep = new ExecuteProcedure1();
		ep.setAppName( appName );
		ep.setOwner( owner );
		ep.setProcedure( pCancelar );
		
		DocumentFields document = new DocumentFields();
		document.setFieldName("transaccion");
		document.setFieldValue("2");
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("total");
		document.setFieldValue( factura.getTotal() );
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("folio_factura");
		document.setFieldValue( cancelarFacRequest.getFolioFactura() );
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("folio_fiscal");
		document.setFieldValue( cancelarFacRequest.getFolioFiscal() );
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("rfc_emisor");
		document.setFieldValue(rfcFibeso);
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("cp_emisor");
		document.setFieldValue("06700");
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("regimen_fiscal_emisor");
		document.setFieldValue("603");
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("motivo_cancelacion");
		document.setFieldValue(cancelarFacRequest.getMotivoCancelacion().getClave());
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("folio_relacionado");
		String folioRelacionado = "";
		if( cancelarFacRequest.getFolioRelacionado() != null ) {
			folioRelacionado = cancelarFacRequest.getFolioRelacionado();
		}
		document.setFieldValue(folioRelacionado);
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("fecha_cancelacion");
		document.setFieldValue(factura.getFecCan());
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("razon_social_receptor");
		document.setFieldValue(factura.getRazonSocial());
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("rfc_receptor");
		document.setFieldValue(factura.getRfc());
		ep.getFields().add(document);
		
		document = new DocumentFields();
		document.setFieldName("usuario_elaboracion");
		document.setFieldValue( usuarioDto.getNombre() );
		ep.getFields().add(document);
		
		Services_Service service = new Services_Service();
		Services port = service.getServicesPort();
		String token = port.authenticate(user, pass);
		
		logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(), 
				this.getClass().getPackage().toString(), "","Datos Factura: " + ep, authentication);
		
		ExecuteProcedureResponse1 facturaResponse = port.executeProcedure("apikey", token, ep);
		
		String respuesta = facturaResponse.getContent();
		
		logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(), 
				this.getClass().getPackage().toString(), "","Respuesta Factura: " + respuesta, authentication);
		
		
		
		
		log.info(
				"----------------------------------------------------------------------------------------------------------------");
		SqlSessionFactory sqlSessionFactory = myBatisConfig.buildqlSessionFactory();
		PagoBitacora pagoAntes = new PagoBitacora();
		List<PagoBitacoraDetalles> pagoDetallesAntes = null;
		OrdenesServicio ordenesServAntes = null;
		String folioFiscal = cancelarFacRequest.getFolioFiscal();
		Factura regisFacturaAntes = new Factura();
		try (SqlSession session = sqlSessionFactory.openSession()) {

			Consultas consultas = session.getMapper(Consultas.class);

			try {
				String consultaFactura = sqlLoader.getConsultaFacturaPorFolioFiscal();
				regisFacturaAntes = consultas
						.cosultaFacturaPorFolioFiscal(consultaFactura.replace("#{idBitacora}", "" + folioFiscal));

			} catch (Exception e) {
				log.error(e.getMessage());
			}
		}
		log.info(
				"----------------------------------------------------------------------------------------------------------------");
		
		
		
		query = facturacionUtil.cancelar(cancelarFacRequest, usuarioDto.getIdUsuario());
		
		logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(), 
				this.getClass().getPackage().toString(), "",CONSULTA +" " + query, authentication);
		
		request.getDatos().put(AppConstantes.QUERY, DatatypeConverter.printBase64Binary(query.getBytes("UTF-8")));
		

		Factura regisFacturaDespues = new Factura();
		try (SqlSession session = sqlSessionFactory.openSession()) {

			Consultas consultas = session.getMapper(Consultas.class);

			try {
				String consultaFactura = sqlLoader.getConsultaFacturaPorFolioFiscal();
				regisFacturaDespues = consultas
						.cosultaFacturaPorFolioFiscal(consultaFactura.replace("#{idBitacora}", "" + folioFiscal));

				String queryBitacora = sqlLoader.getBitacoraNuevoRegistro();
				consultas.insertData(queryBitacora,
						new Bitacora(1, "SVC_FACTURA", regisFacturaAntes.toString(), regisFacturaDespues.toString().toString(), usuarioDto.getIdUsuario()));

			} catch (Exception e) {
				log.error(e.getMessage());
			}
		}
		log.info(
				"----------------------------------------------------------------------------------------------------------------");


		return providerRestTemplate.consumirServicio(request.getDatos(), urlDomino + ACTUALIZAR, 
				authentication);
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public Response<Object> generarRF(DatosRequest request, Authentication authentication) throws IOException {
		
		Gson gson = new Gson();
		FiltroRequest filtrosRequest = gson.fromJson(String.valueOf(request.getDatos().get(AppConstantes.DATOS)), FiltroRequest.class);
		FacturacionUtil facturacionUtil = new FacturacionUtil();
		String query = facturacionUtil.totalFactura(filtrosRequest);
		Response<Object> response;
		List<Map<String, Object>> listadatos;
		Double total;
		String velatorio;
		
		logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(), 
				this.getClass().getPackage().toString(), "",CONSULTA +" " + query, authentication);
		
		request.getDatos().put(AppConstantes.QUERY, DatatypeConverter.printBase64Binary(query.getBytes("UTF-8")));
		
		response = providerRestTemplate.consumirServicio(request.getDatos(), urlDomino + CONSULTA_GENERICA, 
				authentication);
		
		listadatos = Arrays.asList(modelMapper.map(response.getDatos(), Map[].class));
		
		total = (Double) listadatos.get(0).get("total");
		
		if( filtrosRequest.getIdVelatorio() == null ) {
			
			velatorio = "Todos";
			
		}else {
			
			query = facturacionUtil.nomVelatorio(filtrosRequest.getIdVelatorio());
			request.getDatos().put(AppConstantes.QUERY, DatatypeConverter.printBase64Binary(query.getBytes("UTF-8")));
			response = providerRestTemplate.consumirServicio(request.getDatos(), urlDomino + CONSULTA_GENERICA, 
					authentication);
			listadatos = Arrays.asList(modelMapper.map(response.getDatos(), Map[].class));
			velatorio = (String) listadatos.get(0).get("nombre");
			
		}
		
		Map<String, Object> envioDatos = facturacionUtil.reporteCU079(filtrosRequest, NOM_REPORTE);
		envioDatos.put("total", total.toString() );
		envioDatos.put("velatorio", velatorio );
		
		if(filtrosRequest.getTipoReporte().equals("xls")) {
            envioDatos.put("IS_IGNORE_PAGINATION", true);
        }
		log.info(envioDatos.get("filtros"));
		logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(), 
				this.getClass().getPackage().toString(), "",CONSULTA +" " + envioDatos, authentication);
		
		response = providerRestTemplate.consumirServicioReportes(envioDatos, urlReportes,authentication);
		
		response = MensajeResponseUtil.mensajeConsultaResponse(response, ERROR_AL_DESCARGAR_DOCUMENTO);

		return response;
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public Response<Object> reenviarFac(DatosRequest request, Authentication authentication) throws IOException {
		Gson gson = new Gson();
		GenerarFacturaRequest generarFacturaRequest = gson.fromJson(String.valueOf(request.getDatos().get(AppConstantes.DATOS)), GenerarFacturaRequest.class);
		FacturacionUtil facturacionUtil = new FacturacionUtil();
		String query = facturacionUtil.buscarArchivos(generarFacturaRequest.getIdFactura());
		List<Map<String, Object>> listadatos;
		logUtil.crearArchivoLog(Level.INFO.toString(), this.getClass().getSimpleName(), 
				this.getClass().getPackage().toString(), "",CONSULTA +" " + query, authentication);
		
		request.getDatos().put(AppConstantes.QUERY, DatatypeConverter.printBase64Binary(query.getBytes("UTF-8")));
		
		Response<Object> response = providerRestTemplate.consumirServicio(request.getDatos(), urlDomino + CONSULTA_GENERICA, 
				authentication);
		
		listadatos = Arrays.asList(modelMapper.map(response.getDatos(), Map[].class));
		
		CorreoRequest correo = new CorreoRequest();
		correo.setTipoCorreo( "facturaIMSS" );
		correo.setNombre( String.valueOf(listadatos.get(0).get("razonSocial")) );
		correo.setCorreoPara( generarFacturaRequest.getCorreo() );
		correo.setAsunto( "Factura IMSS" );
		correo.setCuerpoCorreo( "Se envia factura" );
		correo.setRemitente( "gestion.derechohabientes@imss.gob.mx" );
		
		String nombre = String.valueOf(listadatos.get(0).get("folioFiscal"));
		Archivos archivos = new Archivos();
		List<Archivos> adjuntos = new ArrayList<>();
		
		archivos.setNombreAdjunto( nombre + ".pdf" );
		archivos.setAdjuntoBase64( String.valueOf(listadatos.get(0).get("arcPdf")) );
		adjuntos.add(archivos);
		
		
		archivos = new Archivos();
		archivos.setNombreAdjunto( nombre + ".xml" );
		String xml = String.valueOf(listadatos.get(0).get("arcXml"));
		xml = xml.trim();
		xml = DatatypeConverter.printBase64Binary(xml.getBytes("UTF-8"));
		
		archivos.setAdjuntoBase64( xml );
		adjuntos.add(archivos);
		correo.setAdjuntos(adjuntos);
		
		
		response = providerRestTemplate.consumirCorreo(correo, urlEnvioCorreo);
		
		if (response.getCodigo() != 200) {
			return response;
		}
		
		response =  new Response<>(false, HttpStatus.OK.value(), "Exito",
				null );
		
		return response;
	}
	
}
