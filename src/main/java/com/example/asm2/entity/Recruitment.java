package com.example.asm2.entity;


import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "recruitments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Recruitment {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column(name = "title")
    private String title;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "experience")
    private String experience;
    
    @Column(name = "quantity")
    private int quantity;
    
    @Column(name = "address")
    private String address; 
    
    @Column(name = "deadline")
    @Temporal(TemporalType.DATE)
    private Date deadline;
    
    @Column(name = "salary")
    private String salary;
    
    @Column(name = "type")
    private String type;
    
    @Column(name = "r_rank")
    private String rank;
    
    @Column(name = "status")
    private int status;
    
    @Column(name = "view")
    private int view;
    
    @Column(name = "created_at")
    @Temporal(TemporalType.DATE)
    @CreationTimestamp
    private Date createdAt;
    
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "category_id")
    private Category category;
    
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "company_id")
    private Company company;
	
	@OneToMany(mappedBy = "recruitment",
				fetch = FetchType.LAZY,
				cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
	private List<ApplyPost> applyPosts;
	
	@ManyToMany(fetch = FetchType.LAZY,
			cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
	@JoinTable(name = "save_job",
				joinColumns = @JoinColumn(name = "recruitment_id"),
				inverseJoinColumns = @JoinColumn(name = "user_id"))
	private List<User> usersSave;
}
