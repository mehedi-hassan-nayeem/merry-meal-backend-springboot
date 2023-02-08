package com.merry.meal.payload;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.merry.meal.data.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor  
public class SessionDto {
	private Long session_id;
	private String session;
	private String date;
	private String status;
	private UserDto user;
	
}
