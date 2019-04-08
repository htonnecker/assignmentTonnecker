package com.redhat.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "FREELANCER", uniqueConstraints = @UniqueConstraint(columnNames = "freelancerId"))
public class Freelancer implements Serializable {

    private static final long serialVersionUID = -7304814269819778382L;
    
    @Id
    private String freelancerId;   

    @NotNull
    @Column(columnDefinition = "text")
    private String firstName;  

    @NotNull
    @Column(columnDefinition = "text")
    private String lastName;    

    @NotNull
    @Column(columnDefinition = "text")
    private String emailAddress;  

    @NotNull
    @Column(columnDefinition = "text")
    private String listOfSkills; 

    public String getFreelancerId() {
		return freelancerId;
	}

	public void setFreelancerId(String freelancerId) {
		this.freelancerId = freelancerId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getListOfSkills() {
		return listOfSkills;
	}

	public void setListOfSkills(String listOfSkills) {
		this.listOfSkills = listOfSkills;
	}
}
