package com.example.asm2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.asm2.entity.Role;
import com.example.asm2.repository.RoleRepository;

@Service
@Transactional
public class RoleService {
	@Autowired
	private RoleRepository roleRepository;
	
	public Role getRole(int roleId) {
		return roleRepository.findById(roleId).get();
	}
}
