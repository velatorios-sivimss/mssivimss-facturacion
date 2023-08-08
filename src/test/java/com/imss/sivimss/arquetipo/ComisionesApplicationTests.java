package com.imss.sivimss.arquetipo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.imss.sivimss.facturacion.FacturacionApplication;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ComisionesApplicationTests {

	@Test
	void contextLoads() {
		String result = "test";
		FacturacionApplication.main(new String[] {});
		assertNotNull(result);
	}

}
