package com.example.asm2.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QueryResult {
	private long numberUserApply;
	private long numberRecruitment;
	private String name;
	
	public QueryResult(long numberUserApply, long numberRecruitment, String name) {
		this.numberUserApply = numberUserApply;
		this.numberRecruitment = numberRecruitment;
		this.name = name;
	}
}
