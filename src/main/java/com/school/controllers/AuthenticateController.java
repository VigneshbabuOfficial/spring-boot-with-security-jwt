package com.school.controllers;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.school.config.AuthenticationRequest;
import com.school.config.AuthenticationResponse;
import com.school.config.MyUserDetailService;
import com.school.config.util.JwtUtil;
import com.school.entities.Users;
import com.school.repos.UserRepository;
import com.school.utils.CommonConstants;

@RestController
@RequestMapping("/authenticate")
public class AuthenticateController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtUtil jwtTokenUtil;

	@Autowired
	private MyUserDetailService userDetailsService;

	@Autowired
	private UserRepository userRepository;

	@PostMapping(value = "/login")
	public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest)
			throws Exception {

		System.out.println("AuthenticateController - authenticationRequest = " + authenticationRequest.getUsername()
				+ " - " + authenticationRequest.getPassword());

		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
					authenticationRequest.getUsername(), authenticationRequest.getPassword()));
		} catch (BadCredentialsException e) {
			throw new Exception("Incorrect username or password", e);
		}

		String userName = authenticationRequest.getUsername();

		final UserDetails userDetails = userDetailsService.loadUserByUsername(userName);

		final String jwt = jwtTokenUtil.generateToken(userDetails);

		Users loggedinUser = userRepository.findByUserName(userName);

		LocalDateTime nowDateTime = LocalDateTime.now(ZoneId.of("UTC")).plusMinutes(15L).withNano(0);

		loggedinUser.setToken(jwt);
		loggedinUser.setTokenExpirationDate(nowDateTime);
		loggedinUser.setLoggedIn(true);
		userRepository.saveAndFlush(loggedinUser);

		System.out.println("createAuthenticationToken - authenticationRequest = " + authenticationRequest.getUsername()
				+ " - " + authenticationRequest.getPassword() + ", jwt = " + jwt);

		return ResponseEntity.ok(new AuthenticationResponse(jwt, nowDateTime, "Loggedin successfully"));
	}

	@GetMapping(value = "/refresh")
	public ResponseEntity<?> refreshAuthenticationToken(HttpServletRequest request) throws Exception {

		String authorizationHeader = request.getHeader("Authorization");

		if (authorizationHeader == null) {
			return new ResponseEntity<>(new AuthenticationResponse("", null, "sorry! authorizationHeader is missed"),
					HttpStatus.BAD_REQUEST);
		}

		String jwt = null;

		LocalDateTime nowDateTime = LocalDateTime.now(ZoneId.of("UTC")).plusMinutes(15L).withNano(0);

		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			jwt = authorizationHeader.substring(7);
		}

		System.out.println(
				"AuthenticateController - refreshAuthenticationToken - authorizationHeader = " + authorizationHeader);

		Users loggedinUser = userRepository.findByToken(jwt);

		if (Objects.isNull(loggedinUser)) {
			return new ResponseEntity<>(new AuthenticationResponse(jwt, nowDateTime, "sorry! Invalid Token"),
					HttpStatus.BAD_REQUEST);
		}

		final UserDetails userDetails = userDetailsService.loadUserByUsername(loggedinUser.getUserName());

		jwt = jwtTokenUtil.generateToken(userDetails);

		loggedinUser.setToken(jwt);
		loggedinUser.setTokenExpirationDate(nowDateTime);
		loggedinUser.setLoggedIn(true);
		userRepository.saveAndFlush(loggedinUser);

		System.out.println("AuthenticateController - refreshAuthenticationToken - userName ="
				+ loggedinUser.getUserName() + ", jwt = " + jwt);

		return ResponseEntity.ok(new AuthenticationResponse(jwt, nowDateTime, "Token refreshed successfully"));
	}

	@GetMapping(value = "/logout")
	public ResponseEntity<?> logout(HttpServletRequest request) throws Exception {

		final String authorizationHeader = request.getHeader("Authorization");

		String jwt = null;

		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			jwt = authorizationHeader.substring(7);
		}

		System.out.println(
				"AuthenticateController - authenticationRequest - authorizationHeader = " + authorizationHeader);

		String userName = jwtTokenUtil.extractUsername(jwt);

		Users loggedinUser = userRepository.findByUserName(userName);

		loggedinUser.setToken(null);
		loggedinUser.setTokenExpirationDate(loggedinUser.getTokenExpirationDate().minusMinutes(1L));
		loggedinUser.setLoggedIn(false);
		userRepository.saveAndFlush(loggedinUser);

		return ResponseEntity.ok(new AuthenticationResponse(jwt, null, "Logged out successfully"));
	}

	@GetMapping(value = "/error")
	public ResponseEntity<?> tokenExpiration(HttpServletRequest request) {

		final String authorizationHeader = request.getHeader("Authorization");

		String jwt = null;

		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			jwt = authorizationHeader.substring(7);
		}

		System.out.println("AuthenticateController - tokenExpiration - authorizationHeader = " + authorizationHeader);

		return new ResponseEntity<>(new AuthenticationResponse(jwt, null, CommonConstants.INVALID_TOKEN_MSG),
				HttpStatus.BAD_REQUEST);
	}

}
