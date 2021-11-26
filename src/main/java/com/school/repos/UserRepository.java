package com.school.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.school.entities.Users;

@Repository
public interface UserRepository extends JpaRepository<Users, Long>{

	Users findByUserName(String username);

	Users findByToken(String token);

}
