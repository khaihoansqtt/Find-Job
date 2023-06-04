package com.example.asm2.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.asm2.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
	// Lấy ra các danh mục order theo lượng apply nhiều nhất, số lượng recruitment nhiều nhất
	@Query("select c, count(ap.user) as numberUserApply, count(distinct r.id) as numberRecruitment, c.name from Category c"
			+ " join c.recruitments r"
			+ " left join ApplyPost ap on r.id = ap.recruitment.id group by c.id, c.name order by numberUserApply desc, numberRecruitment desc")
	List<Category> findTopCategories(Pageable pageable);
}
