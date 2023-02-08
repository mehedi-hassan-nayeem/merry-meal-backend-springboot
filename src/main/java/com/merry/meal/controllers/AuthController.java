package com.merry.meal.controllers;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.merry.meal.config.AppConstants;
import com.merry.meal.data.Account;
import com.merry.meal.data.Role;
import com.merry.meal.data.User;
import com.merry.meal.data.UserRole;
import com.merry.meal.exceptions.BadRequestException;
import com.merry.meal.payload.AuthResponse;
import com.merry.meal.payload.LoginRequest;
import com.merry.meal.payload.SignUpRequest;
import com.merry.meal.repo.AccountRepo;
import com.merry.meal.repo.RoleRepository;
import com.merry.meal.repo.UserRoleRepository;
import com.merry.meal.status.Provider;
import com.merry.meal.utils.JwtUtils;

@RestController
@RequestMapping("/auth")
public class AuthController {
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private AccountRepo accountRepo;
	
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private UserRoleRepository userRoleRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private JwtUtils jwtUtils;

	@PostMapping("/login")
	public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String token = jwtUtils.createToken(authentication);
		return ResponseEntity.ok(new AuthResponse(token));
	}

	@PostMapping("/signUp")
	public ResponseEntity<?> registerUser(@RequestBody SignUpRequest signUpRequest) {
		if (accountRepo.findByEmail(signUpRequest.getEmail()).isPresent()) {
			throw new BadRequestException("Email address already in use.");
		}
		Account account = new Account();
		account.setEmail(signUpRequest.getEmail());
		account.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
		account.setProvider(Provider.local.name());
		account = accountRepo.save(account);
		UserRole userRole = new UserRole();
		Role role = roleRepository.findById(AppConstants.ROLE_MEMBER.longValue()).get();
		userRole.setAccount(account);
		userRole.setRole(role);
		userRoleRepository.save(userRole);
		Authentication authentication = authenticationManager
				.authenticate
				(new UsernamePasswordAuthenticationToken(signUpRequest.getEmail(), 
						signUpRequest.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String token = jwtUtils.createToken(authentication);
		return ResponseEntity.ok(new AuthResponse(token));
	}
	
	private String getJWTFromRequest(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7, bearerToken.length());
		}
		return null;
	}
}