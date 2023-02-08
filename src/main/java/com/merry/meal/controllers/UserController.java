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

import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.merry.meal.data.Account;
import com.merry.meal.data.User;
import com.merry.meal.payload.ApiResponse;
import com.merry.meal.payload.DeliveryDto;
import com.merry.meal.payload.UserDto;
import com.merry.meal.services.AccountService;
import com.merry.meal.services.DeliveryService;
import com.merry.meal.services.FileService;
import com.merry.meal.services.UserService;
import com.merry.meal.utils.JwtUtils;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
	/*
	 * this controller is for user entity, service
	 */

	@Autowired
	private UserService userService;

	@Autowired
	private FileService fileService;
	
	@Autowired
	private AccountService accountService;

	@Autowired
	private JwtUtils jwtUtils;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private DeliveryService deliveryService;

	// register local user
	@PostMapping("/register")
	public ResponseEntity<UserDto> registerUserLocal(HttpServletRequest request, @Valid @RequestBody UserDto userDto) {

		return ResponseEntity.status(HttpStatus.OK)
				.body(userService.createUserProfile(userDto, jwtUtils.getJWTFromRequest(request)));
	}

	// register local user
	@PutMapping("/update/{userId}")
	public ResponseEntity<UserDto> editUserProfile(
			HttpServletRequest request, @Valid @RequestBody UserDto userDto,
			@PathVariable Long userId) {

		return ResponseEntity.status(HttpStatus.OK)
				.body(userService.editUserProfile(userDto, userId));
	}
	
	// get personal user profile
	@GetMapping("/profile")
	public ResponseEntity<?> getPersonalProfile(HttpServletRequest request){
		
		String token =  jwtUtils.getJWTFromRequest(request);
		Account account = accountService.getAccount(token);
		User user = account.getUser();
		return ResponseEntity.ok(this.modelMapper.map(user, UserDto.class));
	}
	
	//delete user 
	@DeleteMapping("/{uid}")
	public ResponseEntity<?> deleteUser(@PathVariable Long uid){
		accountService.deleteAccount(uid);
		return ResponseEntity.ok("Account has been deleted successfully");
	}

	// uploading user profile image
	@PostMapping("/image/upload-profile-image")
	public ResponseEntity<ApiResponse> uploadFile(HttpServletRequest request, @RequestParam("file") MultipartFile image)
			throws IOException {

		// insuring the request has a file
		if (image.isEmpty()) {
			return new ResponseEntity<ApiResponse>(new ApiResponse("Request must have a file", false),
					HttpStatus.BAD_REQUEST);
		}

		// uploading the file into server
		this.userService.uploadImage(image, jwtUtils.getJWTFromRequest(request));
		return new ResponseEntity<ApiResponse>(new ApiResponse("Profile image uploaded successfully", true),
				HttpStatus.OK);

	}

	// get user info to admin dashboard
	@GetMapping("/")
	public ResponseEntity<?> getUsers() {
		return ResponseEntity.ok(userService.getUserProfiles());
	}
	
	//get Single user info
	@GetMapping("/{userId}")
	public ResponseEntity<?> getUser(@PathVariable Long userId) {
		User user = userService.getUser(userId);
		if(user == null) {
			return ResponseEntity.badRequest().body("User with " + userId + " not found");
		}
		return ResponseEntity.ok(modelMapper.map(user, UserDto.class));
	}

	// method to serve user profile image
	@GetMapping(value = "/image/{imageName}", produces = MediaType.IMAGE_JPEG_VALUE)
	public void downloadImage(@PathVariable String imageName, HttpServletResponse response) throws IOException {
		InputStream resource = this.fileService.getResource(imageName);
		response.setContentType(MediaType.IMAGE_JPEG_VALUE);
		StreamUtils.copy(resource, response.getOutputStream());
	}
	
	/**
	 * Food Management
	 */

	// user order meal
	@PostMapping("/meals/{mealId}")
	public ResponseEntity<DeliveryDto> orderMeal(@Valid @RequestBody DeliveryDto deliveryDto, @PathVariable Long mealId,
			HttpServletRequest request) {

		DeliveryDto orderedMeal = this.deliveryService.orderMeal(deliveryDto, mealId,
				jwtUtils.getJWTFromRequest(request));

		return new ResponseEntity<DeliveryDto>(orderedMeal, HttpStatus.CREATED);
	}

	// order status handling
	@PutMapping("/meals/{deliveryId}")
	public ResponseEntity<DeliveryDto> orderMeal(
			HttpServletRequest request ,@PathVariable Long deliveryId, @RequestParam String status) {

		String token = jwtUtils.getJWTFromRequest(request);
		Account account = accountService.getAccount(token);
		User user = account.getUser();
		DeliveryDto orderedMeal = this.deliveryService.orderMealStatus(user,deliveryId, status);

		return new ResponseEntity<DeliveryDto>(orderedMeal, HttpStatus.OK);
	}

	// user all deliveries
	@GetMapping("/meals")
	public ResponseEntity<List<DeliveryDto>> getOrderedMeals(HttpServletRequest request) {

		List<DeliveryDto> orderedMeal = this.deliveryService.userOrders(jwtUtils.getJWTFromRequest(request));

		if (orderedMeal.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		return ResponseEntity.status(HttpStatus.OK).body(orderedMeal);
	}

	// rider confirm delivery
	@PostMapping("/meals/confirm-orders/{deliveryId}")
	public ResponseEntity<DeliveryDto> riderConfirmOrder(@PathVariable Long deliveryId, HttpServletRequest request) {

		DeliveryDto orderedMeal = this.deliveryService.confirmOrderDelivery(deliveryId,
				jwtUtils.getJWTFromRequest(request));

		return new ResponseEntity<DeliveryDto>(orderedMeal, HttpStatus.CREATED);
	}

	// rider all deliveries
	@GetMapping("/meals/confirm-orders")
	public ResponseEntity<List<DeliveryDto>> getRiderConfirmOrders(HttpServletRequest request) {

		List<DeliveryDto> orderedMeal = this.deliveryService.riderConfirmedOrders(jwtUtils.getJWTFromRequest(request));

		if (orderedMeal.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		return ResponseEntity.status(HttpStatus.OK).body(orderedMeal);
	}

}
