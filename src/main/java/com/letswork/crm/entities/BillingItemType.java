package com.letswork.crm.entities;

import javax.persistence.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BillingItemType extends Base{
	
	String type;
	
	Float price;
	
	Integer quantity;
}
