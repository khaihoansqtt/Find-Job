package com.example.asm2.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.asm2.config.CustomUserDetails;
import com.example.asm2.customEvent.OnRegistrationCompleteEvent;
import com.example.asm2.customResponse.MyGeneralResponse;
import com.example.asm2.dto.CompanyDTO;
import com.example.asm2.dto.RecruitmentDTO;
import com.example.asm2.dto.UserDTO;
import com.example.asm2.entity.ApplyPost;
import com.example.asm2.entity.User;
import com.example.asm2.service.CategoryService;
import com.example.asm2.service.CompanyService;
import com.example.asm2.service.RecruitmentService;
import com.example.asm2.service.UserService;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserService userService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private CategoryService categoryService;
	@Autowired
	private RecruitmentService recruitmentService;
	@Autowired
	ApplicationEventPublisher eventPublisher;

	// Hiển thị trang thông tin user (trang profile)
	@GetMapping("/profile{userId}")
	public String userDetail(Authentication authentication, Model model) {
		int userId = getUserId(authentication);
		UserDTO userDTO = userService.getUserById(userId);	// Lấy user và truyền vào view
		model.addAttribute("userInformation", userDTO);

		// Nếu là employer và đã có đối tượng công ty thì get company vào truyền vào view
		if (userDTO.getRoleId() == 2) {	
				model.addAttribute("companyInformation", userDTO.getCompany());
		}
		return "public/profile";
	}
	
	// Cập nhật thông tin user
	@PostMapping("/update-profile")
	public String updateUserProfile(@ModelAttribute("userInformation") UserDTO userDTO) {
		int userId = userService.updateUserProfile(userDTO);
		System.out.println("Update user successfully");
		return "redirect:/user/profile" + userId;
	}
	
	// Xem danh sách các bài đăng tuyển của một user (EMPLOYER)
	@GetMapping("/list-post")
	public String getCruitmentsByUserId(@RequestParam(name = "page", required = false, defaultValue = "0") int pageNumber, 
										Authentication authentication, Model model) {
		int userId = getUserId(authentication);
		// Get các recruitments dựa theo userId của phiên và theo số trang
		Page<RecruitmentDTO> recruitments = recruitmentService.getCruitmentsByUserId(userId, pageNumber);
		model.addAttribute("recruitmentPage", recruitments);
		return "public/post-list";
	}
	
	// Cập nhật ảnh đại diện của user, trả về json chứa đường dẫn ảnhh
	@ResponseBody
	@PostMapping("/upload-avatar")
	public MyGeneralResponse uploadAvatar(@RequestParam("file") MultipartFile multipartFile, Authentication authentication) {
		int userId = getUserId(authentication);
		
		try {
			String avatarPath = userService.uploadAvatar(multipartFile, userId);
			return new MyGeneralResponse(avatarPath);
		} catch (Exception e) {
			e.printStackTrace();
			return new MyGeneralResponse("error");
		}
	}
	
	// Cập nhật cv của user
	@ResponseBody
	@PostMapping("/upload-cv")
	public MyGeneralResponse uploadCv(@RequestParam("file") MultipartFile multipartFile, Authentication authentication) {
		int userId = getUserId(authentication);
		
		try {
			String cvName = userService.uploadCv(multipartFile, userId);
			return new MyGeneralResponse(cvName);
		} catch (Exception e) {
			e.printStackTrace();
			return new MyGeneralResponse("error");
		}
	}
	
	// Xóa cv của user
	@PostMapping("/delete-cv")
	public String deleteCV(Authentication authentication) {
		int userId = getUserId(authentication);
		userService.deleteCV(userId);
		return "redirect:/user/profile" + userId;
	}
	
	// Xử lý khi một user ứng tuyển và dùng cv trong hồ sơ user
	@ResponseBody
	@PostMapping("/apply-job1")
	public MyGeneralResponse applyJob1(@RequestParam("idRe") int recruitmentId,
							@RequestParam("text") String text,
							Authentication authentication) {
		if (authentication == null) {	// chưa xác thực thì trả về "false"
			return new MyGeneralResponse("false");
		}
		int userId = getUserId(authentication);
		
		// responseData trả về đã ứng tuyển trước đó rồi, hoặc vừa ứng tuyển thành công
		String responseData = userService.applyJob(recruitmentId, userId, text);
		return new MyGeneralResponse(responseData);
	}
	
	// Xử lý khi một user ứng tuyển và upload cv mới
	@ResponseBody
	@PostMapping("/apply-job")
	public MyGeneralResponse applyJobUploadCv(@RequestParam("idRe") int recruitmentId,
											@RequestParam("file") MultipartFile multipartFile,
											@RequestParam("text") String text,
											Authentication authentication) {
		if (authentication == null) {
			return new MyGeneralResponse("false");
		}
		int userId = getUserId(authentication);
		try {
			String responseData = userService.applyJobUploadCv(recruitmentId, userId, text, multipartFile);
			return new MyGeneralResponse(responseData);
		} catch (IOException e) {
			return new MyGeneralResponse("error");
		}
	}
	
	// Xử lý khi user lưu lại một công việc để tiện theo dõi
	@ResponseBody
	@PostMapping("/save-job")
	public MyGeneralResponse saveJob(@RequestParam("idRe") int recruitmentId,
										Authentication authentication) {
		if (authentication == null) {
			return new MyGeneralResponse("false");
		}
		int userId = getUserId(authentication);
		String responseData = userService.saveJob(recruitmentId, userId);
		return new MyGeneralResponse(responseData);
	}

	// Xử lý khi user follow một công ty để tiện theo dõi
	@ResponseBody
	@PostMapping("/follow-company")
	public MyGeneralResponse followCompany(@RequestParam("companyId") int companyId, Authentication authentication) {
		if (authentication == null) {
			return new MyGeneralResponse("false");
		}
		int userId = getUserId(authentication);
		String responseData = userService.followCompany(userId, companyId);
		return new MyGeneralResponse(responseData);
	}

	// Lấy danh sách các công việc đã lưu của user
	@GetMapping("/get-list-save-job")
	public String getListSaveJob(@RequestParam(value = "page", required = false, defaultValue = "0") int page,
								Authentication authentication, Model model) {
		int userId = getUserId(authentication);
		Page<RecruitmentDTO> saveJobList = userService.getSaveJobList(userId, page);
		model.addAttribute("saveJobList", saveJobList);
		model.addAttribute("numberPage", page);
		
		return "public/list-save-job";
	}

	// Lấy danh sách các công ty đã theo dõi của user
	@GetMapping("/get-list-follow-company")
	public String getListFollowCompany(@RequestParam(value = "page", required = false, defaultValue = "0") int page,
									Authentication authentication, Model model) {
		int userId = getUserId(authentication);
		Page<CompanyDTO> followCompanyPage = userService.getListFollowCompany(userId, page);
		model.addAttribute("saveJobList", followCompanyPage);
		model.addAttribute("numberPage", page);
		
		return "public/list-follow-company";
	}

	// Lấy danh sách các công việc đã ứng tuyển của user
	@GetMapping("/get-list-apply-job")
	public String getListApplyJob(@RequestParam(value = "page", required = false, defaultValue = "0") int page,
								Authentication authentication, Model model) {
		int userId = getUserId(authentication);
		Page<ApplyPost> applyPostPage = userService.getApplyPost(userId, page);
		model.addAttribute("saveJobList", applyPostPage);
		model.addAttribute("numberPage", page);
		
		return "public/list-apply-job";
	}
	
	// Xử lý khi người dùng ấn vào nút gửi lại mail để xác nhận tài khoản
	@PostMapping("/confirm-account")
	public String confirmAccount(@RequestParam("email") String email, HttpServletRequest request, RedirectAttributes redirectAttributes ) {
		User user = userService.getUserbyEmail(email);	// Lấy ra user
		try {
			String appUrl = request.getContextPath();
			// phát sự kiện để listen và gửi mail
	        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user, appUrl));
			
			redirectAttributes.addFlashAttribute("resend_email_ok", "");
			return "redirect:/user/profile";
		} catch (RuntimeException e) {
			e.printStackTrace();
			System.out.println("Send email failed");
			
			redirectAttributes.addFlashAttribute("resend_email_failed", "");
			return "redirect:/user/profile";
		}
	}
	
	// Lấy userId của phiên đăng nhập
	public int getUserId(Authentication authentication) {
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		return userDetails.getId();
	}
}
