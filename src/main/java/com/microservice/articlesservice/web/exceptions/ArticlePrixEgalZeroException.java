package com.microservice.articlesservice.web.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ArticlePrixEgalZeroException extends RuntimeException {
	
	public ArticlePrixEgalZeroException(String s) {
		super(s);
	}

}
