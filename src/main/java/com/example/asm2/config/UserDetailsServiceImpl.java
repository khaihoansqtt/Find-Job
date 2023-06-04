package com.example.asm2.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.asm2.entity.User;
import com.example.asm2.repository.RoleRepository;
import com.example.asm2.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    // Build UserDetails bằng CustomUserDetails đã thiết lập
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).get();	// Lấy user theo email

        if (user == null) {
            System.out.println("User not found! " + email);
            throw new UsernameNotFoundException("User " + email + " was not found in the database");
        }

        System.out.println("Found User: " + user);

        // Lấy role của user
        String roleName = roleRepository.findById(user.getRole().getId()).get().getRoleName();

        List<GrantedAuthority> grantList = new ArrayList<GrantedAuthority>();
        if (roleName != null) {
        	GrantedAuthority authority = new SimpleGrantedAuthority(roleName);
        	grantList.add(authority);
        }

        // Tạo userDetails
        UserDetails userDetails = (UserDetails) new CustomUserDetails(user.getEmail(),
														                user.getPassword(), 
														                user.getId(), 
														                user.getStatus(), 
														                user.getFullName(), 
														                user.getDescription(), 
														                user.getAddress(), 
														                user.getPhoneNumber(), 
														                user.getImage(), 
														                grantList);
        return userDetails;
    }

}
