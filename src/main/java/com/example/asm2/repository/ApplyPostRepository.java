package com.example.asm2.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.asm2.entity.ApplyPost;
import com.example.asm2.entity.Recruitment;
import com.example.asm2.entity.User;

public interface ApplyPostRepository extends JpaRepository<ApplyPost, Integer> {
	
	List<ApplyPost> findByUser(User user);
	Page<ApplyPost> findAllByUser(User user, Pageable pageable);
	Page<ApplyPost> findAllByRecruitment(Recruitment recruitment, Pageable pageable);
	List<ApplyPost> findAllByRecruitment(Recruitment recruitment);
}
