package com.example.asm2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.asm2.entity.Cv;
import com.example.asm2.repository.CvRepository;

@Service
@Transactional
public class CvService {
	@Autowired
	private CvRepository cvRepository;
	
	public Cv getCv(int cvId) {
		return cvRepository.findById(cvId).get();
	}
}
