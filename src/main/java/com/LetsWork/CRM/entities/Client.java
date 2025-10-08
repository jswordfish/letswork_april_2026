package com.LetsWork.CRM.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "client")
public class Client extends Base{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @Email
    private String email;

    private String phone;

    private Boolean isIndividual = true;

    private String clientCompanyName;
    
    private String location;
    
    private String companyId;
}
