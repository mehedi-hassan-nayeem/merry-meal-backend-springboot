package com.merry.meal.services;

import java.util.List;

import com.merry.meal.data.User;
import com.merry.meal.payload.DeliveryDto;

public interface DeliveryService {

	// order a meal
	DeliveryDto orderMeal(DeliveryDto deliveryDto, Long mid, String token);

	// order status handling
	DeliveryDto orderMealStatus(User user,Long deliveryId, String status);

	// get user all orders
	List<DeliveryDto> userOrders(String token);

	// get all orders
	List<DeliveryDto> allOrders();

	// order details
	DeliveryDto order(Long deliveryId);

	// rider confirm order
	DeliveryDto confirmOrderDelivery(Long deliveryId, String token);

	// get rider all confirmed orders
	List<DeliveryDto> riderConfirmedOrders(String token);
	
	//get pending orders
	List<DeliveryDto> pendingDelivery();

	void deleteDelivery(Long delivery);

}
