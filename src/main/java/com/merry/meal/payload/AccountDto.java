package com.merry.meal.payload;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AccountDto {

	private Long account_id;

	private String email;

	private String password;

	private String provider;

	private List<UserRoleDto> userRoles;

	private UserDto user;
}
