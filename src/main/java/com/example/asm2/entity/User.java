package com.example.asm2.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.example.asm2.dto.UserDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="users")
@AllArgsConstructor
@Getter
@Setter
//@ToString
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	@Column(name = "email")
	private String email;

	@Column(name = "password")
	private String password;

	@Column(name = "status")
	private int status;

	@Column(name = "full_name")
	private String fullName;

	@Column(name = "description")
	private String description;

	@Column(name = "address")
	private String address;

	@Column(name = "phone_number")
	private String phoneNumber;

	@Column(name = "image")
	private String image;

	@ManyToOne
	@JoinColumn(name = "role_id")
	private Role role;
	
	@OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
	@JoinColumn(name = "cv_id")
	private Cv cv;
	
	@OneToOne(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
	private Company company;
	
	@OneToMany(mappedBy = "user",
				fetch = FetchType.LAZY,
				cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
	private List<ApplyPost> applyPost;
	
	@ManyToMany(fetch = FetchType.LAZY,
				cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
	@JoinTable(name = "save_job",
				joinColumns = @JoinColumn(name = "user_id"),
				inverseJoinColumns = @JoinColumn(name = "recruitment_id"))
	private List<Recruitment> savedRecruitments;
	
	@ManyToMany(fetch = FetchType.LAZY,
				cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
	@JoinTable(name = "follow_company",
				joinColumns = @JoinColumn(name = "user_id"),
				inverseJoinColumns = @JoinColumn(name = "company_id"))
	private List<Company> followedCompanies;
	
	public User(UserDTO userDTO) {
		id = userDTO.getId();
		email = userDTO.getEmail();
		password = userDTO.getPassword();
		status = userDTO.getStatus();
		fullName = userDTO.getFullName();
		description = userDTO.getDescription();
		address = userDTO.getAddress();
		phoneNumber = userDTO.getPhoneNumber();
		image = userDTO.getImage();
		cv = userDTO.getCv();
		company = userDTO.getCompany();
	}
	
	public User() {
		this.status = 0;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", email=" + email + ", password=" + password + ", status=" + status + ", fullName="
				+ fullName + ", description=" + description + ", address=" + address + ", phoneNumber=" + phoneNumber
				+ ", image=" + image + ", role=" + role + ", cv=" + cv + ", company=" + company + "]";
	}
}
