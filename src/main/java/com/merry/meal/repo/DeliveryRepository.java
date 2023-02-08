package com.merry.meal.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.merry.meal.data.Delivery;
import com.merry.meal.data.User;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

	List<Delivery> findByUser(User user);
	
	List<Delivery> findByStatus(String delivery_status);

}
