package com.algaworks.algafood.api.controller;

import java.io.IOException;

import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.algaworks.algafood.api.assembler.FotoProdutoModelAssembler;
import com.algaworks.algafood.api.model.FotoProdutoModel;
import com.algaworks.algafood.api.model.input.FotoProdutoInput;
import com.algaworks.algafood.domain.model.FotoProduto;
import com.algaworks.algafood.domain.model.Produto;
import com.algaworks.algafood.domain.service.CadastroProdutoService;
import com.algaworks.algafood.domain.service.CatalogoFotoProdutoService;

@RestController
@RequestMapping("/restaurantes/{restauranteId}/produtos/{produtoId}/foto")
public class RestauranteProdutoFotoController {

	private final CatalogoFotoProdutoService catalogoFotoProduto;
	private final CadastroProdutoService cadastroProduto;
	private final FotoProdutoModelAssembler fotoProdutoModelAssembler;

	public RestauranteProdutoFotoController(CatalogoFotoProdutoService catalogoFotoProduto,
			CadastroProdutoService cadastroProduto, FotoProdutoModelAssembler fotoProdutoModelAssembler) {
		this.catalogoFotoProduto = catalogoFotoProduto;
		this.cadastroProduto = cadastroProduto;
		this.fotoProdutoModelAssembler = fotoProdutoModelAssembler;
	}

	@PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public FotoProdutoModel atualizarFoto(@PathVariable Long restauranteId, @PathVariable Long produtoId,
			@Valid FotoProdutoInput fotoProdutoInput) throws IOException {
		Produto produto = cadastroProduto.buscarOuFalhar(restauranteId, produtoId);

		MultipartFile arquivo = fotoProdutoInput.getArquivo();

		FotoProduto foto = new FotoProduto();
		foto.setProduto(produto);
		foto.setDescricao(fotoProdutoInput.getDescricao());
		foto.setContentType(arquivo.getContentType());
		foto.setTamanho(arquivo.getSize());
		foto.setNomeArquivo(arquivo.getOriginalFilename());

		return fotoProdutoModelAssembler.toModel(catalogoFotoProduto.salvar(foto, arquivo.getInputStream()));
	}
}
