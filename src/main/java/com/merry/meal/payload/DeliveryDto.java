package com.merry.meal.payload;

import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryDto {

	private Long delivery_id;

	private Integer delivery_number;

	private String status;

	@NotEmpty(message = "Delivery address can't not be empty")
	private String delivery_address;

	private UserDto user;

	private MealDto meal;
	
	private RiderDeliveryDto riderDelivery;
}
