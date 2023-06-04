package com.example.asm2.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// Cấu hình cho spring security

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    // Tạo bean paswordEncoder tiện inject mã hóa mật khẩu dùng thuật toán bcrypt
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public AuthenticationProvider authenticationProvider() {
    	DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
    	authenticationProvider.setUserDetailsService(userDetailsService);
    	authenticationProvider.setPasswordEncoder(passwordEncoder());
    	return authenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    	return http.csrf().disable()
    		.authorizeRequests()	// Các url sau không cần login, còn lại phải cần login
    			.antMatchers("/","/auth/register", "/search/**", "/user/apply-job1/", "/user/apply-job/", "/user/save-job/",
    						"/user/follow-company/", "/recruitment/detail/**", "/company/detail/**", "/upload/**",
    						"/regitrationConfirm/**" ,"/assets/**").permitAll()
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/processLogin")
                .defaultSuccessUrl("/?login=true")
                .failureUrl("/login?error=true")
                .usernameParameter("email")		// khi process login, requestparam là "email" dành cho username
                .passwordParameter("password")	//  và "password" dành cho password
                .permitAll()					
                .and()
            .logout()
                .permitAll()
                .and()	
    		.exceptionHandling().accessDeniedPage("/403")	// cài đặt đường dẫn trang access denied
             
                .and().build();
    }
}