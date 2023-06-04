package com.example.asm2.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.asm2.entity.User;
import com.example.asm2.entity.VerificationToken;

public interface VerificationTokenRepository 
extends JpaRepository<VerificationToken, Long> {

  VerificationToken findByToken(String token);

  VerificationToken findByUser(User user);
}