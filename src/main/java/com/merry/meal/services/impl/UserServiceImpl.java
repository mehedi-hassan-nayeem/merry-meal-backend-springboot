package com.merry.meal.services.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.merry.meal.data.Account;
import com.merry.meal.data.User;
import com.merry.meal.exceptions.ResourceNotFoundException;
import com.merry.meal.payload.UserDto;
import com.merry.meal.repo.AccountRepo;
import com.merry.meal.repo.UserRepository;
import com.merry.meal.services.FileService;
import com.merry.meal.services.UserService;
import com.merry.meal.utils.JwtUtils;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AccountRepo accountRepo;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private FileService fileService;

	@Autowired
	private JwtUtils jwtUtils;

	public void uploadImage(MultipartFile multipartFile, String token) throws IOException {

		String email = jwtUtils.getUserNameFromToken(token);
		Account account = accountRepo.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("user", "credentials", email));

		User user = account.getUser();

		// deleting old image
		if (user.getProfile_image() != null) {
			this.fileService.deleteFile(user.getProfile_image());
		}

		// getting new file name
		String uploadedImage = fileService.uploadImage(multipartFile);

		// setting image name
		user.setProfile_image(uploadedImage);

		// updating user
		this.userRepository.save(user);
	}

	///// Changes from Aung ||||||||||||||||| changed accept by - HASSAN

	@Override
	public UserDto createUserProfile(UserDto userDto, String token) {

		String email = jwtUtils.getUserNameFromToken(token);
		Account account = accountRepo.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("user", "credentials", email));

		User user = this.modelMapper.map(userDto, User.class);
		user.setAccount(account);
		user = userRepository.save(user);
		return this.modelMapper.map(user, UserDto.class);
	}

	@Override
	public User getUser(Long userId) {
		Optional<User> user = userRepository.findById(userId);
		if(!user.isPresent()) {
			return null;
		}
			return user.get();
	}

	@Override
	public List<UserDto> getUserProfiles() {
		
		List<User> users = userRepository.findAll();
		List<UserDto> userDto = new ArrayList<UserDto>();
		users.stream().forEach((user) -> {
			userDto.add(this.modelMapper.map(user, UserDto.class));
		});
		return userDto;
	}

	@Override
	public UserDto editUserProfile(UserDto userDto, Long userId) {
		Optional<User> user = userRepository.findById(userId);
		if(user.isEmpty()) {
			return null;
		}
		User editUser =this.modelMapper.map(userDto, User.class);
		User realUser = user.get();
		editUser.setAccount(realUser.getAccount());
		editUser.setUser_id(realUser.getUser_id());
		editUser = userRepository.save(editUser);
		userDto = this.modelMapper.map(editUser, UserDto.class);
		return userDto;
	}

}
