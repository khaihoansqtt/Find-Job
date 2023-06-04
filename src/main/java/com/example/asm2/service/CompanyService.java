package com.example.asm2.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.example.asm2.dto.CompanyDTO;
import com.example.asm2.dto.RecruitmentDTO;
import com.example.asm2.entity.Company;
import com.example.asm2.entity.Recruitment;
import com.example.asm2.entity.User;
import com.example.asm2.repository.CompanyRepository;
import com.example.asm2.repository.RecruitmentRepository;
import com.example.asm2.repository.UserRepository;
import com.example.asm2.utils.FileUploadUtil;

@Service
@Transactional
public class CompanyService {
	@Autowired
	private CompanyRepository companyRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RecruitmentRepository recruitmentRepository;

//	public CompanyDTO getCompanyByUser(User user) {
//		Optional<Company> result = companyRepository.findByUser(user);
//		if (result.isPresent()) {
//			return new CompanyDTO(result.get());
//		} else return null;
//	}
	
	// upload logo của company, lưu tại: "src/main/resources/static/upload/company" + companyId
	public String uploadCompanyLogo(MultipartFile multipartFile, String email) throws IOException {
		User user = userRepository.findByEmail(email).get();
		int companyId = user.getCompany().getId();

		// Lấy fileName của file upload lên 
		String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
		// Tạo đường dẫn thư mục lưu file
		String uploadDir = "src/main/resources/static/upload/company" + companyId;
		// Lưu file
		FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		String logoPath = "/upload/company" + companyId + "/" + fileName;

		Company company = user.getCompany(); 
		company.setLogo(logoPath);		// set logo cho company
		
		companyRepository.save(company);
		return logoPath;
	}

	// Cập nhật thông tin của company
	public void updateCompany(CompanyDTO companyDTO) {
		Company company = getCompanyById(companyDTO.getId());

		company.setEmail(companyDTO.getEmail());
		company.setName(companyDTO.getName());
		company.setAddress(companyDTO.getAddress());
		company.setPhoneNumber(companyDTO.getPhoneNumber());
		company.setDescription(companyDTO.getDescription());

		companyRepository.save(company);
	}
	
	public Company getCompanyById(int companyId) {
		Optional<Company> result = companyRepository.findById(companyId);
		if (result.isPresent()) {
			return result.get();
		} else return null;
	}
	
	// Lấy danh sách các recruitment của một công ty theo id
	public Page<RecruitmentDTO> getCompanyPostList(int companyId, int page) {
		Pageable pageable = PageRequest.of(page, 3);
		Page<Recruitment> recruitmentsPage = recruitmentRepository.findbyCompanyId(companyId, pageable);
		return recruitmentsPage.map(recruitment -> new RecruitmentDTO(recruitment));
	}
	
	// Lấy top 4 công ty xịn nhất
	public List<Company> getTopCompanies() {
		return companyRepository.findTopCompanies(PageRequest.of(0, 4));
	}
}
