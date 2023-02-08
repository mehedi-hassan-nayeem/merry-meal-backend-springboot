package com.merry.meal.payload;

import javax.validation.constraints.NotEmpty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class MealDto {

	private Long mealId;

//	@NotEmpty(message = "Meal Image can not be empty")
	private String image;

	private String status;

	@NotEmpty(message = "Meal name can not be empty")
	private String meal_name;

	@NotEmpty(message = "Meal description can not be empty")
	private String meal_desc;

	@NotEmpty(message = "Meal category can not be empty")
	private String category;

	private UserDto user;

}
