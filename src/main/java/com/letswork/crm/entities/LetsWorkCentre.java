package com.letswork.crm.entities;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonFormat;

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
public class LetsWorkCentre extends Base{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "LetsWorkCentre name is required")
    private String name;

    private Integer totalConferenceRooms;

    private String address;
    
    private String state;
    
    private String city;
    
    private Boolean hasCafe;
    
    private String amenities;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "hh:mm a")
    private LocalTime startTimeRegular;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "hh:mm a")
    private LocalTime endTimeRegular;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "hh:mm a")
    private LocalTime startTimeSat;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "hh:mm a")
    private LocalTime endTimeSat;
    
    private String latitude;
    
    private String longitude;
    
    @OneToMany(
            mappedBy = "letsWorkCentre",
            cascade = CascadeType.ALL,
            orphanRemoval = true
        )
        private List<LetsWorkCentreImage> images = new ArrayList<>();

}
