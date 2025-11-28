package com.letswork.crm.entities;

import java.beans.Transient;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.poiji.annotation.ExcelCellName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@SuperBuilder

@NoArgsConstructor
@AllArgsConstructor
public class User extends Base{
	@ExcelCellName(value = "First Name")
	String firstName;
	
	@ExcelCellName(value = "Last Name")
	String lastName;
	
	@ExcelCellName(value = "Role or Designation")
	
	String roleOrDesig;
	
	@ExcelCellName(value = "LetsWorkCentre")
	String letsWorkCentre;
	
	@ExcelCellName(value = "Department")
	String department;
	
	@ExcelCellName(value = "Email")
	String email;
	
	@ExcelCellName(value = "Emp Id")
	String empId;
	
	@ExcelCellName(value = "Password")
	String password;
	
	@ExcelCellName(value = "City")
	String city;
	
	@ExcelCellName(value = "State")
	String state;
	
	@JsonIgnore
	@ManyToOne
	OrgHierarchy orgHierarchy;
	
	@Builder.Default
	Boolean external = false;
	//String companyId;
	
	

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

	public String getLetsWorkCentre() {
		return letsWorkCentre;
	}

	public void setLetsWorkCentre(String letsWorkCentre) {
		this.letsWorkCentre = letsWorkCentre;
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

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	
	
}
