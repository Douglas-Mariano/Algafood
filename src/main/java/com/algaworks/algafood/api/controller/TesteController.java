package com.algaworks.algafood.api.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.algaworks.algafood.domain.model.Cozinha;
import com.algaworks.algafood.domain.model.Restaurante;
import com.algaworks.algafood.domain.repository.CozinhaRepository;
import com.algaworks.algafood.domain.repository.RestauranteRepository;

@RestController
@RequestMapping("/teste")
public class TesteController {

	@Autowired
	private CozinhaRepository cozinhaRepository;
	
	@Autowired
	private RestauranteRepository restauranteRepository;
	
	@GetMapping("/cozinhas/por-nome")
	public List<Cozinha> cozinhaPorNome(String nome) {
		return cozinhaRepository.findByNomeContaining(nome);
	}
	
	@GetMapping("/cozinhas/por-nome-unico")
	public Optional<Cozinha> cozinhaNome(String nome) {
		return cozinhaRepository.findByNome(nome);
	}
	
	@GetMapping("/restaurantes/taxa")
	public List<Restaurante> restaurantePorTaxaFrete(BigDecimal taxaInicial, BigDecimal taxaFinal) {
		return restauranteRepository.findByTaxaFreteBetween(taxaInicial, taxaFinal);
	}
	
	@GetMapping("/retaurantes/por-nome")
	public List<Restaurante> consultarRestaurantePorNome(String nome, Long cozinhaId) {
		return restauranteRepository.consultarPorNome(nome, cozinhaId);
	}
}