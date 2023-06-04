package com.example.asm2.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.example.asm2.dto.CompanyDTO;
import com.example.asm2.dto.RecruitmentDTO;
import com.example.asm2.dto.UserDTO;
import com.example.asm2.entity.ApplyPost;
import com.example.asm2.entity.Company;
import com.example.asm2.entity.Cv;
import com.example.asm2.entity.Recruitment;
import com.example.asm2.entity.Role;
import com.example.asm2.entity.User;
import com.example.asm2.entity.VerificationToken;
import com.example.asm2.repository.ApplyPostRepository;
import com.example.asm2.repository.CompanyRepository;
import com.example.asm2.repository.CvRepository;
import com.example.asm2.repository.RecruitmentRepository;
import com.example.asm2.repository.RoleRepository;
import com.example.asm2.repository.UserRepository;
import com.example.asm2.repository.VerificationTokenRepository;
import com.example.asm2.utils.FileUploadUtil;

@Service
@Transactional
public class UserService {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private CompanyRepository companyRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private CvRepository cvRepository;
	@Autowired
	private RecruitmentRepository recruitmentRepository;
	@Autowired
	private ApplyPostRepository applyPostRepository;
	@Autowired
	private VerificationTokenRepository tokenRepository;

	public UserDTO getUserById(int userId) {
		Optional<User> user = userRepository.findById(userId);
		if (user.isPresent()) {
			return new UserDTO(user.get());
		} else
			return null;
	}

	public UserDTO getUserDTObyEmail(String email) {
		Optional<User> user = userRepository.findByEmail(email);
		if (user.isPresent()) {
			return new UserDTO(user.get());
		} else
			return null;
	}

	public User getUserbyEmail(String email) {
		Optional<User> result = userRepository.findByEmail(email);
		if (result.isPresent()) {
			return result.get();
		} else
			return null;
	}

	// tạo mới user
	public User addNewUser(UserDTO userDTO) {
		User user = new User(userDTO);
		user.setPassword(passwordEncoder.encode(user.getPassword())); // Mã hóa mk
		user.setStatus(1);

		Optional<Role> result = roleRepository.findById(userDTO.getRoleId()); // Lấy role tương ứng roleId
		if (result.isPresent()) {
			Role role = result.get();
			user.setRole(role);
			user.setStatus(1);
			
			if (userDTO.getRoleId() == 2) { // Nếu user là EMPLOYER thì set một new company cho nó
				Company company = new Company();
				company.setStatus(1);		
				company.setUser(user);

				user.setStatus(0);	// Nếu user là EMPLOYER thì set status = 0, khi xác thực bằng email thành công
				companyRepository.save(company);											// mới set status = 1
			}
			userRepository.save(user);
		}
		return user;
	}

	// Cập nhật thông tin user
	public int updateUserProfile(UserDTO userDTO) {

		User findedUser = userRepository.findByEmail(userDTO.getEmail()).get();
		findedUser.setEmail(userDTO.getEmail());
		findedUser.setFullName(userDTO.getFullName());
		findedUser.setAddress(userDTO.getAddress());
		findedUser.setPhoneNumber(userDTO.getPhoneNumber());
		findedUser.setDescription(userDTO.getDescription());

		userRepository.save(findedUser);
		return findedUser.getId();
	}

	// cập nhật ảnh avatar của user
	public String uploadAvatar(MultipartFile multipartFile, int userId) throws IOException {

		String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
		String uploadDir = "src/main/resources/static/upload/user" + userId + "/avatar";
		FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		String avatarPath = "/upload/user" + userId + "/avatar/" + fileName;

		User user = userRepository.findById(userId).get();
		user.setImage(avatarPath);
		return avatarPath;
	}

	// upload cv của user, cv lưu tại: "src/main/resources/static/upload/user" + userId + "/cv"
	public String uploadCv(MultipartFile multipartFile, int userId) throws IOException {

		String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
		String uploadDir = "src/main/resources/static/upload/user" + userId + "/cv";
		FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

		User user = userRepository.findById(userId).get();
		if (user.getCv() == null) {		// Nếu lần đầu upload thì tạo mới một object cv
			Cv cv = new Cv();
			cv.setFileName(fileName);
			user.setCv(cv);
		} else {
			user.getCv().setFileName(fileName);		// Nếu cập nhật cv thì get cv ra rồi set lại file name
		}
		userRepository.save(user);
		return fileName;
	}

	// xóa cv
	public void deleteCV(int userId) {
		User user = userRepository.findById(userId).get();
		Cv cv = user.getCv();

		user.setCv(null);
		cvRepository.delete(cv);
		userRepository.save(user);

	}

	// xử lý apply job không upload kèm cv
	public String applyJob(int recruitmentId, int userId, String text) {
		User user = userRepository.findById(userId).get();

		List<ApplyPost> applyPosts = applyPostRepository.findByUser(user);	// lấy các apply post của user

		boolean isApplied = false;		
		for (ApplyPost applyPost : applyPosts) {
			if (applyPost.getRecruitment().getId() == recruitmentId) {
				isApplied = true;	// nếu đã ứng tuyển trước đó rồi thì = true
			}
		}
		
		if (isApplied)	// nếu đã ứng tuyển trước đó rồi thì trả về "applied"
			return "applied";
		
		// còn nếu chưa ứng tuyển thì tạo mới apply post và lưu
		Recruitment recruitment = recruitmentRepository.findById(recruitmentId).get();

		ApplyPost applyPost = new ApplyPost();
		applyPost.setText(text);
		applyPost.setStatus(0);
		applyPost.setUser(user);
		applyPost.setRecruitment(recruitment);

		applyPostRepository.save(applyPost);
		return "true";
	}
	
	// apply vào một công việc kèm theo upload cv
	// cv lưu tại /upload/recruitment{id}/
	public String applyJobUploadCv(int recruitmentId, int userId, String text, MultipartFile multipartFile)
			throws IOException {
		User user = userRepository.findById(userId).get();

		List<ApplyPost> applyPosts = applyPostRepository.findByUser(user);

		boolean isApplied = false;
		for (ApplyPost applyPost : applyPosts) {
			if (applyPost.getRecruitment().getId() == recruitmentId) {
				isApplied = true;
			}
		}
		if (isApplied)
			return "applied";

		String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
		String uploadDir = "src/main/resources/static/upload/recruitment" + recruitmentId;
		FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

		Recruitment recruitment = recruitmentRepository.findById(recruitmentId).get();

		ApplyPost applyPost = new ApplyPost();
		applyPost.setText(text);
		applyPost.setStatus(0);
		applyPost.setNameCv(fileName);
		applyPost.setUser(user);
		applyPost.setRecruitment(recruitment);

		applyPostRepository.save(applyPost);
		return "true";
	}

	// Xử lý lưu lại một công việc để tiện theo dõi
	public String saveJob(int recruitmentId, int userId) {
		User user = userRepository.findById(userId).get();
		List<Recruitment> savedRecruitments = user.getSavedRecruitments();

		for (Recruitment recruitment : savedRecruitments) {
			if (recruitment.getId() == recruitmentId) {
				return "saved";
			}
		}
		Recruitment recruitment = recruitmentRepository.findById(recruitmentId).get();
		user.getSavedRecruitments().add(recruitment);
		userRepository.save(user);

		return "true";
	}

	// Xử lý follow một công ty để tiện theo dõi
	public String followCompany(int userId, int companyId) {
		User user = userRepository.findById(userId).get();
		List<Company> savedCompanies = user.getFollowedCompanies();

		for (Company company : savedCompanies) {
			if (company.getId() == companyId) {
				return "saved";		// Nếu đã follow thì trả về "saved"
			}
		}

		Company company = companyRepository.findById(companyId).get();
		user.getFollowedCompanies().add(company);
		userRepository.save(user);

		return "true";	// trả về "true" thể hiện là đã follow thành công
	}

	// Lấy danh sách các job đã lưu
	public Page<RecruitmentDTO> getSaveJobList(int userId, int page) {
		Pageable pageable = PageRequest.of(page, 3);
		Page<Recruitment> recruitmentPage = recruitmentRepository.findAllByUserId(userId, pageable);
		return recruitmentPage.map(recruitment -> new RecruitmentDTO(recruitment));
	}

	// Lấy danh sách company đã theo dõi
	public Page<CompanyDTO> getListFollowCompany(int userId, int page) {
		Pageable pageable = PageRequest.of(page, 3);
		Page<Company> companyPage = companyRepository.findAllByUserId(userId, pageable);
		return companyPage.map(company -> new CompanyDTO(company));
	}

	// lấy danh sách các công việc đã ứng tuyển
	public Page<ApplyPost> getApplyPost(int userId, int page) {
		User user = userRepository.findById(userId).get();
		Pageable pageable = PageRequest.of(page, 3);
		Page<ApplyPost> applyPostPage = applyPostRepository.findAllByUser(user, pageable);
		return applyPostPage;
	}

	// Lấy user thì đối tượng verificationToken
	public User getUser(String verificationToken) {
		User user = tokenRepository.findByToken(verificationToken).getUser();
		return user;
	}

	// Lấy đối tượng verificationToken từ chuỗi token trên url
	public VerificationToken getVerificationToken(String VerificationToken) {
		return tokenRepository.findByToken(VerificationToken);
	}

	// Save một user
	public void saveRegisteredUser(User user) {
		userRepository.save(user);
	}

	// tạo một đối tượng verificationToken và lưu vào trong database
	public void createVerificationToken(User user, String token) {
		VerificationToken myToken = new VerificationToken(user, token);
		tokenRepository.save(myToken);
	}
}
