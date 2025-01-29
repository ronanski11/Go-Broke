package com.ina23c.gobroke.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.ina23c.gobroke.model.User;

@Repository
public interface UserRepository extends MongoRepository<User, String>{
	
	@Query("{ username: ?0 }")
	public Optional<User> findByUsernameOptional(String username);
	
	User findByUsername(String username);

}
