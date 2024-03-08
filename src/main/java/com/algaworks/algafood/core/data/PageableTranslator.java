package com.algaworks.algafood.core.data;

import java.util.Map;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageableTranslator {
	
	private PageableTranslator() {
		throw new IllegalStateException("Classe utilitária");
	}
	
	public static Pageable translate(Pageable pageable, Map<String, String> fieldsMapping) {
		var orders = pageable.getSort().stream()
				.filter(order -> fieldsMapping.containsKey(order.getProperty()))
		.map(order -> new Sort.Order(order.getDirection(), 
				fieldsMapping.get(order.getProperty()))).toList();
		
		return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(orders));
	}

}
