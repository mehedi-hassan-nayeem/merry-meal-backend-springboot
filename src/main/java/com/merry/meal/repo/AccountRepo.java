package com.merry.meal.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.merry.meal.data.Account;
import com.merry.meal.data.Session;
import com.merry.meal.data.User;

@Repository
public interface AccountRepo extends JpaRepository<Account, Long>{
	public Optional<Account> findByEmail(String email);
}
