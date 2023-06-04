package com.example.asm2.dto;

import com.example.asm2.entity.Company;
import com.example.asm2.entity.Cv;
import com.example.asm2.entity.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
//@ToString
public class UserDTO {
	private int id;
	private String email;
	private String password;
	private String rePassword;
	private int status;
	private String fullName;
	private String description;
	private String address;
	private String phoneNumber;
	private String image;
	private String roleName;
	private int roleId;
	private Cv cv;
	private Company company;
	
	public UserDTO(User user) {
		id = user.getId();
		email = user.getEmail();
		password = user.getPassword();
		status = user.getStatus();
		fullName = user.getFullName();
		description = user.getDescription();
		address = user.getAddress();
		phoneNumber = user.getPhoneNumber();
		image = user.getImage();
		roleName = user.getRole().getRoleName();
		roleId = user.getRole().getId();
		cv = user.getCv();
		company = user.getCompany();
	}

	@Override
	public String toString() {
		return "UserDTO [id=" + id + ", email=" + email + ", password=" + password + ", rePassword=" + rePassword
				+ ", status=" + status + ", fullName=" + fullName + ", description=" + description + ", address="
				+ address + ", phoneNumber=" + phoneNumber + ", image=" + image + ", roleName=" + roleName + ", roleId="
				+ roleId + ", cv=" + cv + ", company=" + company + "]";
	}
}
