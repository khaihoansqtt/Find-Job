package com.example.asm2.dto;

import com.example.asm2.entity.Company;
import com.example.asm2.entity.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CompanyDTO {
	private int id;
	private String name;
	private String address;
	private String email;
	private String phoneNumber;
	private String description;
	private String logo;
	private int status;
	private User user;
	
	public CompanyDTO(Company company) {
		this.id = company.getId();
		this.name = company.getName();
		this.address = company.getAddress();
		this.email = company.getEmail();
		this.phoneNumber = company.getPhoneNumber();
		this.description = company.getDescription();
		this.logo = company.getLogo();
		this.status = company.getStatus();
		this.user = company.getUser();
	}

	@Override
	public String toString() {
		return "CompanyDTO [id=" + id + ", name=" + name + ", address=" + address + ", email=" + email
				+ ", phoneNumber=" + phoneNumber + ", description=" + description + ", logo=" + logo + ", status="
				+ status + ", user=" + user + "]";
	}
}
