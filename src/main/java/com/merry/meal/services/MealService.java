package com.merry.meal.services;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.merry.meal.data.Meal;
import com.merry.meal.payload.MealDto;
import com.merry.meal.payload.MealResponse;

public interface MealService {

	// add meal
	Meal addNewMeal(MealDto mealDto, String token);

	// upload meal image
	void uploadImage(MultipartFile multipartFile, Long mid) throws IOException;

	// delete meal
	void deleteAddedMeal(Long mid) throws IOException;

	// update meal
	MealDto updateAddedMeal(MealDto mealDto, String token);

	// get meal with pagination
	MealResponse getAllMeals(Long pageNumber, Long pageSize, String sortBy, String sortDir);

	// get all meals
	List<MealDto> getAvailableMeals();

	// get particular meal
	MealDto getMeal(Long mid);
}
