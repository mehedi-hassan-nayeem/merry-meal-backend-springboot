package com.merry.meal.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.merry.meal.data.Account;
import com.merry.meal.repo.AccountRepo;

@Service
public class CustomUserService implements UserDetailsService{

	@Autowired
	private AccountRepo accountRepo;
	
	@Transactional
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Account account = accountRepo.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with email : " + email));
		return UserPrincipal.create(account);
	}
}