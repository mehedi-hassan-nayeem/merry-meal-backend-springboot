package com.merry.meal.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.merry.meal.data.Delivery;
import com.merry.meal.data.RideDelivery;

public interface RiderDeliveryRepo extends JpaRepository<RideDelivery, Long>{
	public void deleteByDelivery(Delivery delivery);
}
