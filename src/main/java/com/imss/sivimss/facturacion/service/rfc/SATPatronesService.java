
package com.imss.sivimss.facturacion.service.rfc;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.9-b130926.1035
 * Generated source version: 2.2
 * 
 */
@WebServiceClient(name = "SATPatronesService", targetNamespace = "http://mx/gob/imss/didt/cia/interoper/sat/ws", wsdlLocation = "http://172.16.23.207/ServiciosPerifericos/ConsultaRFC.ws?wsdl")
public class SATPatronesService
    extends Service
{

    private final static URL SATPATRONESSERVICE_WSDL_LOCATION;
    private final static WebServiceException SATPATRONESSERVICE_EXCEPTION;
    private final static QName SATPATRONESSERVICE_QNAME = new QName("http://mx/gob/imss/didt/cia/interoper/sat/ws", "SATPatronesService");

    static {
        URL url = null;
        WebServiceException e = null;
        try {
            url = new URL("http://172.16.23.207/ServiciosPerifericos/ConsultaRFC.ws?wsdl");
        } catch (MalformedURLException ex) {
            e = new WebServiceException(ex);
        }
        SATPATRONESSERVICE_WSDL_LOCATION = url;
        SATPATRONESSERVICE_EXCEPTION = e;
    }

    public SATPatronesService() {
        super(__getWsdlLocation(), SATPATRONESSERVICE_QNAME);
    }

    public SATPatronesService(WebServiceFeature... features) {
        super(__getWsdlLocation(), SATPATRONESSERVICE_QNAME, features);
    }

    public SATPatronesService(URL wsdlLocation) {
        super(wsdlLocation, SATPATRONESSERVICE_QNAME);
    }

    public SATPatronesService(URL wsdlLocation, WebServiceFeature... features) {
        super(wsdlLocation, SATPATRONESSERVICE_QNAME, features);
    }

    public SATPatronesService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public SATPatronesService(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
        super(wsdlLocation, serviceName, features);
    }

    /**
     * 
     * @return
     *     returns SATPatrones
     */
    @WebEndpoint(name = "SATPatronesSoapPort")
    public SATPatrones getSATPatronesSoapPort() {
        return super.getPort(new QName("http://mx/gob/imss/didt/cia/interoper/sat/ws", "SATPatronesSoapPort"), SATPatrones.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns SATPatrones
     */
    @WebEndpoint(name = "SATPatronesSoapPort")
    public SATPatrones getSATPatronesSoapPort(WebServiceFeature... features) {
        return super.getPort(new QName("http://mx/gob/imss/didt/cia/interoper/sat/ws", "SATPatronesSoapPort"), SATPatrones.class, features);
    }

    private static URL __getWsdlLocation() {
        if (SATPATRONESSERVICE_EXCEPTION!= null) {
            throw SATPATRONESSERVICE_EXCEPTION;
        }
        return SATPATRONESSERVICE_WSDL_LOCATION;
    }

}