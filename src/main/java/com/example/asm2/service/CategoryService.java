package com.example.asm2.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.example.asm2.entity.Category;
import com.example.asm2.repository.CategoryRepository;

@Service
@Transactional
public class CategoryService {
	@Autowired
	private CategoryRepository categoryRepository;
	
	public List<Category> getAllCategories() {
		return categoryRepository.findAll();
	}
	
	// Lấy 4 danh mục top đầu
	public List<Category> getTopCategories() {
		List<Category> categories = categoryRepository.findTopCategories(PageRequest.of(0, 4));
		return categories;
				
	}
}
