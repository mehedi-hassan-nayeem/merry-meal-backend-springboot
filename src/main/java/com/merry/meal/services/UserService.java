package com.merry.meal.services;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.merry.meal.data.User;
import com.merry.meal.payload.UserDto;

public interface UserService {

	// uploading user profile image
	void uploadImage(MultipartFile multipartFile, String token) throws IOException;

	// user create profile
	UserDto createUserProfile(UserDto userDto, String token);
	
	User getUser(Long userId);
	
	List<UserDto> getUserProfiles();
	
	//edit user profile 
	UserDto editUserProfile(UserDto userDto, Long userId);

}
