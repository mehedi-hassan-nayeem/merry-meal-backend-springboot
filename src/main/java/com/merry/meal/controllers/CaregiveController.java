package com.merry.meal.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.merry.meal.config.AppConstants;
import com.merry.meal.config.SessionResponse;
import com.merry.meal.payload.ApiResponse;
import com.merry.meal.payload.CareMemberDto;
import com.merry.meal.payload.MealDto;
import com.merry.meal.payload.SessionDto;
import com.merry.meal.repo.AccountRepo;
import com.merry.meal.services.CaregiveService;
import com.merry.meal.utils.JwtUtils;

@RestController
@RequestMapping("/api/v1/caregivers")
public class CaregiveController {
	@Autowired
	private CaregiveService caregiveService;
	@Autowired
	private JwtUtils jwtUtils;
	@Autowired
	private AccountRepo accountRepo;
	@Autowired
	private ModelMapper modelmapper;

	@PostMapping("/caregivepost")
	public ResponseEntity<SessionDto> newSession(@RequestBody SessionDto sessionDto, HttpServletRequest request) {
		SessionDto newSessionDto = this.caregiveService.addSession(sessionDto, request);
		return new ResponseEntity<SessionDto>(newSessionDto, HttpStatus.CREATED);

	}

//get all Session
	@GetMapping("/sessions")
	public ResponseEntity<List<SessionDto>> allSessions(HttpServletRequest request) {
		List<SessionDto> availableSession = this.caregiveService.getAllSession(request);

		if (availableSession.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		return ResponseEntity.status(HttpStatus.OK).body(availableSession);
	}

	@PostMapping("/caregivepost/{userId}")
	public ResponseEntity<SessionDto> newCreateSession(@RequestBody SessionDto sessionDto, HttpServletRequest request,
			Long userId) {
		SessionDto newSessionDto = this.caregiveService.createSession(sessionDto, userId, request);
		return new ResponseEntity<SessionDto>(newSessionDto, HttpStatus.CREATED);

	}

	@GetMapping("/caregivepost/{sessionId}")
	public ResponseEntity<SessionDto> getpost(@PathVariable Long sessionId) {
		System.out.println("This controller for get session as ");
		SessionDto getSession = this.caregiveService.getoneSession(sessionId);
		System.out.println(getSession);
		return new ResponseEntity<SessionDto>(getSession, HttpStatus.OK);

	}

	@GetMapping("/{userId}/caregivepost")
	public ResponseEntity<List<SessionDto>> getSessionasUser(@PathVariable Long userId, HttpServletRequest request) {
		List<SessionDto> sessionDtos = this.caregiveService.getsessionByUser(userId, request);
		return new ResponseEntity<List<SessionDto>>(sessionDtos, HttpStatus.OK);

	}

	@DeleteMapping("/{sessionId}/caregivepost")
	public ApiResponse deleteSession(@PathVariable Long sessionId, HttpServletRequest request) {
		this.caregiveService.deleteSession(sessionId, request);
		return new ApiResponse("Session is successfully deleted", true);

	}

//this code for update session
	@PutMapping("/caregivepost")
	public ResponseEntity<ApiResponse> updateSession(@RequestBody SessionDto sessionDto, HttpServletRequest request) {

		this.caregiveService.updateSession(sessionDto, jwtUtils.getJWTFromRequest(request));
		return new ResponseEntity<ApiResponse>(new ApiResponse("Session updated successfully", true),
				HttpStatus.CREATED);

	}
	
	@PutMapping("/requestCare/{sessionId}")
	public ResponseEntity<?> requestCare(HttpServletRequest request, @PathVariable Long sessionId){
		CareMemberDto careMemberDto = this.caregiveService.requestCare(sessionId,jwtUtils.getJWTFromRequest(request));
		if(careMemberDto == null) {
			return ResponseEntity.badRequest().body("Session with id " + sessionId + " not found");
		}
		return ResponseEntity.ok(careMemberDto);
	}
}
