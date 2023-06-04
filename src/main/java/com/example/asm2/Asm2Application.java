package com.example.asm2;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.PageRequest;

import com.example.asm2.entity.Category;
import com.example.asm2.entity.Company;
import com.example.asm2.repository.CategoryRepository;
import com.example.asm2.repository.CompanyRepository;

@SpringBootApplication
public class Asm2Application {

	public static void main(String[] args) {
		SpringApplication.run(Asm2Application.class, args);
	}
	
	@Bean
	CommandLineRunner run(CategoryRepository categoryRepository, CompanyRepository companyRepository) {
		return args -> {
			List<Company> companies = companyRepository.findTopCompanies(PageRequest.of(0, 1));
			companies.forEach(company -> System.out.println(company.getName()));
		};
	}
}
