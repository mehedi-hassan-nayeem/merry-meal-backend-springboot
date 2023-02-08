package com.merry.meal.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.merry.meal.data.Meal;

public interface MealRepository extends JpaRepository<Meal, Long> {

}
