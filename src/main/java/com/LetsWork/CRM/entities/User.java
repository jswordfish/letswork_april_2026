package com.LetsWork.CRM.entities;

import java.beans.Transient;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.poiji.annotation.ExcelCellName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder

@NoArgsConstructor
@AllArgsConstructor
public class User extends Base{
	@ExcelCellName(value = "First Name")
	String firstName;
	
	@ExcelCellName(value = "Last Name")
	String lastName;
	
	@ExcelCellName(value = "Role or Designation")
	
	String roleOrDesig;
	
	@ExcelCellName(value = "Location")
	String location;
	
	@ExcelCellName(value = "Department")
	String department;
	
	@ExcelCellName(value = "Email")
	String email;
	
	@ExcelCellName(value = "Emp Id")
	String empId;
	
	@ExcelCellName(value = "Password")
	String password;
	
	@JsonIgnore
	@ManyToOne
	OrgHierarchy orgHierarchy;
	
	@Builder.Default
	Boolean external = false;
	//String companyId;
	
	@ExcelCellName(value = "Is Reviewer")
	@Builder.Default
	Boolean reviewer = false;
	
	Float overAllScore;

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

	public String getRoleOrDesig() {
		return roleOrDesig;
	}

	public void setRoleOrDesig(String roleOrDesig) {
		this.roleOrDesig = roleOrDesig;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public OrgHierarchy getOrgHierarchy() {
		return orgHierarchy;
	}

	public void setOrgHierarchy(OrgHierarchy orgHierarchy) {
		this.orgHierarchy = orgHierarchy;
	}
	

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}
	
	public String getCompanyId() {
		return this.companyId;
	}

	public Boolean getExternal() {
		return external;
	}

	public void setExternal(Boolean external) {
		this.external = external;
	}

	public Boolean getReviewer() {
		return reviewer;
	}

	public void setReviewer(Boolean reviewer) {
		this.reviewer = reviewer;
	}

	public Float getOverAllScore() {
		return overAllScore;
	}

	public void setOverAllScore(Float overAllScore) {
		this.overAllScore = overAllScore;
	}
	
	
}
