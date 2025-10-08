package com.LetsWork.CRM.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
//rename it as VisitorEntry
public class Visitor extends Base{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    private String phone;

    @Email
    private String email;

    private Boolean oneDayPass = false;

    private LocalDate visitDate = LocalDate.now();
}