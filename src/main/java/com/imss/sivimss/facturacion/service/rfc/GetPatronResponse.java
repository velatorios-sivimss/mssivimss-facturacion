
package com.imss.sivimss.facturacion.service.rfc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para anonymous complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="return" type="{java:mx.gob.imss.didt.cia.interoper.sat.pojos.salida}SalidaSAT"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "_return"
})
@XmlRootElement(name = "getPatronResponse", namespace = "http://mx/gob/imss/didt/cia/interoper/sat/ws")
public class GetPatronResponse {

    @XmlElement(name = "return", namespace = "http://mx/gob/imss/didt/cia/interoper/sat/ws", required = true)
    protected SalidaSAT _return;

    /**
     * Obtiene el valor de la propiedad return.
     * 
     * @return
     *     possible object is
     *     {@link SalidaSAT }
     *     
     */
    public SalidaSAT getReturn() {
        return _return;
    }

    /**
     * Define el valor de la propiedad return.
     * 
     * @param value
     *     allowed object is
     *     {@link SalidaSAT }
     *     
     */
    public void setReturn(SalidaSAT value) {
        this._return = value;
    }

}
