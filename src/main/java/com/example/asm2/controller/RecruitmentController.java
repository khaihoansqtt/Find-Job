package com.example.asm2.controller;

import java.util.List;

import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.asm2.config.CustomUserDetails;
import com.example.asm2.dto.RecruitmentDTO;
import com.example.asm2.entity.ApplyPost;
import com.example.asm2.entity.Category;
import com.example.asm2.service.CategoryService;
import com.example.asm2.service.RecruitmentService;

@Controller
@RequestMapping("/recruitment")
public class RecruitmentController {
	@Autowired
	private CategoryService categoryService;
	@Autowired
	private RecruitmentService recruitmentService;
	
	// Hiển thị trang đăng việc làm (tạo mới một recruitment)
	@GetMapping("/post")
	public String showPostRecruitmentPage(Model model) {
		RecruitmentDTO recruitmentDTO = new RecruitmentDTO();
		List<Category> categories = categoryService.getAllCategories();
		model.addAttribute("recruitment", recruitmentDTO);
		model.addAttribute("categories", categories);
		return "public/post-job";
	}
	
	// Xử lý tạo mới một recruitment
	@PostMapping("/add")
	public String postNewRecruitment(Authentication authentication, 
									@ModelAttribute("recruitment") RecruitmentDTO recruitmentDTO) {
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		int userId = userDetails.getId();
		recruitmentService.addNewRecruitment(userId, recruitmentDTO);
		return "redirect:/user/list-post";
	}
	
	// Xem chi tiết một công việc
	@GetMapping("/detail/{recruitmentId}")
	public String detailRecruitment(@PathVariable("recruitmentId") int recruitmentId, Model model) {
		RecruitmentDTO recruitmentDTO = recruitmentService.getRecruitmentById(recruitmentId);
		List<ApplyPost> applyPosts = recruitmentService.getApplyPosts(recruitmentId);
		model.addAttribute("recruitment", recruitmentDTO);
		model.addAttribute("applyPosts", applyPosts);	// Để view hiển thị các lượt apply vào công việc đó
		
		return "public/detail-post";
	}
	
	// Hiển thị trang update công việc
	@GetMapping("/editpost/{recruitmentId}")
	public String ShowUpdateRecruitmentPage(@PathVariable("recruitmentId") int recruitmentId, Model model) {
		RecruitmentDTO recruitmentDTO = recruitmentService.getRecruitmentById(recruitmentId);
		List<Category> categories = categoryService.getAllCategories();
		model.addAttribute("recruitment", recruitmentDTO);
		// truyền categories vào để load động các danh mục trong database vào select - option trong html
		model.addAttribute("categories", categories);	
		return "public/edit-job";
	}
	
	// Xử lý cập nhật công việc
	@PostMapping("/edit")
	public String updateRecruitment(@ModelAttribute("recruitment") RecruitmentDTO recruitmentDTO, RedirectAttributes redirectAttributes) {
		recruitmentService.updateRecruitment(recruitmentDTO);
		redirectAttributes.addFlashAttribute("success","");
		return "redirect:/recruitment/editpost/" + recruitmentDTO.getId();
	}
	
	// Xóa một công việc
	@PostMapping("/delete")
	public String deleteRecruitment(@RequestParam("recruitmentId") int recruitmentId, RedirectAttributes redirectAttributes) {
		recruitmentService.deleteRecruitment(recruitmentId);
		redirectAttributes.addFlashAttribute("success","");
		return "redirect:/user/list-post";
	}
	
}
