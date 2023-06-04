package com.example.asm2.customEvent;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.example.asm2.entity.User;
import com.example.asm2.service.UserService;

// Lắng nghe sự kiện OnRegistrationCompleteEvent để gửi mail
@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {
	@Autowired
    private UserService userService;
    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(OnRegistrationCompleteEvent event) {
        User user = event.getUser();
        String token = UUID.randomUUID().toString();	// tạo chuỗi token
     	// tạo một đối tượng verificationToken để lưu xuống database, quan hệ 1-1 với user
        userService.createVerificationToken(user, token);	
        
        String recipientAddress = user.getEmail();	// địa chỉ email nhận
        String subject = "Registration Confirmation";	// chủ đề email
        String confirmationUrl 						// link xác thực
          = event.getAppUrl() + "/regitrationConfirm?token=" + token;
        
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText("Xác nhận tài khoản ở link sau: " + "\r\n" + "http://localhost:8080" + confirmationUrl);
        mailSender.send(email);
    }
}
