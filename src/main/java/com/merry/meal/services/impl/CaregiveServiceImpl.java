package com.merry.meal.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.merry.meal.config.SessionResponse;
import com.merry.meal.data.Account;
import com.merry.meal.data.CareMember;
import com.merry.meal.data.Meal;
import com.merry.meal.data.Session;
import com.merry.meal.data.User;
import com.merry.meal.exceptions.ResourceNotFoundException;
import com.merry.meal.payload.CareMemberDto;
import com.merry.meal.payload.MealDto;
import com.merry.meal.payload.SessionDto;
import com.merry.meal.repo.AccountRepo;
import com.merry.meal.repo.CareMemberRepository;
import com.merry.meal.repo.SessionRepository;
import com.merry.meal.services.CaregiveService;
import com.merry.meal.status.CareStatus;
import com.merry.meal.utils.JwtUtils;

@Service
public class CaregiveServiceImpl implements CaregiveService {

	@Autowired
	private ModelMapper modelmapper;

	@Autowired
	private JwtUtils jwtUtils;
	@Autowired
	private AccountRepo accountRepo;
	@Autowired
	private SessionRepository sessionRepo;
	@Autowired
	private CareMemberRepository careMemberRepo;

	private String getJWTFromRequest(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7, bearerToken.length());
		}

		return null;
	}

	// only caregiver can add their session
	@Override
	public SessionDto addSession(SessionDto sessionDto, HttpServletRequest request) {
		String token = getJWTFromRequest(request);
		String email = jwtUtils.getUserNameFromToken(token);
		Account account = accountRepo.findByEmail(email).get();
		System.out.println(account);
		if (account.getUser() != null) {
			User user = account.getUser();
			System.out.println();
			user.getUser_id();
		}
		

		Session session = this.modelmapper.map(sessionDto, Session.class);
		session.setStatus(CareStatus.Available.name());
		session.setUser(account.getUser());
		Session saveSession = this.sessionRepo.save(session);
		return this.modelmapper.map(saveSession, SessionDto.class);
	}
	
	@Override
	public List<SessionDto> getAllSession(HttpServletRequest request) {
		String token = jwtUtils.getJWTFromRequest(request);
		String email = jwtUtils.getUserNameFromToken(token);
		Account account = accountRepo.findByEmail(email).get();
		List<Session> allSession = this.sessionRepo.findAll();
		if (account.getUser() != null) {
			User user = account.getUser();
			System.out.println();
			user.getUser_id();
		}
		
		List<SessionDto> allSessionDto = allSession.stream().map(sessiom ->this.modelmapper.map(sessiom, SessionDto.class)).collect(Collectors.toList());

		return allSessionDto;
	}
	
	
	// only care caregiver can add session in the databaseas user id
	@Override
	public SessionDto createSession(SessionDto sessionDto, Long userId, HttpServletRequest request) {
		String token = getJWTFromRequest(request);
		String email = jwtUtils.getUserNameFromToken(token);
		Account account = accountRepo.findByEmail(email).get();
		System.out.println("it will get account details of logged in user" + account);
		if (account.getUser() != null) {
			User user = account.getUser();
			user.getUser_id();
			Session session = this.modelmapper.map(sessionDto, Session.class);
			session.setStatus(CareStatus.Available.name());
			session.setUser(user);
			Session saveSession = this.sessionRepo.save(session);
			return this.modelmapper.map(saveSession, SessionDto.class);
		}
		
		return null;

	}

	// User and can member both can see the session
	@Override
	public SessionDto getoneSession(Long sessionId) {

		Session session = this.sessionRepo.findById(sessionId)
				.orElseThrow(() -> new ResourceNotFoundException("Session", "SessionId", sessionId.toString()));
		System.out.println(session);
		return this.modelmapper.map(session, SessionDto.class);
	}

	// Member will get caregiver session
	@Override
	public List<SessionDto> getsessionByUser(Long userId, HttpServletRequest request) {
		String token = getJWTFromRequest(request);
		String email = jwtUtils.getUserNameFromToken(token);
		Account account = accountRepo.findByEmail(email).get();
		System.out.println("////////////////////////////////////");
		System.out.println(account);
		System.out.println("////////////////////////////////////");
		if (account.getUser() != null) {
			User user = account.getUser();
			System.out.println();
			user.getUser_id();
		}

		CareMember careMember = new CareMember();
		User user = careMember.getUser();
		System.out.println("get user entity" + user);
		List<Session> sessions = user.getSession();
		List<SessionDto> sessionDtos = sessions.stream()
				.map((session) -> this.modelmapper.map(session, SessionDto.class)).collect(Collectors.toList());
		return sessionDtos;
	}

	// Only Caregiver able to delete session
	@Override
	public void deleteSession(Long sessionId, HttpServletRequest request) {
		String token = getJWTFromRequest(request);
		String email = jwtUtils.getUserNameFromToken(token);
		Account account = accountRepo.findByEmail(email).get();
		System.out.println("////////////////////////////////////");
		System.out.println(account);
		System.out.println("////////////////////////////////////");
		if (account.getUser() != null) {
			User user = account.getUser();
			System.out.println();
			user.getUser_id();
		}
		Session session = this.sessionRepo.findById(sessionId)
				.orElseThrow(() -> new ResourceNotFoundException("Session", "Session Id", sessionId.toString()));
		this.sessionRepo.delete(session);
	}

	// only caregiver update his session
	@Override
	public SessionDto updateSession(SessionDto sessionDto,  String token) {

		String email = jwtUtils.getUserNameFromToken(token);
		Account account = accountRepo.findByEmail(email)
		.orElseThrow(() -> new ResourceNotFoundException("user", "credentials", email));
		
		this.sessionRepo.findById(sessionDto.getSession_id())
		.orElseThrow(() -> new ResourceNotFoundException("Session", "Session id", sessionDto.getSession_id().toString()));
		Session session = this.modelmapper.map(sessionDto, Session.class);

		System.out.println("////////////////////Session ---------------Id---//////////////////////");
		session.setStatus(sessionDto.getStatus());
		session.setDate(sessionDto.getDate());
		session.setSession(sessionDto.getSession());
		session.setUser(account.getUser());
		Session updateSession = sessionRepo.save(session);
		return this.modelmapper.map(updateSession, SessionDto.class);
	}

	// Caregiver and member both
	@Override
	public SessionResponse getSession(Integer pageNumber, Integer pazeSize, String sortBy) {

		PageRequest page = PageRequest.of(pageNumber, pazeSize, Sort.by(sortBy).descending());

		Page<Session> pagePost = this.sessionRepo.findAll(page);

		List<Session> allPosts = pagePost.getContent();

		List<SessionDto> allSession = allPosts.stream().map((post) -> this.modelmapper.map(post, SessionDto.class))
				.collect(Collectors.toList());
		SessionResponse newResponse = new SessionResponse();
		newResponse.setContent(allSession);
		newResponse.setTotalPages(pagePost.getTotalPages());
		newResponse.setTotalElement(pagePost.getTotalElements());
		newResponse.setPageSize(pagePost.getSize());
		newResponse.setLastPage(pagePost.isLast());

		return newResponse;
	}

	@Override
	public CareMemberDto requestCare(Long sessionId, String token) {
		String email = jwtUtils.getUserNameFromToken(token);
		Account account = accountRepo.findByEmail(email)
		.orElseThrow(() -> new ResourceNotFoundException("user", "credentials", email));
		User member = account.getUser(); 
		Optional<Session> optionalSession = this.sessionRepo.findById(sessionId);
		if(optionalSession.isEmpty()) {
			return null;
		}
		Session session = optionalSession.get();
		session.setStatus(CareStatus.Unavilable.name());
		this.sessionRepo.save(session);
		User caregiver = session.getUser();
		CareMember careMember = new CareMember();
		careMember.setUser(member);
		careMember.setCaregiver(caregiver);
		careMember =  this.careMemberRepo.save(careMember);
		return this.modelmapper.map(careMember, CareMemberDto.class); 
	}

}
