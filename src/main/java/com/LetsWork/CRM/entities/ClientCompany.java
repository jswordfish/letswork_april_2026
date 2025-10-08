package com.LetsWork.CRM.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientCompany extends Base{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String clientCompanyName;

    //private Integer totalEmployees;

    private String industry;
    
    private String location;
    
    private String companyId;

//    @OneToMany(mappedBy = "clientCompany", cascade = CascadeType.ALL)
//    private List<Client> employees;
}