package com.example.asm2.controller;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.asm2.customEvent.OnRegistrationCompleteEvent;
import com.example.asm2.dto.RecruitmentDTO;
import com.example.asm2.dto.UserDTO;
import com.example.asm2.entity.Category;
import com.example.asm2.entity.Company;
import com.example.asm2.entity.Recruitment;
import com.example.asm2.entity.User;
import com.example.asm2.entity.VerificationToken;
import com.example.asm2.service.CategoryService;
import com.example.asm2.service.CompanyService;
import com.example.asm2.service.RecruitmentService;
import com.example.asm2.service.UserService;

@Controller
public class HomeController {
	
	@Autowired
	UserService userService;
	@Autowired
	RecruitmentService  recruitmentService;
	@Autowired
	CategoryService categoryService;
	@Autowired
	CompanyService companyService;
	@Autowired
	ApplicationEventPublisher eventPublisher;
	
	// Trang chủ website
	@GetMapping("/")
	public String home(Model model) {
		List<Category> categories = categoryService.getTopCategories();	// Lấy top danh mục
		List<Company> companies = companyService.getTopCompanies();		// Lấy top công ty
		List<Recruitment> recruitments = recruitmentService.getTopRecruitments();	// Lấy top công việc
		model.addAttribute("categories", categories);
		model.addAttribute("companies", companies);
		model.addAttribute("recruitments", recruitments);
		return "public/home";
	}
	
	// Trang đăng nhập, đăng ký
	@GetMapping("/login")
	public String login(Model model) {
		UserDTO newUser = new UserDTO();
		model.addAttribute("newUser", newUser);	// Truyền một đối tượng mới qua view để dùng cho chức năng tạo mới user
		return "public/login";
	}
	
	// Xử lý đăng ký user
	@PostMapping("/auth/register")
	public String register(@ModelAttribute("newUser") UserDTO userDTO,HttpServletRequest request ,RedirectAttributes redirectAttributes) {
		// Kiểm tra nhập lại mật khẩu có đúng không
		if (userDTO.getPassword().equals(userDTO.getRePassword())) {
			User user = userService.addNewUser(userDTO);
			// Nếu là EMPLOYER thì phải xác thực qua mail
			if (userDTO.getRoleId() == 2) {
				try {
					String appUrl = request.getContextPath();
					// Phát một sự kiện OnRegistrationCompleteEvent có thông tin của user và contextPath của server
			        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user, appUrl));
				} catch (RuntimeException e) {
					e.printStackTrace();
					System.out.println("Send email failed");
				}
			}
			// Truyền vào view message tạo user thành công
			redirectAttributes.addFlashAttribute("msg_register_success", "");
			return "redirect:/login";
		} else {
			redirectAttributes.addFlashAttribute("notMatchRePassword", "");	//Nhập lại mk sai thì về trang login báo lỗi
			return "redirect:/login";
		}
	}
	
	// Xử lý url xác thực từ email
	@GetMapping("/regitrationConfirm")
	public String confirmRegistration (RedirectAttributes redirectAttributes, @RequestParam("token") String token) {
	    // Lấy ra đối tượng verificationToken bằng token
	    VerificationToken verificationToken = userService.getVerificationToken(token);
	    // Nếu bị mất token thì thông báo cho view
	    if (verificationToken == null) {
	        redirectAttributes.addFlashAttribute("msg_register_error_token_null", "");
	        return "redirect:/login";
	    }
	    
	    User user = verificationToken.getUser();	// Lấy ra user từ verificationToken
	    Calendar cal = Calendar.getInstance();
	    
	    // Nếu token hết hạn thì thông báo cho view
	    if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
	        redirectAttributes.addFlashAttribute("msg_register_error_token_expire", "");
	        return "redirect:/login";
	    }
	    // Set lại trạng thái = 1 cho user
	    user.setStatus(1); 
	    userService.saveRegisteredUser(user);
		redirectAttributes.addFlashAttribute("confirm_email_success", "");	// Thông báo cho view xác thực email thành công
	    return "redirect:/";
	}
	
	// Trang access denied
	@GetMapping("/403")
	public String accessDenied() {
		return "errors/access-denied";
	}
	
	// Tìm kiếm công việc theo tên công việc
	@RequestMapping("/search/recruitment")
	public String searchRecruitment(@RequestParam("keySearch") String keySearch, 
									@RequestParam(value = "page", required = false, defaultValue = "0") int page,
									Model model) {
		Page<RecruitmentDTO> recruitmentsDTOPage = recruitmentService.searchByTitle(keySearch, page);
		
		model.addAttribute("keySearch", keySearch);
		model.addAttribute("list", recruitmentsDTOPage);
		model.addAttribute("numberPage", page);		// Truyền page qua để xử lý logic về phân trang
		model.addAttribute("searchType", "recruitment");// Truyền searchType để xử lý url khi nhấn các nút phân trang trong view
		return "public/result-search";
	}

	// Tìm kiếm công việc theo tên công ty
	@RequestMapping("/search/company")
	public String searchCompany(@RequestParam("keySearch") String keySearch, 
								@RequestParam(value = "page", required = false, defaultValue = "0") int page,
								Model model) {
		Page<RecruitmentDTO> recruitmentsDTOPage = recruitmentService.searchByCompanyName(keySearch, page);

		model.addAttribute("keySearch", keySearch);
		model.addAttribute("list", recruitmentsDTOPage);
		model.addAttribute("numberPage", page);
		model.addAttribute("searchType", "company");
		return "public/result-search";
	}

	// Tìm kiếm công việc theo địa chỉ của công việc
	@RequestMapping("/search/address")
	public String searchAddress(@RequestParam("keySearch") String keySearch, 
								@RequestParam(value = "page", required = false, defaultValue = "0") int page,
								Model model) {
		Page<RecruitmentDTO> recruitmentsDTOPage = recruitmentService.searchByAddress(keySearch, page);

		model.addAttribute("keySearch", keySearch);
		model.addAttribute("list", recruitmentsDTOPage);
		model.addAttribute("numberPage", page);
		model.addAttribute("searchType", "address");
		
		return "public/result-search";
	}
}
