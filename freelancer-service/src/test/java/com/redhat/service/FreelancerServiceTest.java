package com.redhat.service;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.redhat.model.Freelancer;
import com.redhat.repository.FreelancerRepository;

/**
 * Don't work without local postgresql database installed
 * 
 * @author hagentonnecker
 */

@RunWith(SpringRunner.class)
@DataJpaTest
@TestPropertySource(locations = {"classpath:application-test.properties"})
public class FreelancerServiceTest {
	
	@Autowired
    private FreelancerRepository freelancerRepository;
	
    @Test
    public void getFreelancer() throws Exception {	    	
    	Freelancer freelancer = freelancerRepository.findOne("111111");
    
        assertThat(freelancer, notNullValue());
        assertThat(freelancer.getFreelancerId(), equalTo("111111"));
    }
    
    @Test
    public void getFreelancers() throws Exception {	
    	List<Freelancer> freelancers = freelancerRepository.findAll();
    	
        assertThat(freelancers, notNullValue());
        assertThat(freelancers.size(), equalTo(1));
        assertThat(freelancers.get(0).getFreelancerId(), equalTo("111111"));
    }
}
