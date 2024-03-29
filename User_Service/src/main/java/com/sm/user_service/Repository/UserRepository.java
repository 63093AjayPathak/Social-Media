package com.sm.user_service.repository;

import java.util.List;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import com.sm.user_service.node.User;

public interface UserRepository extends Neo4jRepository<User, Integer> {
	
	@Query("Match (n: User) Where n.name STARTS WITH $name RETURN n")
	public List<User> findByName(@Param("name")String name);
	
	@Query("Match (n: User) Where n.email STARTS WITH $email RETURN n")
	public List<User> findByEmail(@Param("email")String email);
	
	@Query("Match (n: User) Where n.city STARTS WITH $city AND n.country STARTS WITH $country RETURN n")
	public List<User> findByCityAndCountry(@Param("city")String city, @Param("country")String country);
	
	@Query("MATCH (n: User) WHERE n.country STARTS WITH $country RETURN n")
	public List<User> findByCountry(@Param("country") String country);
	
//	to delete a relationship
	@Query("Match (a: User)-[r: REQUEST_RECEIVED ]->(b: User) Where a.email STARTS WITH $email1 And b.email STARTS WITH $email2 Delete r") // :#{#email2}
	public void deleteRequestRelatiotnship(@Param("email1") String email1, @Param("email2") String email2);
	
	@Query("Match (a: User)-[r: REQUEST_SEND ]->(b: User) Where a.email STARTS WITH $email1 And b.email STARTS WITH $email2 Delete r")
	public void deleteRequestSendRelationship(@Param("email1") String email1, @Param("email2") String email2);
	
	@Query("Match (a: User)-[r: FRIENDS ]->(b: User) Where a.email STARTS WITH $email1 And b.email STARTS WITH $email2 Delete r")
	public void deleteFriendshipRelationship(@Param("email1") String email1, @Param("email2") String email2);
	
//	get users form a specific country 
	

}
