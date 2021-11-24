package com.school;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.annotation.RequestScope;

import com.school.entities.Users;
import com.school.repos.UserRepository;
import com.school.utils.RequestId;
import com.school.utils.SchoolLogger;

@SpringBootApplication(scanBasePackages = "com.school")
public class SchoolApplication {
	
	@Autowired
    private UserRepository repository;

	public static void main(String[] args) {
		SpringApplication.run(SchoolApplication.class, args);
	}
	
	@Bean
	public SchoolLogger logger() {
		return SchoolLogger.getLogger(" ##### com.school.api.v1.O ##### ");
	}
	
	@Bean
	@RequestScope(proxyMode = ScopedProxyMode.TARGET_CLASS)
	public RequestId requestId() {

		String string = Long.toString( Instant.now().toEpochMilli() );
		UUID requestId = UUID.nameUUIDFromBytes( string.getBytes() );

		return new RequestId( requestId.toString() );
	}
	
	@PostConstruct
    public void initUsers() {

		List<Users> dbUsers = repository.findAll();
		

		if (!dbUsers.isEmpty()) {

			repository.deleteAllInBatch(dbUsers);
		}

		List<Users> users = new ArrayList<>();

		users.add(new Users(102L, "user1", "pwd1", "user1@gmail.com"));
		users.add(new Users(103L, "user2", "pwd2", "user2@gmail.com"));
		users.add(new Users(104L, "user3", "pwd3", "user3@gmail.com"));

		repository.saveAll(users);
	}
	
}
