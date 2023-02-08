package com.merry.meal.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.merry.meal.data.Account;
import com.merry.meal.data.Delivery;
import com.merry.meal.data.Meal;
import com.merry.meal.data.RideDelivery;
import com.merry.meal.data.User;
import com.merry.meal.exceptions.ResourceNotFoundException;
import com.merry.meal.payload.DeliveryDto;
import com.merry.meal.repo.AccountRepo;
import com.merry.meal.repo.DeliveryRepository;
import com.merry.meal.repo.MealRepository;
import com.merry.meal.repo.RiderDeliveryRepo;
import com.merry.meal.services.DeliveryService;
import com.merry.meal.utils.JwtUtils;

@Service
public class DeliveryServiceImpl implements DeliveryService {

	@Autowired
	private MealRepository mealRepository;

	@Autowired
	private DeliveryRepository deliveryRepository;
	
	@Autowired
	private RiderDeliveryRepo riderDeliveryRepo;

	@Autowired
	private AccountRepo accountRepo;

	@Autowired
	private JwtUtils jwtUtils;
	
	

	@Autowired
	private ModelMapper modelMapper;
	


	@Override
	public DeliveryDto orderMeal(DeliveryDto deliveryDto, Long mid, String token) {

		Meal meal = this.mealRepository.findById(mid)
				.orElseThrow(() -> new ResourceNotFoundException("meal", "meal id", mid.toString()));

		String email = jwtUtils.getUserNameFromToken(token);
		Account account = accountRepo.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("user", "credentials", email));

		Delivery delivery = this.modelMapper.map(deliveryDto, Delivery.class);

		delivery.setDelivery_number((int) (Math.floor(Math.random() * (99999999 - 00000000)) + 00000000));
		delivery.setStatus("Pending");

		delivery.setUser(account.getUser());
		delivery.setMeal(meal);

		Delivery newOrder = this.deliveryRepository.save(delivery);

		return this.modelMapper.map(newOrder, DeliveryDto.class);
	}

	@Override
	public DeliveryDto orderMealStatus(User user, Long deliveryId, String status) {
		Delivery delivery = this.deliveryRepository.findById(deliveryId)
				.orElseThrow(() -> new ResourceNotFoundException("delivery", "delivery id", deliveryId.toString()));

		delivery.setStatus(status);
		System.out.println(status);
		System.out.println(status.equalsIgnoreCase("Order") + "/////////////////////////////////////////");
		if(status.equalsIgnoreCase("Ordered")) {
			System.out.println(status);
			RideDelivery rideDelivery = new RideDelivery();
			rideDelivery.setRider(user);
			rideDelivery.setDelivery(delivery);
			this.riderDeliveryRepo.save(rideDelivery);
		}

		Delivery changedStatus = this.deliveryRepository.save(delivery);

		return this.modelMapper.map(changedStatus, DeliveryDto.class);
	}

	@Override
	public List<DeliveryDto> userOrders(String token) {
		String email = jwtUtils.getUserNameFromToken(token);
		Account account = accountRepo.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("user", "credentials", email));

		List<Delivery> userOrders = this.deliveryRepository.findByUser(account.getUser());

		List<DeliveryDto> ordersDtos = userOrders.stream().map(order -> this.modelMapper.map(order, DeliveryDto.class))
				.collect(Collectors.toList());

		return ordersDtos;
	}

	@Override
	public List<DeliveryDto> allOrders() {
		List<Delivery> userOrders = this.deliveryRepository.findAll();

		List<DeliveryDto> ordersDtos = userOrders.stream().map(order -> this.modelMapper.map(order, DeliveryDto.class))
				.collect(Collectors.toList());
		return ordersDtos;
	}

	@Override
	public DeliveryDto order(Long deliveryId) {
		Delivery delivery = this.deliveryRepository.findById(deliveryId)
				.orElseThrow(() -> new ResourceNotFoundException("delivery", "delivery id", deliveryId.toString()));

		return this.modelMapper.map(delivery, DeliveryDto.class);
	}

	@Override
	public DeliveryDto confirmOrderDelivery(Long deliveryId, String token) {

		Delivery delivery = this.deliveryRepository.findById(deliveryId)
				.orElseThrow(() -> new ResourceNotFoundException("delivery", "delivery id", deliveryId.toString()));

		String email = jwtUtils.getUserNameFromToken(token);
		Account account = accountRepo.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("user", "credentials", email));

		//repository
		RideDelivery riderDelivery = new RideDelivery();
		riderDelivery.setRider(account.getUser());
		riderDelivery.setDelivery(delivery);
		riderDelivery = riderDeliveryRepo.save(riderDelivery);
		return this.modelMapper.map(delivery, DeliveryDto.class);
	}

	@Override
	public List<DeliveryDto> riderConfirmedOrders(String token) {

		String email = jwtUtils.getUserNameFromToken(token);
		Account account = accountRepo.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("user", "credentials", email));

		// repository call
		User rider = account.getUser();
		List<DeliveryDto> deliveryDto = new ArrayList<DeliveryDto>();
		rider.getRideDeliveries().stream().forEach((delivery) -> {
			deliveryDto.add(this.modelMapper.map(delivery.getDelivery(), DeliveryDto.class));
		});
		return deliveryDto;
	}

	@Override
	public List<DeliveryDto> pendingDelivery() {
		List<DeliveryDto> deliveryDtos = this.deliveryRepository.findByStatus("PENDING")
				.stream().map((delivery) -> this.modelMapper.map(delivery, DeliveryDto.class)).collect(Collectors.toList());
		return deliveryDtos;
	}
	
	@Override
	public void deleteDelivery(Long deliveryId) {
		Delivery delivery = deliveryRepository.findById(deliveryId).get();
		this.riderDeliveryRepo.deleteByDelivery(delivery);
		this.deliveryRepository.delete(delivery);
		return;
	}

}
