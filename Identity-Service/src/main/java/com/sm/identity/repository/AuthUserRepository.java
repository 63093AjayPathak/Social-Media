package com.sm.identity.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sm.identity.entity.AuthUser;

public interface AuthUserRepository extends JpaRepository<AuthUser, Integer> {

	public Optional<AuthUser> findByEmail(String email);
}
