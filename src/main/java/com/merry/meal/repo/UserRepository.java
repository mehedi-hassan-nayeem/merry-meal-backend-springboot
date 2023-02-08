package com.merry.meal.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.merry.meal.data.User;

public interface UserRepository extends JpaRepository<User, Long>{

}
