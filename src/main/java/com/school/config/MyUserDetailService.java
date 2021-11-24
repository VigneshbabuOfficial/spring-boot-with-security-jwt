package com.school.config;

import java.util.ArrayList;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.school.entities.Users;
import com.school.repos.UserRepository;
import com.school.utils.CommonConstants;

@Service
public class MyUserDetailService implements UserDetailsService{

	private static final String LOG_STR = "MyUserDetailService.%s";
	
	@Autowired
    private UserRepository repository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		
		Users user = repository.findByUserName(username);
		
		System.out.println(String.format(LOG_STR, "loadUserByUsername") + " , username = " + username);
		
		if( Objects.isNull(user)) {
			
			ObjectNode responseNode = JsonNodeFactory.instance.objectNode();

			responseNode.put(CommonConstants.RESPONSE, CommonConstants.ERROR);

			ArrayNode errorsArr = responseNode.putArray("errors");
			
			errorsArr.addObject().put(CommonConstants.ERRORCODE, "ACCESS_DENIED").put(CommonConstants.MESSAGE,
					"Invalid User name");
			
			System.out.println(String.format(LOG_STR, "loadUserByUsername") + " , responseNode = " + responseNode);
			
			return null;
		}
		
		return new org.springframework.security.core.userdetails.User(user.getUserName(), user.getPassword(), new ArrayList<>());
	}

}
