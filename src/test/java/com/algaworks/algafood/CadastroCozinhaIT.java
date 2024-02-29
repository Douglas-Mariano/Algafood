package com.algaworks.algafood;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;

import com.algaworks.algafood.domain.model.Cozinha;
import com.algaworks.algafood.domain.repository.CozinhaRepository;
import com.algaworks.algafood.util.DatabaseCleaner;
import com.algaworks.algafood.util.ResourceUtils;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("/application-test.properties")
class CadastroCozinhaIT {
	
	@LocalServerPort
	private int port;
	
	@Autowired
	private DatabaseCleaner databaseCleaner;
	
	@Autowired
	private CozinhaRepository cozinhaRepository;

	private Cozinha cozinhaAmericana;
	private int quantidadeCozinhasCadastradas;
	private String jsonCorretoCozinhaChinesa;

	@BeforeEach
	void setUp() {
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
		RestAssured.port = port;
		RestAssured.basePath = "/cozinhas";
		jsonCorretoCozinhaChinesa = ResourceUtils.getContentFromResource(
				"/json/correto/cozinha-chinesa.json");
		
		databaseCleaner.clearTables();
		prepararDados();

	}

	@Test
	void deveRetornarStatus200_QuandoConsultarCozinhas() {
		given().accept(ContentType.JSON)
		.when().get()
		.then().statusCode(HttpStatus.OK.value());
	}

	@Test
	void deveRetornarQuantidadeCorretaDeCozinhas_QuandoConsultarCozinhas() {
		given().accept(ContentType.JSON)
		.when().get()
		.then().body("", Matchers.hasSize(quantidadeCozinhasCadastradas));
	}
	
	@Test
	void deveRetornarStatus201_QuandoCadastrarCozinha() {
		given().body(jsonCorretoCozinhaChinesa).contentType(ContentType.JSON).accept(ContentType.JSON)
		.when().post()
		.then().statusCode(HttpStatus.CREATED.value());
	}
	
	@Test
	void deveRetornarRespostaEStatusCorretos_QuandoConsultarCozinhaExistente() {
		given().pathParam("cozinhaId", cozinhaAmericana.getId()).accept(ContentType.JSON)
		.when().get("/{cozinhaId}")
		.then().statusCode(HttpStatus.OK.value()).body("nome", equalTo(cozinhaAmericana.getNome()));
	}
	
	@Test
	void deveRetornarStatus404_QuandoConsultarCozinhaInexistente() {
		given().pathParam("cozinhaId", quantidadeCozinhasCadastradas + 1).accept(ContentType.JSON)
		.when().get("/{cozinhaId}")
		.then().statusCode(HttpStatus.NOT_FOUND.value());
	}
	
	private void prepararDados() {
	    Cozinha cozinhaTailandesa = new Cozinha();
	    cozinhaTailandesa.setNome("Tailandesa");
	    cozinhaRepository.save(cozinhaTailandesa);

	    cozinhaAmericana = new Cozinha();
	    cozinhaAmericana.setNome("Americana");
	    cozinhaRepository.save(cozinhaAmericana);
	    
	    quantidadeCozinhasCadastradas = (int) cozinhaRepository.count();
	}
}
