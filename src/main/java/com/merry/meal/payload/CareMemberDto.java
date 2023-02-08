package com.merry.meal.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CareMemberDto {
	private Long care_member_id;
	private UserDto caregiver;
	private UserDto user;
}
