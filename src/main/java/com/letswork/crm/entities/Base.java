package com.letswork.crm.entities;

import java.util.Date;
import java.util.Objects;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import com.poiji.annotation.ExcelCellName;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
@MappedSuperclass
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Base {
	 @Id
	    @GeneratedValue(strategy = GenerationType.AUTO)
	    protected Long id;
	 
	
	private String companyDescription;
	private Integer version;
	
	Date createDate;
	Date updateDate;
	
	String createdBy;
	
	String updatedBy;
	
	
	String deletedBy;
	
	@ExcelCellName(value = "Company Id")
	String companyId;
	
	String companyName;
	
	
	public String getCompanyDescription() {
		return companyDescription;
	}
	public void setCompanyDescription(String companyDescription) {
		this.companyDescription = companyDescription;
	}
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public Date getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getUpdatedBy() {
		return updatedBy;
	}
	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	
	
	public String getDeletedBy() {
		return deletedBy;
	}
	public void setDeletedBy(String deletedBy) {
		this.deletedBy = deletedBy;
	}
	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Base other = (Base) obj;
		return Objects.equals(id, other.id);
	}
	public String getCompanyId() {
//		if(this.companyId == null && this.companyId.trim().length() == 0) {
//			return company();
//		}
		return companyId;
	}
	public void setCompanyId(String compamyId) {
		this.companyId = compamyId;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	
	
	private String company() {
		return "AB_123";
	}
	

}
