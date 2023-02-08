package com.merry.meal.payload;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class UserRoleDto {

	private Long role_id;

	private String role_name;

}
