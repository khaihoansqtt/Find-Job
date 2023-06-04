package com.example.asm2.customEvent;

import java.util.Locale;

import org.springframework.context.ApplicationEvent;

import com.example.asm2.entity.User;

import lombok.Getter;
import lombok.Setter;

// Tạo một sự kiện OnRegistrationCompleteEvent để xác thực bằng email
@Getter
@Setter
public class OnRegistrationCompleteEvent extends ApplicationEvent {
	 private String appUrl;
	    private User user;

	    public OnRegistrationCompleteEvent(User user, String appUrl) {
	        super(user);
	        this.user = user;
	        this.appUrl = appUrl;
	    }
}
