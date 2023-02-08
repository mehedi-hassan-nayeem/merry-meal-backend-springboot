package com.merry.meal.payload;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FunDto {
	private int fund_id;
	private int amount;

	private String status;
	@JsonIgnore
	private UserDto user;

}
