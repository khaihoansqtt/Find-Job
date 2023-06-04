package com.example.asm2.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.asm2.entity.Company;
import com.example.asm2.entity.User;



public interface CompanyRepository extends JpaRepository<Company, Integer> {
	Optional<Company> findByUser(User user);
	
	// Tìm kiếm công ty theo tên
	@Query(value = "select * from companies where name like %?1%", nativeQuery = true)
	List<Company> searchByName(String keyString);
	
	// Tìm kiếm công ty đã follow của một user
	@Query("select c from Company c join c.usersFollow u where u.id = :userId")
	Page<Company> findAllByUserId(@Param("userId") int userId, Pageable page);
	
	// Lấy ra các công ty có lượt apply nhiều nhất
	@Query("select c, count(ap.user.id) as numberApply from Company c join c.recruitments r"
			+ " join ApplyPost ap on r.id = ap.recruitment.id"
			+ " group by c.id order by numberApply desc")
	List<Company> findTopCompanies(Pageable pageable);
}
