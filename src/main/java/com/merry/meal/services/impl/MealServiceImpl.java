package com.merry.meal.services.impl;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.merry.meal.data.Account;
import com.merry.meal.data.Delivery;
import com.merry.meal.data.Meal;
import com.merry.meal.exceptions.ResourceNotFoundException;
import com.merry.meal.payload.MealDto;
import com.merry.meal.payload.MealResponse;
import com.merry.meal.repo.AccountRepo;
import com.merry.meal.repo.DeliveryRepository;
import com.merry.meal.repo.MealRepository;
import com.merry.meal.repo.RiderDeliveryRepo;
import com.merry.meal.services.FileService;
import com.merry.meal.services.MealService;
import com.merry.meal.utils.JwtUtils;

@Service
public class MealServiceImpl implements MealService {

	@Autowired
	private AccountRepo accountRepo;

	@Autowired
	private MealRepository mealRepository;

	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private DeliveryRepository deliveryRepository;
	
	@Autowired
	private RiderDeliveryRepo riderDeliveryRepo;

	@Autowired
	private FileService fileService;

	@Autowired
	private JwtUtils jwtUtils;

	@Override
	public Meal addNewMeal(MealDto mealDto, String token) {

		String email = jwtUtils.getUserNameFromToken(token);
		Account account = accountRepo.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("user", "credentials", email));

		Meal meal = this.modelMapper.map(mealDto, Meal.class);
		meal.setUser(account.getUser());
		meal.setStatus("Pending");

		return this.mealRepository.save(meal);

	}

	@Override
	public void deleteAddedMeal(Long mid) throws IOException {
		
		Meal meal = this.mealRepository.findById(mid)
				.orElseThrow(() -> new ResourceNotFoundException("meal", "meal id", mid.toString()));

		String image = meal.getImage();
		List<Delivery> delivery = meal.getDelivery();
		if (image != null) {
			this.fileService.deleteFile(meal.getImage());
		}
		if(delivery != null) {
			delivery.stream().forEach((deli) -> {
				if(deli.getRideDelivery() != null) {
					this.riderDeliveryRepo.delete(deli.getRideDelivery());
				}
				
				this.deliveryRepository.delete(deli);
			});
		}
		this.mealRepository.delete(meal);
	}

	@Override
	public MealDto updateAddedMeal(MealDto mealDto, String token) {

		String email = jwtUtils.getUserNameFromToken(token);
		Account account = accountRepo.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("user", "credentials", email));

		this.mealRepository.findById(mealDto.getMealId())
				.orElseThrow(() -> new ResourceNotFoundException("meal", "meal id", mealDto.getMealId().toString()));

		Meal updatedMeal = this.modelMapper.map(mealDto, Meal.class);
		updatedMeal.setUser(account.getUser());

		this.mealRepository.save(updatedMeal);

		return this.modelMapper.map(updatedMeal, MealDto.class);
	}

	@Override
	public MealResponse getAllMeals(Long pageNumber, Long pageSize, String sortBy, String sortDir) {

		Sort sort = (sortDir.equalsIgnoreCase("asc")) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

		Pageable p = PageRequest.of(pageNumber.intValue(), pageSize.intValue(), sort);

		Page<Meal> pageMeal = this.mealRepository.findAll(p);

		List<Meal> meals = pageMeal.getContent();

		List<MealDto> allMealsDto = meals.stream().map(meal -> this.modelMapper.map(meal, MealDto.class))
				.collect(Collectors.toList());

		MealResponse mealResponse = new MealResponse();
		mealResponse.setContent(allMealsDto);
		mealResponse.setPageNumber(pageMeal.getNumber());
		mealResponse.setPageSize(pageMeal.getSize());
		mealResponse.setTotalElements(pageMeal.getTotalElements());
		mealResponse.setTotalPages(pageMeal.getTotalPages());
		mealResponse.setLastPage(pageMeal.isLast());

		return mealResponse;
	}

	@Override
	public List<MealDto> getAvailableMeals() {
		List<Meal> allMeals = this.mealRepository.findAll();

		List<MealDto> allMealsDto = allMeals.stream().map(meal -> this.modelMapper.map(meal, MealDto.class))
				.collect(Collectors.toList());

		return allMealsDto;
	}

	@Override
	public MealDto getMeal(Long mid) {

		Meal meal = this.mealRepository.findById(mid)
				.orElseThrow(() -> new ResourceNotFoundException("meal", "meal id", mid.toString()));

		return this.modelMapper.map(meal, MealDto.class);
	}

	@Override
	public void uploadImage(MultipartFile multipartFile, Long mid) throws IOException {

		Meal meal = this.mealRepository.findById(mid)
				.orElseThrow(() -> new ResourceNotFoundException("meal", "meal id", mid.toString()));

		// deleting old image
		if (meal.getImage() != null) {
			this.fileService.deleteFile(meal.getImage());
		}

		// getting new file name
		String uploadedImage = fileService.uploadImage(multipartFile);

		// setting image name
		meal.setImage(uploadedImage);

		// updating user
		this.mealRepository.save(meal);

	}

}
