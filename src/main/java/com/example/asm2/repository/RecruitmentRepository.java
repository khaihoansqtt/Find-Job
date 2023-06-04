package com.example.asm2.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.asm2.entity.Company;
import com.example.asm2.entity.Recruitment;

public interface RecruitmentRepository extends JpaRepository<Recruitment, Integer> {
	// Tìm các recruitment của một công ty
	@Query(value = "select * from recruitments where company_id = :companyId", nativeQuery = true)
	Page<Recruitment> findByCompanyId(@Param("companyId") int companyId, Pageable pageable);
	
	// Tìm kiếm các recruitment theo từ khóa
	@Query(value = "select * from recruitments where title like %:keySearch%", nativeQuery = true)
	Page<Recruitment> searchByTitle(@Param("keySearch") String keySearch, Pageable pageable);

	// Tìm kiếm các recruitment theo address của recruitment
	@Query(value = "select * from recruitments where address like %:keySearch%", nativeQuery = true)
	Page<Recruitment> searchByAddress(@Param("keySearch") String keySearch, Pageable pageable);

	// Tìm kiếm các recruitment của một mảng các id công ty
	@Query(value = "select * from recruitments where company_id in ?1", nativeQuery = true)
	Page<Recruitment> searchByCompanyIds(List<Integer> ids, Pageable pageable);
	
	// Tìm kiếm các recruitment đã lưu của một user
	@Query("select r from Recruitment r join r.usersSave u where u.id= :userId")
	Page<Recruitment> findAllByUserId(int userId, Pageable page);

	// Tìm kiếm các recruitment của một công ty
	@Query("select r from Recruitment r where r.company.id = :companyId")
	Page<Recruitment> findbyCompanyId(@Param("companyId") int companyId, Pageable pageable);
	
	// Lấy các công việc có nhiều lượt ứng tuyển nhất
	@Query("select r, count(ap.user.id) as numberApply from Recruitment r"
			+ " join r.applyPosts ap group by r.id order by numberApply desc")
	List<Recruitment> findTopRecruitments(Pageable pageable);
}
