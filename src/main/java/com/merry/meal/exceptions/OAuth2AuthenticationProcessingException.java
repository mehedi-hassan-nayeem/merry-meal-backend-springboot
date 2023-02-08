package com.merry.meal.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OAuth2AuthenticationProcessingException extends RuntimeException {
	private String msg;
	public OAuth2AuthenticationProcessingException(String msg) {
		this.msg = msg;
	}
}