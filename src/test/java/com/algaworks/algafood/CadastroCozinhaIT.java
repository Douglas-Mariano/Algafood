package com.algaworks.algafood;

import static io.restassured.RestAssured.enableLoggingOfRequestAndResponseIfValidationFails;
import static io.restassured.RestAssured.given;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import io.restassured.http.ContentType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CadastroCozinhaIT {

	@LocalServerPort
	private int port;

	@Test
	void deveRetornarStatus200_QuandoConsultarCozinhas() {
		enableLoggingOfRequestAndResponseIfValidationFails();
		
		given().basePath("/cozinhas").port(port).accept(ContentType.JSON).when().get().then()
				.statusCode(HttpStatus.OK.value());
	}
}
