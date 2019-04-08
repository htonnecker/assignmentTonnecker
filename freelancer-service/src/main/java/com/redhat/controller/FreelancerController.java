package com.redhat.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.redhat.model.Freelancer;
import com.redhat.repository.FreelancerRepository;

@RestController
public class FreelancerController {

    @Autowired
    private FreelancerRepository freelancerRepository;
    
    @GetMapping(value = "/freelancer/{freelancerId}")
    public Freelancer getFreelancer(@PathVariable String freelancerId) {
    	return freelancerRepository.findOne(freelancerId);
    }
    
    @GetMapping(value = "/freelancers")
    public List<Freelancer> getFreelancers() {
    	return freelancerRepository.findAll();
    }
}
