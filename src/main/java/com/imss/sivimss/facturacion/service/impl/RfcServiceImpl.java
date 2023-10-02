package com.imss.sivimss.facturacion.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.imss.sivimss.facturacion.model.request.RfcRequest;
import com.imss.sivimss.facturacion.model.response.RfcResponse;
import com.imss.sivimss.facturacion.service.RfcService;
import com.imss.sivimss.facturacion.service.rfc.ConfigurationRfc;
import com.imss.sivimss.facturacion.service.rfc.EntradaSAT;
import com.imss.sivimss.facturacion.service.rfc.GetPatron;
import com.imss.sivimss.facturacion.service.rfc.GetPatronResponse;
import com.imss.sivimss.facturacion.service.rfc.SOAPConnectClient;
import com.imss.sivimss.facturacion.util.AppConstantes;
import com.imss.sivimss.facturacion.util.DatosRequest;
import com.imss.sivimss.facturacion.util.Response;

@Service
public class RfcServiceImpl implements RfcService {

	@Value("${endpoints.consulta-rfc}")
	private String urlRfc;
	
	@Override
	public Response<Object> buscar(DatosRequest request, Authentication authentication) throws Exception {
		
		Gson gson = new Gson();
		RfcRequest rfcRequest = gson.fromJson(String.valueOf(request.getDatos().get(AppConstantes.DATOS)), RfcRequest.class);
		Response<Object> response = new Response<>();
		
		EntradaSAT entradaSAT = new EntradaSAT();
		entradaSAT.setRfc(rfcRequest.getRfc());
		GetPatron patron = new GetPatron();
		patron.setDatosEntrada(entradaSAT);
		ConfigurationRfc configurationRfc = new ConfigurationRfc();
		Jaxb2Marshaller marshaller = configurationRfc.marshaller();
		SOAPConnectClient client = configurationRfc.soapconnector(marshaller);
		GetPatronResponse salida = (GetPatronResponse) client.callWebServices(urlRfc, patron);
		
		RfcResponse rfcResponse = new RfcResponse();
		
		rfcResponse.setRazonSocial(salida.getReturn().getIdentificacion().get(0).getNomComercial());
		
		if( salida.getReturn().getIdentificacion().get(0).getTPersona().equals("F") ) {
			rfcResponse.setTipoPersona(AppConstantes.PERSONA_FISICA);
		}else {
			rfcResponse.setTipoPersona(AppConstantes.PERSONA_MORAL);
		}
		
		String cveRegimenFiscal = salida.getReturn().getRegimen().get(0).getCRegimen();
		rfcResponse.setCveRegimenFiscal( cveRegimenFiscal ); 
		StringBuilder reg = new StringBuilder( cveRegimenFiscal );
		
		reg.append(" ");
		reg.append(salida.getReturn().getRegimen().get(0).getDRegimen());
		rfcResponse.setRegimenFiscal( reg.toString() );
		
		rfcResponse.setDomicilioFiscal( salida.getReturn().getUbicacion().get(0) );
		
		response =  new Response<>(false, HttpStatus.OK.value(), "Exito",
				rfcResponse );
		
		return response;
	}

}
