package com.algaworks.algafood;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;

import com.algaworks.algafood.domain.model.Cozinha;
import com.algaworks.algafood.domain.model.Restaurante;
import com.algaworks.algafood.domain.repository.CozinhaRepository;
import com.algaworks.algafood.domain.repository.RestauranteRepository;
import com.algaworks.algafood.util.DatabaseCleaner;
import com.algaworks.algafood.util.ResourceUtils;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("/application-test.properties")
class CadastroRestauranteIT {

	private static final String VIOLACAO_DE_REGRA_DE_NEGOCIO_PROBLEM_TYPE = "Violação de regra de negócio";

	private static final String DADOS_INVALIDOS_PROBLEM_TITLE = "Dados inválidos";

	@LocalServerPort
	private int port;

	@Autowired
	private DatabaseCleaner databaseCleaner;

	@Autowired
	private RestauranteRepository restauranteRepository;

	@Autowired
	private CozinhaRepository cozinhaRepository;

	private Restaurante restauranteMineiro;
	private Restaurante burgerTopRestaurante;

	private String jsonRestauranteCorreto;
	private String jsonRestauranteSemFrete;
	private String jsonRestauranteSemCozinha;
	private String jsonRestauranteComCozinhaInexistente;
	private int quantidadeRestaurantesCadastrados;

	@BeforeEach
	void setUp() {
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
		RestAssured.port = port;
		RestAssured.basePath = "/restaurantes";

		jsonRestauranteCorreto = ResourceUtils
				.getContentFromResource("/json/correto/restaurante-new-york-barbecue.json");

		jsonRestauranteSemFrete = ResourceUtils
				.getContentFromResource("/json/incorreto/restaurante-new-york-barbecue-sem-frete.json");

		jsonRestauranteSemCozinha = ResourceUtils
				.getContentFromResource("/json/incorreto/restaurante-new-york-barbecue-sem-cozinha.json");

		jsonRestauranteComCozinhaInexistente = ResourceUtils
				.getContentFromResource("/json/incorreto/restaurante-new-york-barbecue-com-cozinha-inexistente.json");

		databaseCleaner.clearTables();
		prepararDados();
	}

	@Test
	void deveRetornarRespostaEStatusCorretos_QuandoConsultarRestauranteExistente() {
		given().pathParam("restauranteId", restauranteMineiro.getId()).accept(ContentType.JSON).when()
				.get("/{restauranteId}").then().statusCode(HttpStatus.OK.value())
				.body("nome", equalTo(restauranteMineiro.getNome()));
	}

	@Test
	void deveRetornarStatusCorretos_QuandoExcluirRestauranteExistente() {
		given().pathParam("restauranteId", restauranteMineiro.getId()).accept(ContentType.JSON).when()
				.delete("/{restauranteId}").then().statusCode(HttpStatus.NO_CONTENT.value());
	}

	@Test
	void deveRetornarStatus404_QuandoConsultarRestauranteInexistente() {
		given().pathParam("restauranteId", quantidadeRestaurantesCadastrados + 1).accept(ContentType.JSON).when()
				.get("/{restauranteId}").then().statusCode(HttpStatus.NOT_FOUND.value());
	}

	@Test
	void deveRetornarStatus201_QuandoCadastrarRestaurante() {
		given().body(jsonRestauranteCorreto).contentType(ContentType.JSON).accept(ContentType.JSON).when().post().then()
				.statusCode(HttpStatus.CREATED.value());
	}

	@Test
	void deveRetornarStatus400_QuandoCadastrarRestauranteSemTaxaFrete() {
		given().body(jsonRestauranteSemFrete).contentType(ContentType.JSON).accept(ContentType.JSON).when().post()
				.then().statusCode(HttpStatus.BAD_REQUEST.value())
				.body("title", equalTo(DADOS_INVALIDOS_PROBLEM_TITLE));
	}

	@Test
	void deveRetornarStatus400_QuandoCadastrarRestauranteSemCozinha() {
		given().body(jsonRestauranteSemCozinha).contentType(ContentType.JSON).accept(ContentType.JSON).when().post()
				.then().statusCode(HttpStatus.BAD_REQUEST.value())
				.body("title", equalTo(DADOS_INVALIDOS_PROBLEM_TITLE));
	}

	@Test
	void deveRetornarStatus400_QuandoCadastrarRestauranteComCozinhaInexistente() {
		given().body(jsonRestauranteComCozinhaInexistente).contentType(ContentType.JSON).accept(ContentType.JSON).when()
				.post().then().statusCode(HttpStatus.BAD_REQUEST.value())
				.body("title", equalTo(VIOLACAO_DE_REGRA_DE_NEGOCIO_PROBLEM_TYPE));
	}

	private void prepararDados() {
		Cozinha cozinhaBrasileira = new Cozinha();
		cozinhaBrasileira.setNome("Brasileira");
		cozinhaRepository.save(cozinhaBrasileira);

		Cozinha cozinhaAmericana = new Cozinha();
		cozinhaAmericana.setNome("Americana");
		cozinhaRepository.save(cozinhaAmericana);

		Restaurante restauranteCarioca = new Restaurante();
		restauranteCarioca.setNome("Comida Carioca - frete grátis");
		restauranteCarioca.setTaxaFrete(new BigDecimal("0"));
		restauranteCarioca.setCozinha(cozinhaBrasileira);
		restauranteRepository.save(restauranteCarioca);

		restauranteMineiro = new Restaurante();
		restauranteMineiro.setNome("Comida mineiro");
		restauranteMineiro.setTaxaFrete(new BigDecimal("1"));
		restauranteMineiro.setCozinha(cozinhaBrasileira);
		restauranteRepository.save(restauranteMineiro);

		burgerTopRestaurante = new Restaurante();
		burgerTopRestaurante.setNome("Burger Top");
		burgerTopRestaurante.setTaxaFrete(new BigDecimal(10));
		burgerTopRestaurante.setCozinha(cozinhaAmericana);
		restauranteRepository.save(burgerTopRestaurante);

		quantidadeRestaurantesCadastrados = (int) restauranteRepository.count();
	}
}
