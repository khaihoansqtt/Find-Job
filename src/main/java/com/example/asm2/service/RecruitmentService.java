package com.example.asm2.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.example.asm2.config.CustomUserDetails;
import com.example.asm2.dto.RecruitmentDTO;
import com.example.asm2.entity.ApplyPost;
import com.example.asm2.entity.Category;
import com.example.asm2.entity.Company;
import com.example.asm2.entity.Recruitment;
import com.example.asm2.entity.User;
import com.example.asm2.repository.ApplyPostRepository;
import com.example.asm2.repository.CategoryRepository;
import com.example.asm2.repository.CompanyRepository;
import com.example.asm2.repository.RecruitmentRepository;
import com.example.asm2.repository.UserRepository;

@Service
@Transactional
public class RecruitmentService {
	@Autowired
	RecruitmentRepository recruitmentRepository;
	@Autowired
	CompanyRepository companyRepository;
	@Autowired
	CategoryRepository categoryRepository;
	@Autowired
	UserRepository userRepository;
	@Autowired
	ApplyPostRepository applyPostRepository;
	
	// Tạo mới một bài đăng tuyển công việc
	public void addNewRecruitment(int userId, RecruitmentDTO recruitmentDTO) {
		User user = userRepository.findById(userId).get();
		Company company = companyRepository.findById(user.getCompany().getId()).get();
		Category category = categoryRepository.findById(recruitmentDTO.getCategoryId()).get();
		
		Recruitment recruitment = new Recruitment();
		recruitment.setTitle(recruitmentDTO.getTitle());
		recruitment.setDescription(recruitmentDTO.getDescription());
		recruitment.setExperience(recruitmentDTO.getExperience());
		recruitment.setQuantity(recruitmentDTO.getQuantity());
		recruitment.setAddress(recruitmentDTO.getAddress());
		recruitment.setDeadline(recruitmentDTO.getDeadline());
		recruitment.setSalary(recruitmentDTO.getSalary());
		recruitment.setType(recruitmentDTO.getType());
		recruitment.setCategory(category);
		recruitment.setCompany(company);
		
		recruitmentRepository.save(recruitment);
	}
	
	// Lấy các bài recruitments đã đăng tuyển của một user(EMPLOYER)
	public Page<RecruitmentDTO> getCruitmentsByUserId(int userId, int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, 2);
        User user = userRepository.findById(userId).get();
        Company company = user.getCompany();
        Page<Recruitment> recruitmentPage = recruitmentRepository.findByCompanyId(company.getId(), pageable);
        return recruitmentPage.map(recruitment -> new RecruitmentDTO(recruitment));
	}
	
	// Lấy recruitment theo id của nó
	public RecruitmentDTO getRecruitmentById(int recruitmentId) {
		Optional<Recruitment> result = recruitmentRepository.findById(recruitmentId);
		if (result.isPresent()) {
			return new RecruitmentDTO(result.get());
		} else return null;
	}
	
	// cập nhật một bài đăng tuyển
	public void updateRecruitment(RecruitmentDTO recruitmentDTO) {
		Recruitment recruitment = recruitmentRepository.findById(recruitmentDTO.getId()).get();
		Category category = categoryRepository.findById(recruitmentDTO.getCategoryId()).get();
		
		recruitment.setTitle(recruitmentDTO.getTitle());
		recruitment.setDescription(recruitmentDTO.getDescription());
		recruitment.setExperience(recruitmentDTO.getExperience());
		recruitment.setQuantity(recruitmentDTO.getQuantity());
		recruitment.setAddress(recruitmentDTO.getAddress());
		recruitment.setDeadline(recruitmentDTO.getDeadline());
		recruitment.setSalary(recruitmentDTO.getSalary());
		recruitment.setType(recruitmentDTO.getType());
		recruitment.setCategory(category);
		
		recruitmentRepository.save(recruitment);
	}
	
	// xóa một bài đăng tuyển
	public void deleteRecruitment(int recruitmentId) {
		recruitmentRepository.deleteById(recruitmentId);;
	}
	
	// tìm kiếm bằng tiêu đề của bài dăng tuyển
	public Page<RecruitmentDTO> searchByTitle(String keySearch, int page) {
		Pageable pageable = PageRequest.of(page, 3);
		Page<Recruitment> recruitments = recruitmentRepository.searchByTitle(keySearch, pageable);

		return pageEntityToDTORecruitment(recruitments);
		
	}

	// tìm kiếm bằng địa chỉ của bài dăng tuyển
	public Page<RecruitmentDTO> searchByAddress(String keySearch, int page) {
		Pageable pageable = PageRequest.of(page, 3);
		Page<Recruitment> recruitments = recruitmentRepository.searchByAddress(keySearch, pageable);
		
		return pageEntityToDTORecruitment(recruitments);
		
	}

	// tìm kiếm bằng tên của công ty 
	public Page<RecruitmentDTO> searchByCompanyName(String keySearch, int page) {
		List<Company> companies = companyRepository.searchByName(keySearch);
		List<Integer> companyIds = companies.stream().map(company -> company.getId()).collect(Collectors.toList());
		
		Pageable pageable = PageRequest.of(page, 3);
		
		Page<Recruitment> recruitmentsPage = recruitmentRepository.searchByCompanyIds(companyIds, pageable);
		
		return pageEntityToDTORecruitment(recruitmentsPage);
	}

	// Lấy cái apply của một bài đăng tuyển theo id của bài đăng
	public List<ApplyPost> getApplyPosts(int recruitmentId) {
		Recruitment recruitment = recruitmentRepository.findById(recruitmentId).get();
		List<ApplyPost> applyPosts = applyPostRepository.findAllByRecruitment(recruitment);
		return applyPosts;
	}
	
	// Lấy top 4 bài đăng tuyển
	public List<Recruitment> getTopRecruitments() {
		return recruitmentRepository.findTopRecruitments(PageRequest.of(0, 4));
	}
	
	// chuyển list entity thành DTO
	public List<RecruitmentDTO> listEntityToDTORecruitment(List<Recruitment> recruitments) {
		return recruitments.stream().map(recruitment -> new RecruitmentDTO(recruitment)).collect(Collectors.toList());
	}

	// chuyển page entity thành DTO
	public Page<RecruitmentDTO> pageEntityToDTORecruitment(Page<Recruitment> recruitmentsPage) {
		return recruitmentsPage.map(recruitment -> new RecruitmentDTO(recruitment));
	}
}
