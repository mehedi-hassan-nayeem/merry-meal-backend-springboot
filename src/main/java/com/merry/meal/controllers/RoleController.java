package com.merry.meal.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.merry.meal.data.Account;
import com.merry.meal.data.Role;
import com.merry.meal.data.User;
import com.merry.meal.data.UserRole;
import com.merry.meal.payload.RoleResponse;
import com.merry.meal.payload.RolesArrayResponse;
import com.merry.meal.repo.AccountRepo;
import com.merry.meal.repo.RoleRepository;
import com.merry.meal.repo.UserRepository;
import com.merry.meal.repo.UserRoleRepository;
import com.merry.meal.services.UserService;
import com.merry.meal.utils.JwtUtils;

@RestController
@RequestMapping("/api/v1/role")
public class RoleController {

	@Autowired
	private JwtUtils jwtUtils;

	@Autowired
	private UserRoleRepository userRoleRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private AccountRepo accountRepo;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserService userService;

	@GetMapping("/userRoles")
	public ResponseEntity<?> getUserRoles(HttpServletRequest request) {
		String token = jwtUtils.getJWTFromRequest(request);
		String email = jwtUtils.getUserNameFromToken(token);
		Account account = accountRepo.findByEmail(email).get();
		List<String> roleResponses = new ArrayList<String>();
		account.getUserRoles().stream().forEach((role) -> {
			roleResponses.add(role.getRole().getRole_name());
		});
		return ResponseEntity.ok(new RolesArrayResponse(roleResponses));
	}

	@PostMapping("/{userId}")
	public ResponseEntity<?> appendNewRole(HttpServletRequest request, @PathVariable Long userId,
			@RequestParam String assignRoles) {
		User user = userService.getUser(userId);
		if (user == null) {
			return new ResponseEntity<String>("User with " + userId + " cannot be found", HttpStatus.BAD_REQUEST);
		}
		Account account = user.getAccount();
		List<UserRole> originalUserRole = account.getUserRoles();
		originalUserRole.forEach((userRole) -> {
			userRoleRepository.deleteById(userRole.getUser_role_id());
		});
		String[] roles = assignRoles.split(",");
		for (String roleId : roles) {
			Role role = roleRepository.findById(Long.parseLong(roleId)).get();
			UserRole userRole = new UserRole();
			userRole.setAccount(account);
			userRole.setRole(role);
			userRoleRepository.save(userRole);
		}
		return new ResponseEntity<String>("User roles have been updated successfully", HttpStatus.OK);
	}
	
	@GetMapping("/{userId}")
	public ResponseEntity<?> getRolesForOtherUser(HttpServletRequest request, @PathVariable Long userId) {
		User user = userService.getUser(userId);
		if (user == null) {
			return new ResponseEntity<String>("User with " + userId + " cannot be found", HttpStatus.BAD_REQUEST);
		}
		Account account = user.getAccount();
		List<String> roleResponses = new ArrayList<String>();
		account.getUserRoles().stream().forEach((role) -> {
			roleResponses.add(role.getRole().getRole_name());
		});
		return ResponseEntity.ok(new RolesArrayResponse(roleResponses));
	}
}
