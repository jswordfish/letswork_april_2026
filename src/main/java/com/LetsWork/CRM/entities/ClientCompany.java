package com.LetsWork.CRM.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

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