package com.imss.sivimss.facturacion.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.annotation.PostConstruct;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import lombok.Data;

@Component
@Data
public class SQLLoader {

    private final ResourceLoader resourceLoader;

    private String bitacoraNuevoRegistro;
    private String consultaFacturaPorIdFactura;
    private String consultaFacturaPorFolioFiscal;

    public SQLLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    public void init() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:sql/BITACORA_CREAR_REGISTRO.SQL");
        byte[] bytes = FileCopyUtils.copyToByteArray(resource.getInputStream());
        bitacoraNuevoRegistro = new String(bytes, StandardCharsets.UTF_8);

        resource = resourceLoader.getResource("classpath:sql/FACTURA_CONSULTA_POR_ID.SQL");
        bytes = FileCopyUtils.copyToByteArray(resource.getInputStream());
        consultaFacturaPorIdFactura = new String(bytes, StandardCharsets.UTF_8);

        resource = resourceLoader.getResource("classpath:sql/FACTURA_CONSULTA_POR_FOLIO_FISCAL.SQL");
        bytes = FileCopyUtils.copyToByteArray(resource.getInputStream());
        consultaFacturaPorFolioFiscal = new String(bytes, StandardCharsets.UTF_8);
    }

    
}
