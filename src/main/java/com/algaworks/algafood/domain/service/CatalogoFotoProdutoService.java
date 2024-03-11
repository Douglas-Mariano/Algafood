package com.algaworks.algafood.domain.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.algaworks.algafood.domain.model.FotoProduto;
import com.algaworks.algafood.domain.repository.ProdutoRepository;

@Service
public class CatalogoFotoProdutoService {

	private final ProdutoRepository produtoRepository;
	
	public CatalogoFotoProdutoService(ProdutoRepository produtoRepository) {
		this.produtoRepository = produtoRepository;
	}
	
	@Transactional
	public FotoProduto salvar(FotoProduto foto) {
		return produtoRepository.save(foto);
	}
}
