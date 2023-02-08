package com.merry.meal.services;

import java.util.List;

import com.merry.meal.data.Account;
import com.merry.meal.payload.AccountDto;

public interface AccountService {

	// get all accounts
	List<AccountDto> getAllAccountDto();

	// get particular account info
	AccountDto getAccountDto(String token);

	Account getAccount(String token);
	
	void deleteAccount(Long userId);
}
