package com.example.asm2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.example.asm2.customResponse.MyUploadFileResponse;
import com.example.asm2.dto.CompanyDTO;
import com.example.asm2.dto.RecruitmentDTO;
import com.example.asm2.entity.Company;
import com.example.asm2.service.CompanyService;

@Controller
@RequestMapping("/company")
public class CompanyController {
	@Autowired
	CompanyService companyService;
	
	// Update logo công ty, trả về client dưới dạng json
	@ResponseBody
	@PostMapping("/upload-company-logo")
	public MyUploadFileResponse uploadCompanyLogo(@RequestParam("file") MultipartFile multipartFile, @RequestParam("email") String email) {
		try {
			String logoPath = companyService.uploadCompanyLogo(multipartFile, email);
			return new MyUploadFileResponse(logoPath);
		} catch (Exception e) {
			e.printStackTrace();
			return new MyUploadFileResponse("ERROR");
		}
	}
	
	// Update thông tin company
	@PostMapping("/update-company")
	public String updateCompany(@ModelAttribute("companyInformation") CompanyDTO companyDTO,
								@RequestParam("userId") int userId) {
		companyService.updateCompany(companyDTO);
		return "redirect:/user/profile" + userId;
	}
	
	// Xem chi tiết một company
	@GetMapping("/detail/{companyId}")
	public String detailRecruitment(@PathVariable("companyId") int companyId, Model model) {
		Company company = companyService.getCompanyById(companyId);
		model.addAttribute("company", company);
		
		return "public/detail-company";
	}
	
	// Xem các bài đăng của một công ty
	@GetMapping("/post-list/{companyId}")
	public String allCompanyPostList(@PathVariable("companyId") int companyId, 
									@RequestParam(value = "page", required = false, defaultValue = "0") int page,
									Model model) {
		// Phân trang cho kết quả
		Page<RecruitmentDTO> recruitmentsDTO = companyService.getCompanyPostList(companyId, page);
		// Chuyển thành companyDTO để truyền vào view
		CompanyDTO companyDTO = new CompanyDTO(companyService.getCompanyById(companyId));
		model.addAttribute("list", recruitmentsDTO);
		model.addAttribute("company", companyDTO);
		model.addAttribute("numberPage", page);
		
		return "public/post-company";
	}
}
