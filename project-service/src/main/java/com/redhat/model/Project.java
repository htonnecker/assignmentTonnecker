package com.redhat.model;

import java.io.Serializable;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@DataObject
public class Project implements Serializable {

    private static final long serialVersionUID = -6994655395272795259L;

    private String projectId;
    private String ownerFirstName;
    private String ownerLastName;
    private String ownerEmailAddress;
    private String projectTitle;
    private String projectDescription;
    // open, in_progress, completed, cancelled
    private String projectStatus;

    public Project() { }

    public Project(JsonObject json) {
    	this.projectId 			= json.getString("projectId");
    	this.ownerFirstName 	= json.getString("ownerFirstName");
    	this.ownerLastName		= json.getString("ownerLastName");
    	this.ownerEmailAddress  = json.getString("ownerEmailAddress");
    	this.projectTitle 		= json.getString("projectTitle");
    	this.projectDescription = json.getString("projectDescription");
    	this.projectStatus		= json.getString("projectStatus");
    }

    public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getOwnerFirstName() {
		return ownerFirstName;
	}

	public void setOwnerFirstName(String ownerFirstName) {
		this.ownerFirstName = ownerFirstName;
	}

	public String getOwnerLastName() {
		return ownerLastName;
	}

	public void setOwnerLastName(String ownerLastName) {
		this.ownerLastName = ownerLastName;
	}
	
	public String getOwnerEmailAddress() {
		return ownerEmailAddress;
	}

	public void setOwnerEmailAddress(String ownerEmailAddress) {
		this.ownerEmailAddress = ownerEmailAddress;
	}

	public String getProjectTitle() {
		return projectTitle;
	}

	public void setProjectTitle(String projectTitle) {
		this.projectTitle = projectTitle;
	}

	public String getProjectDescription() {
		return projectDescription;
	}

	public void setProjectDescription(String projectDescription) {
		this.projectDescription = projectDescription;
	}

	public String getProjectStatus() {
		return projectStatus;
	}

	public void setProjectStatus(String projectStatus) {
		this.projectStatus = projectStatus;
	}

	public JsonObject toJson() {
        final JsonObject json = new JsonObject();
        
        json.put("projectId", this.projectId);
        json.put("ownerFirstName", this.ownerFirstName);
        json.put("ownerLastName", this.ownerLastName);
        json.put("ownerEmailAddress", this.ownerEmailAddress);
        json.put("projectTitle", this.projectTitle);
        json.put("projectDescription", this.projectDescription);
        json.put("projectStatus", this.projectStatus);
        
        return json;
    }

}
