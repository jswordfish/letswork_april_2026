package com.letswork.crm.entities;

import java.time.LocalDate;
import java.time.LocalTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

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
public class Visitor extends Base{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;
    
    private String nameOfUser;
    
    private String phoneOfUser;

    private String phone;

    @Email
    private String email;

    private LocalDate visitDate;
    
    private LocalTime timeOfVisit;
    
    private String emailOfVisitor;
    
    private String bookingCode;
    
    private String qrS3Path;
    
    private Integer numberOfGuests;
    
    private String letsWorkCentre;
    
    private String city;
    
    private String state;
    
    private Boolean visited;
    
}