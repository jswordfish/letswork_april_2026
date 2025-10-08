package com.LetsWork.CRM.entities;

import javax.persistence.Entity;

import com.poiji.annotation.ExcelCellName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrgHierarchy extends Base{
	
	@ExcelCellName(value = "Level")
	String level;
	
	@ExcelCellName(value = "Role Or Designation")
	String roleOrDesig;
	
	@ExcelCellName(value = "Parent Role Or Designation")
	String parentRoleOrDesig;

}
