package com.imss.sivimss.facturacion.service.rfc;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

public class ConfigurationRfc {
	
	@Value("${endpoints.consulta-rfc}")
	private String urlRfc;

	@Bean
	public Jaxb2Marshaller marshaller() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setContextPath("com.imss.sivimss.facturacion.service.rfc");
		return marshaller;
	}

	@Bean
	public SOAPConnectClient soapconnector(Jaxb2Marshaller marshaller) {
		SOAPConnectClient client = new SOAPConnectClient();
		client.setDefaultUri(urlRfc);
		client.setMarshaller(marshaller);
		client.setUnmarshaller(marshaller);
		return client;
	}
}
