package com.letswork.crm.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;

import com.letswork.crm.enums.ConferenceRoomType;

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
public class ConferenceRoom extends Base{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;
    

    private Integer capacity;

    private String location;
    
//    @Enumerated(EnumType.STRING)  
//    @Column(nullable = false)
//    private ConferenceRoomType roomType;
    
    private boolean hasProjector;
    
    private boolean hasWhiteBoard;
    
    private boolean hasChargingPorts;
    
    
    
}
