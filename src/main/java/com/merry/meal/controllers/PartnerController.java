package com.merry.meal.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.merry.meal.config.AppConstants;
import com.merry.meal.data.Meal;
import com.merry.meal.payload.ApiResponse;
import com.merry.meal.payload.DeliveryDto;
import com.merry.meal.payload.MealDto;
import com.merry.meal.payload.MealResponse;
import com.merry.meal.services.DeliveryService;
import com.merry.meal.services.FileService;
import com.merry.meal.services.MealService;
import com.merry.meal.utils.JwtUtils;

@RestController
@RequestMapping("/api/v1/partners")
public class PartnerController {

	/*
	 * this controller is responsible for meal entity, service
	 */

	@Autowired
	private MealService mealService;

	@Autowired
	private FileService fileService;

	@Autowired
	private JwtUtils jwtUtils;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private DeliveryService deliveryService;

	// add new meal
	@PostMapping("/meals")
	public ResponseEntity<MealDto> addNewMeal(@Valid @RequestBody MealDto mealDto, HttpServletRequest request) {
		Meal meal = this.mealService.addNewMeal(mealDto, jwtUtils.getJWTFromRequest(request));
		MealDto newMealDto = this.modelMapper.map(meal, MealDto.class);
		return new ResponseEntity<MealDto>(newMealDto, HttpStatus.CREATED);
	}

	// update added meal
	@PutMapping("/meals")
	public ResponseEntity<ApiResponse> updateAddedMeal(@Valid @RequestBody MealDto mealDto,
			HttpServletRequest request) {
		System.out.println(mealDto.getStatus());
		this.mealService.updateAddedMeal(mealDto, jwtUtils.getJWTFromRequest(request));
		return new ResponseEntity<ApiResponse>(new ApiResponse("Meal updated successfully", true), HttpStatus.CREATED);
	}

	// delete added meal
	@DeleteMapping("/meals/{mid}")
	public ResponseEntity<ApiResponse> deleteAccount(@PathVariable Long mid) throws IOException {
		this.mealService.deleteAddedMeal(mid);
		return new ResponseEntity<ApiResponse>(new ApiResponse("Meal deleted successfully", true), HttpStatus.OK);
	}

	// get particular meal
	@GetMapping("/meals/{mid}")
	public ResponseEntity<MealDto> getAccountInfo(@PathVariable Long mid) {
		return ResponseEntity.status(HttpStatus.OK).body(this.mealService.getMeal(mid));
	}

	// get all meals
	@GetMapping("/meals")
	public ResponseEntity<List<MealDto>> allMeals() {

		List<MealDto> availableMeals = this.mealService.getAvailableMeals();

		if (availableMeals.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		return ResponseEntity.status(HttpStatus.OK).body(availableMeals);
	}

	// get meals with pagination
	@GetMapping("/all-meals")
	public ResponseEntity<MealResponse> getAllPost(
			@RequestParam(value = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Long pageNumber,
			@RequestParam(value = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Long pageSize,
			@RequestParam(value = "sortBy", defaultValue = AppConstants.SORT_BY, required = false) String sortBy,
			@RequestParam(value = "sortDir", defaultValue = AppConstants.SORT_DIR, required = false) String sortDir) {

		MealResponse mealResponse = this.mealService.getAllMeals(pageNumber, pageSize, sortBy, sortDir);

		return new ResponseEntity<MealResponse>(mealResponse, HttpStatus.OK);
	}

	// uploading meal image
	@PostMapping("/meals/{mid}/upload-meal-image")
	public ResponseEntity<ApiResponse> uploadFile(@PathVariable("mid") Long mid,
			@RequestParam("file") MultipartFile image) throws IOException {

		// insuring the request has a file
		if (image.isEmpty()) {
			return new ResponseEntity<ApiResponse>(new ApiResponse("Request must have a file", false),
					HttpStatus.BAD_REQUEST);
		}

		// uploading the file into server
		this.mealService.uploadImage(image, mid);
		return new ResponseEntity<ApiResponse>(new ApiResponse("Meal image uploaded successfully", true),
				HttpStatus.OK);
	}
	


	// method to serve meal image
	@GetMapping(value = "/meals/image/{imageName}", produces = MediaType.IMAGE_JPEG_VALUE)
	public void downloadImage(@PathVariable String imageName, HttpServletResponse response) throws IOException {
		InputStream resource = this.fileService.getResource(imageName);
		response.setContentType(MediaType.IMAGE_JPEG_VALUE);
		StreamUtils.copy(resource, response.getOutputStream());
	}
	
	/**
	 * Food Management
	 */

	// all deliveries
	@GetMapping("/orders")
	public ResponseEntity<List<DeliveryDto>> getAllOrderedMeals() {

		List<DeliveryDto> orderedMeal = this.deliveryService.allOrders();

		if (orderedMeal.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		return ResponseEntity.status(HttpStatus.OK).body(orderedMeal);
	}

	// particular delivery
	@GetMapping("/orders/{deliveryId}")
	public ResponseEntity<DeliveryDto> getParticularOrderedMeal(@PathVariable Long deliveryId) {

		DeliveryDto orderedMeal = this.deliveryService.order(deliveryId);

		return ResponseEntity.status(HttpStatus.OK).body(orderedMeal);
	}
	
	@GetMapping(value="/meals/pending-delivery")
	public ResponseEntity<List<DeliveryDto>> getPendingDelivery(){
		return ResponseEntity.ok(this.deliveryService.pendingDelivery());
	}

}
