package com.merry.meal.services.impl;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.merry.meal.data.Account;
import com.merry.meal.data.CareMember;
import com.merry.meal.data.Meal;
import com.merry.meal.data.Session;
import com.merry.meal.data.User;
import com.merry.meal.data.UserRole;
import com.merry.meal.exceptions.ResourceNotFoundException;
import com.merry.meal.payload.AccountDto;
import com.merry.meal.repo.AccountRepo;
import com.merry.meal.repo.CareMemberRepository;
import com.merry.meal.repo.SessionRepository;
import com.merry.meal.repo.UserRepository;
import com.merry.meal.repo.UserRoleRepository;
import com.merry.meal.services.AccountService;
import com.merry.meal.services.MealService;
import com.merry.meal.utils.JwtUtils;

@Service
public class AccountServiceImpl implements AccountService {

	@Autowired
	private AccountRepo accountRepo;

	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private UserRepository userRepo;

	@Autowired
	private JwtUtils jwtUtils;
	
	@Autowired
	private MealService mealService;
	
	@Autowired
	private UserRoleRepository userRoleRepository;
	
	@Autowired
	private CareMemberRepository careMemberRepository;
	
	@Autowired
	private SessionRepository sessionRepository;

	@Override
	public List<AccountDto> getAllAccountDto() {

		List<Account> allAccounts = this.accountRepo.findAll();

		List<AccountDto> allAccountDtos = allAccounts.stream()
				.map((account) -> this.modelMapper.map(account, AccountDto.class)).collect(Collectors.toList());

		return allAccountDtos;
	}

	@Override
	public AccountDto getAccountDto(String token) {

		String email = jwtUtils.getUserNameFromToken(token);
		Account account = accountRepo.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("account", "credentials", email));

		return this.modelMapper.map(account, AccountDto.class);
	}
	
	@Override
	public Account getAccount(String token) {
		String email = jwtUtils.getUserNameFromToken(token);
		return accountRepo.findByEmail(email).get();
	}
	
	@Override
	public void deleteAccount(Long userId) {
		User user = userRepo.findById(userId).get();
		List<Meal> meals = user.getMeals();
		List<Session> sessions = user.getSession();
		if(sessions != null) {
			sessions.forEach((session)-> {
				sessionRepository.delete(session);
			});
		}
		if(meals != null) {
			meals.stream().forEach((meal)-> {
				try {
					mealService.deleteAddedMeal(meal.getMealId());
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		}
		List<CareMember> requestCares = user.getRequestedCares();
		if(requestCares != null) {
			requestCares.stream().forEach((cares) -> {
				careMemberRepository.delete(cares);
			});
			List<CareMember> reacceptedCares = user.getAcceptedCares();
			if(reacceptedCares != null) {
				reacceptedCares.stream().forEach((cares) -> {
					careMemberRepository.delete(cares);
				});
			}
		}
		
		List<CareMember> acceptedCares = user.getAcceptedCares();
		if(acceptedCares != null) {
			acceptedCares.stream().forEach((cares) -> {
				careMemberRepository.delete(cares);
			});
		}
		Account account = user.getAccount();
		List<UserRole> userRoles = account.getUserRoles();
		if(userRoles != null) {
			userRoles.stream().forEach((role) -> {
				userRoleRepository.delete(role);
			});
		}
		userRepo.delete(user);
		accountRepo.delete(account);
		return;
	}

}
