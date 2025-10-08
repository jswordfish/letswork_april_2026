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
public class Location extends Base{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Location name is required")
    private String name;

    private Integer totalSeats;

    private Integer totalConferenceRooms;

    private String address;

//    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL)
//    private List<ConferenceRoom> conferenceRooms;
}
