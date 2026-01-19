package com.letswork.crm.entities;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Referral extends Base{
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	private String email;
	
	private String name;
	
	private String phoneNumber;
	
	private String emailOfUser;
	
	private String nameOfUser;
	
	private String phoneOfUser;
	
	@Column(nullable = true)
	private LocalDate joiningDate;
	
	@Column(nullable = true)
	private Boolean receivedBonus;

}
