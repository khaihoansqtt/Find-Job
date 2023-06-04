package com.example.asm2.config;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.userdetails.UserDetails;

// Cấu hình custom userDetails để lấy đc đầy đủ thông tin user đã xác thực từ spring security
public class CustomUserDetails implements UserDetails {

	private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

	private String password;
	private String email;
	private int id;
	private int status;
	private String fullName;
	private String description;
	private String address;
	private String phoneNumber;
	private String image;
	private List<GrantedAuthority> authorities;
	private boolean accountNonExpired;
	private boolean accountNonLocked;
	private boolean credentialsNonExpired;
	private boolean enabled;
	
	public CustomUserDetails(String email, String password, int id, int status, String fullName, String description,
						String address, String phoneNumber, String image, List<GrantedAuthority> authorities) {
		this.email = email;
		this.password = password;
		this.id = id;
		this.status = status;
		this.fullName = fullName;
		this.description = description;
		this.address = address;
		this.phoneNumber = phoneNumber;
		this.image = image;
		this.authorities = authorities;
		this.accountNonExpired = true;
		this.accountNonLocked = true;
		this.credentialsNonExpired = true;
		this.enabled = true;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return password;
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return email;
	}

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return accountNonExpired;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return accountNonLocked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return credentialsNonExpired;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return enabled;
	}

	public int getId() {
		return id;
	}

	public int getStatus() {
		return status;
	}

	public String getFullName() {
		return fullName;
	}

	public String getDescription() {
		return description;
	}

	public String getAddress() {
		return address;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public String getImage() {
		return image;
	}
}
