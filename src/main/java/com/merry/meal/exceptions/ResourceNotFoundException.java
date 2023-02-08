package com.merry.meal.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResourceNotFoundException extends RuntimeException {

	String resourceName;
	String fieldName;
	String fieldValue;

	public ResourceNotFoundException(String resourceName, String fieldName, String fieldValue) {
		super(String.format("Unable to find | %s | with provided | %s | : %s", resourceName, fieldName, fieldValue));
		this.resourceName = resourceName;
		this.fieldName = fieldName;
		this.fieldValue = fieldValue;
	}

}
