package com.example.asm2.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.asm2.entity.Role;
import java.util.List;
import com.example.asm2.entity.User;


public interface RoleRepository extends JpaRepository<Role, Integer> {
	
}
