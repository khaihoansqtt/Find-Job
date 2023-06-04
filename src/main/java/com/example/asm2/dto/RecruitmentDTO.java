package com.example.asm2.dto;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.example.asm2.entity.Category;
import com.example.asm2.entity.Company;
import com.example.asm2.entity.Recruitment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecruitmentDTO {
	private int id;
    private String title;
    private String description;
    private String experience;
    private int quantity;
    private String address;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date deadline;
    private String salary;
    private String type;
    private String rank;
    private int status;
    private int view;
    private Date createdAt;
    private int categoryId;
    private int companyId;
    private Company company;
    private Category category;
    
    public RecruitmentDTO(Recruitment recruitment) {
    	id = recruitment.getId();
    	title = recruitment.getTitle();
    	description = recruitment.getDescription();
    	experience = recruitment.getExperience();
    	quantity = recruitment.getQuantity();
    	address = recruitment.getAddress();
    	deadline = recruitment.getDeadline();
    	salary = recruitment.getSalary();
    	type = recruitment.getType();
    	rank = recruitment.getRank();
    	status = recruitment.getStatus();
    	view = recruitment.getView();
    	createdAt = recruitment.getCreatedAt();
    	categoryId = recruitment.getCategory().getId();
    	companyId = recruitment.getCompany().getId();
    	company = recruitment.getCompany();
    	category = recruitment.getCategory();
    }

	@Override
	public String toString() {
		return "RecruitmentDTO [id=" + id + ", title=" + title + ", description=" + description + ", experience="
				+ experience + ", quantity=" + quantity + ", address=" + address + ", deadline=" + deadline
				+ ", salary=" + salary + ", type=" + type + ", rank=" + rank + ", status=" + status + ", view=" + view
				+ ", createdAt=" + createdAt + ", category=" + categoryId + ", company=" + companyId + "]";
	}
}
